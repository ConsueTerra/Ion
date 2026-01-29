package net.horizonsend.ion.server.features.ai.spawning.spawner.scheduler

import kotlinx.coroutines.launch
import net.horizonsend.ion.common.database.Oid
import net.horizonsend.ion.common.database.schema.economy.TradeCityCaravanConfig
import net.horizonsend.ion.common.database.schema.economy.TradeCityCaravanHangarEntry
import net.horizonsend.ion.common.database.schema.economy.TradeCityCaravanLaunch
import net.horizonsend.ion.common.database.schema.nations.Territory
import net.horizonsend.ion.server.features.ai.configuration.AITemplate
import net.horizonsend.ion.server.features.ai.convoys.AIConvoyRegistry
import net.horizonsend.ion.server.features.ai.spawning.AISpawningManager
import net.horizonsend.ion.server.features.ai.spawning.spawner.AISpawner
import net.horizonsend.ion.server.features.ai.starship.StarshipPlayerSoldTemplate
import net.horizonsend.ion.server.features.ai.convoys.TraceCityCaravanRoute
import net.horizonsend.ion.server.features.ai.faction.AIFaction.Companion.MINING_GUILD
import net.horizonsend.ion.server.features.ai.module.misc.DifficultyModule
import net.horizonsend.ion.server.features.ai.spawning.ships.SpawnedShip
import net.horizonsend.ion.server.features.ai.spawning.spawner.mechanics.BagSpawner
import net.horizonsend.ion.server.features.ai.spawning.spawner.mechanics.CompositeFleetSpawner
import net.horizonsend.ion.server.features.ai.spawning.spawner.mechanics.GroupSpawner
import net.horizonsend.ion.server.features.ai.spawning.spawner.mechanics.SpawnerMechanic
import net.horizonsend.ion.server.features.ai.starship.AITemplateRegistry
import net.horizonsend.ion.server.features.ai.starship.BehaviorConfiguration
import net.horizonsend.ion.server.features.ai.util.AITarget
import org.bukkit.Location
import org.litote.kmongo.eq
import org.litote.kmongo.and
import org.slf4j.Logger
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.function.Supplier

/**
 * Scheduler for: DB-backed weekly trade-city caravans
 *
 * Will spawn in a bucketed pag of ships per trade city
 *
 * Notes:
 * - Restart recovery: on first tick (or at enable time) we respawn ships leased to active launches.
 * - Launch does not store ship ids; ships are fetched from hangar entries by leasedByLaunchId.
 */
object CaravanScheduler : SpawnerScheduler, TickedScheduler {

	@Volatile private var spawner: AISpawner? = null
	@Volatile private var didResumeActive = false

	override fun tick(logger: Logger) {
		val now = ZonedDateTime.now(ZoneOffset.UTC)

		// One-time recovery pass after restart
		if (!didResumeActive) {
			didResumeActive = true
			AISpawningManager.context.launch {
				runCatching { resumeActiveLaunches(logger) }
					.onFailure { logger.error("Failed to resume active trade-city caravans", it) }
			}
		}

		// Only evaluate schedules on the hour boundary (minute 0), same as legacy scheduler
		if (now.minute != 0) return

		tickTradeCityCaravans(logger, now)
	}

	private fun tickTradeCityCaravans(logger: Logger, now: ZonedDateTime) {
		val hour = now.hour
		val dow = now.dayOfWeek

		// Pull configs that match current schedule
		//TODO: wrap this into a helper
		val configs = TradeCityCaravanConfig.col.find(
			and(
				TradeCityCaravanConfig::enabled eq true,
				TradeCityCaravanConfig::hour eq hour,
				TradeCityCaravanConfig::dayOfWeek eq dow
			)
		).toList()

		for (cfg in configs) {
			AISpawningManager.context.launch {
				runCatching { tryLaunchFromConfig(logger, cfg) }
					.onFailure { logger.error("Trade-city caravan launch failed for cityTerritory=${cfg.cityTerritory}", it) }
			}
		}
	}

	/**
	 * Launch flow (high-level):
	 * - choose a route (cfg.allowedDestinations + origin roundtrip)
	 * - create launch record
	 * - select hangar entries by bucket composition (cfg.buckets)
	 * - atomically lease entries to launch
	 * - charge economy (you said you'll do later; hook is here)
	 * - spawn leased ships, attaching leasedModule that points at hangarEntryId + launchId
	 */
	private fun tryLaunchFromConfig(logger: Logger, cfg: TradeCityCaravanConfig) {
		// Resolve origin territory
		val origin: Territory = Territory.findById(cfg.cityTerritory)
			?: run {
				logger.warn("TradeCityCaravanConfig references missing Territory id=${cfg.cityTerritory}")
				return
			}

		// No destinations configured => nothing to do
		if (cfg.allowedDestinations.isEmpty()) return

		val destinations = TradeCityCaravanConfig.getAllowedTerritories(cfg).shuffled().toMutableList()
		// Create route and launch record (active)
		val launchId = TradeCityCaravanLaunch.create(
			originTerritory = cfg.cityTerritory,
			route = destinations.map { it._id },
			riskFactor = cfg.riskFactor,
			speedFactor = cfg.speedFactor
		)
		val route = TraceCityCaravanRoute(
			cites = destinations,
			source = Territory.findById(cfg.cityTerritory)!!,
			launchId
		)

		val availableEntries = selectShipsForLaunch(cfg)

		//TODO: calculate the balance needed if all ships spawned at once, this is a proxy since there is no good way to
		// get the actual cost at spawntime and prevent spawning.

		val mechanic = buildCompositeSpawnerForLaunch(
			route::getSourceLocation,
			cfg, launchId,availableEntries, route)

		mechanic.trigger(logger)
	}

	/**
	 * Select ships from hangar entries.
	 *
	 */
	private fun selectShipsForLaunch(
		cfg: TradeCityCaravanConfig
	): List<TradeCityCaravanHangarEntry> {
		val picked = mutableListOf<TradeCityCaravanHangarEntry>()

		for ((bucketName) in cfg.buckets) {

			// Available ships in this bucket for this city (not leased, intact)
			val candidates = TradeCityCaravanHangarEntry.col.find(
				and(
					TradeCityCaravanHangarEntry::cityTerritory eq cfg.cityTerritory,
					TradeCityCaravanHangarEntry::bucket eq bucketName,
					TradeCityCaravanHangarEntry::leasedByLaunchId eq null,
					TradeCityCaravanHangarEntry::isIntact eq true
				)
			).toList()

			if (candidates.isEmpty()) continue

			picked += candidates.shuffled()
		}

		if (picked.isEmpty()) return emptyList()
		return picked
	}


	private fun buildCompositeSpawnerForLaunch(
		originSpawnLocationProvider: Supplier<Location?>,
		caravanConfig: TradeCityCaravanConfig,
		launchId: Oid<TradeCityCaravanLaunch>,
		availableEntries: List<TradeCityCaravanHangarEntry>,
		route: TraceCityCaravanRoute
	): SpawnerMechanic {


		// 2) Turn entries into BagSpawners (one per bucket)
		val mechanics: List<SpawnerMechanic> = caravanConfig.buckets.map { (bucket, budget) ->
			val budgetSupplier = Supplier {	budget}

			val inBucket = availableEntries.filter { it.bucket == bucket }

			val bagShips: Array<BagSpawner.BagSpawnShip> = inBucket.map { entry ->
				val template = buildTemplate(entry, launchId)
				// Convert effective AITemplate -> SpawnedShip
				//TODO: add faction flavor
				val spawnedShip: SpawnedShip = MINING_GUILD.asSpawnedShip(template)
					.withRandomRadialOffset(50.0, 200.0, 0.0)

				BagSpawner.BagSpawnShip(
					ship = spawnedShip,
					cost = 1
				)
			}.toTypedArray()

			BagSpawner(
				locationProvider = originSpawnLocationProvider,
				budget = budgetSupplier,
				groupMessage = null,
				individualSpawnMessage = null,
				difficultySupplier = {_ -> Supplier{2}},
				targetModeSupplier = { AITarget.TargetMode.MIXED },
				fleetSupplier = { null },
				*bagShips
			)
		}

		// 3) Wrap in CompositeFleetSpawner
		return CompositeFleetSpawner(
			mechanics = mechanics,
			locationProvider = originSpawnLocationProvider,
			groupMessage = null,
			individualSpawnMessage = null,
			difficultySupplier = DifficultyModule::regularSpawnDifficultySupplier,
			targetModeSupplier = { AITarget.TargetMode.MIXED },
			fleetSupplier = { null },
			controllerModifier = {controller -> AIConvoyRegistry.addCaravanModule(controller,route,"SMALL_TC_CARAVAN") }
		)
	}

	private fun buildGroupSpawnerForLaunch(
		originSpawnLocationProvider: Supplier<Location?>,
		launchId: Oid<TradeCityCaravanLaunch>,
		availableEntries: List<TradeCityCaravanHangarEntry>,
		route: TraceCityCaravanRoute
	): SpawnerMechanic {


		val templates = availableEntries.map { entry -> buildTemplate(entry, launchId) }
		val spawnShips = templates.map { entry ->MINING_GUILD.asSpawnedShip(entry)
		.withRandomRadialOffset(50.0, 200.0, 0.0)}

		//TODO: attach route information (CaravanModule)
		return GroupSpawner(
			locationProvider = originSpawnLocationProvider,
			groupMessage = null,
			individualSpawnMessage = null,
			difficultySupplier = {_ -> Supplier{2}},
			targetModeSupplier = { AITarget.TargetMode.MIXED },
			ships = spawnShips.toMutableList()
		)
	}

	private fun buildTemplate(entry : TradeCityCaravanHangarEntry,
							  launchId: Oid<TradeCityCaravanLaunch>) : AITemplate {
		val aiTemplateKey = entry.aiTemplateKey
		val base = AITemplateRegistry.all()[aiTemplateKey]
			?: error("Missing AITemplate '$aiTemplateKey'")

		val baseBehavior = base.behaviorInformation
		val additionalModules = baseBehavior.additionalModules.toMutableList()
		additionalModules.addLast(BehaviorConfiguration.LeasedInformation(entry, launchId))
		// Hull template from sold ship
		val hull = StarshipPlayerSoldTemplate(entry._id)
		//TODO: add weaponsets by guessing them (guessWeaponSets)

		val effectiveBehavior = baseBehavior.copy(additionalModules = additionalModules)
		val effective = base.copy(starshipInfo = hull, behaviorInformation = effectiveBehavior)
		return effective
	}



	/**
	 * Restart recovery:
	 * - find active launches
	 * - find all intact hangar entries leased to them
	 * - respawn those ships (same as normal spawn)
	 */
	private fun resumeActiveLaunches(logger: Logger) {
		val active = TradeCityCaravanLaunch.col.find(TradeCityCaravanLaunch::active eq true).toList()
		if (active.isEmpty()) return

		for (launch in active) {
			val origin = Territory.findById(launch.originTerritory)
				?: run {
					logger.warn("Active launch ${launch._id} references missing origin territory=${launch.originTerritory}")
					continue
				}

			val leased = TradeCityCaravanHangarEntry.col.find(
				and(
					TradeCityCaravanHangarEntry::leasedByLaunchId eq launch._id,
					TradeCityCaravanHangarEntry::isIntact eq true
				)
			).toList()

			if (leased.isEmpty()) continue

			val destinations = launch.route.map { Territory.findById(it)!!}
			val route = TraceCityCaravanRoute(
				cites = destinations.toMutableList(),
				source = destinations[launch.routeIndex],
				launch._id
			)
			route.routeIndex = launch.routeIndex


			val mechanic = buildGroupSpawnerForLaunch(
				route::getSourceLocation,
				launch._id,
				leased,
				route
			)
			mechanic.trigger(logger)
		}
	}

	override fun getSpawner(): AISpawner {
		return spawner ?: error("CaravanScheduler spawner not set")
	}

	override fun setSpawner(spawner: AISpawner): SpawnerScheduler {
		this.spawner = spawner
		return this
	}

	override fun getTickInfo(): String =
		TODO("Not Implemented")
}
