package net.horizonsend.ion.server.features.ai

import SteeringModule
import net.horizonsend.ion.server.IonServer.aiSteeringConfig
import net.horizonsend.ion.server.IonServerComponent
import net.horizonsend.ion.server.features.ai.module.combat.CombatModule
import net.horizonsend.ion.server.features.ai.module.combat.DefensiveCombatModule
import net.horizonsend.ion.server.features.ai.module.combat.FrigateCombatModule
import net.horizonsend.ion.server.features.ai.module.combat.GoonCombatModule
import net.horizonsend.ion.server.features.ai.module.combat.MultiTargetFrigateCombatModule
import net.horizonsend.ion.server.features.ai.module.combat.StarfighterCombatModule
import net.horizonsend.ion.server.features.ai.module.debug.AIDebugModule
import net.horizonsend.ion.server.features.ai.module.misc.ContactsJammerModule
import net.horizonsend.ion.server.features.ai.module.misc.DifficultyModule
import net.horizonsend.ion.server.features.ai.module.misc.GravityWellModule
import net.horizonsend.ion.server.features.ai.module.misc.TrackingModule
import net.horizonsend.ion.server.features.ai.module.steering.BasicSteeringModule
import net.horizonsend.ion.server.features.ai.module.steering.CapitalSteeringModule
import net.horizonsend.ion.server.features.ai.module.steering.DistancePositioningModule
import net.horizonsend.ion.server.features.ai.module.steering.GunshipSteeringModule
import net.horizonsend.ion.server.features.ai.module.steering.StarfighterSteeringModule
import net.horizonsend.ion.server.features.ai.module.steering.SteeringSolverModule
import net.horizonsend.ion.server.features.ai.module.steering.TravelSteeringModule
import net.horizonsend.ion.server.features.ai.module.targeting.ClosestLargeStarshipTargetingModule
import net.horizonsend.ion.server.features.ai.module.targeting.ClosestPlayerTargetingModule
import net.horizonsend.ion.server.features.ai.module.targeting.ClosestSmallStarshipTargetingModule
import net.horizonsend.ion.server.features.ai.module.targeting.ClosestTargetingModule
import net.horizonsend.ion.server.features.ai.module.targeting.HighestDamagerTargetingModule
import net.horizonsend.ion.server.features.ai.module.targeting.TargetingModule
import net.horizonsend.ion.server.features.space.Space
import net.horizonsend.ion.server.features.starship.control.controllers.ai.AIController
import net.horizonsend.ion.server.miscellaneous.utils.Vec3i
import net.horizonsend.ion.server.miscellaneous.utils.distanceToVector
import net.horizonsend.ion.server.miscellaneous.utils.map
import net.horizonsend.ion.server.miscellaneous.utils.orNull
import java.util.Optional
import kotlin.random.Random

@Suppress("unused") // Entry points
object AIControllerFactories : IonServerComponent() {
	val presetControllers = mutableMapOf<String, AIControllerFactory>()

	val starfighter = registerFactory("STARFIGHTER") {
		setCoreModuleBuilder { controller: AIController, difficulty ->
			val builder = AIControllerFactory.Builder.ModuleBuilder()

			val targeting = builder.addModule(TargetingModule::class, ClosestTargetingModule(controller, 500.0, null).apply { sticky = false })

			val difficultyManager = builder.addModule(DifficultyModule::class, DifficultyModule(controller, internalDifficulty = difficulty))
			builder.addModule(CombatModule::class, StarfighterCombatModule(controller, difficultyManager, targeting::findTarget))

			val distance = builder.addModule(DistancePositioningModule::class, DistancePositioningModule(controller, difficultyManager, aiSteeringConfig.starfighterDistanceConfiguration))

			val steering = builder.addModule(SteeringModule::class, StarfighterSteeringModule(
				controller,
				difficultyManager,
				targeting::findTarget,
				distance::calcDistance
			))

			builder.addModule(
				SteeringSolverModule::class, SteeringSolverModule(
				controller,
				steering,
				difficultyManager,
				targeting::findTarget ,
				SteeringSolverModule.MovementType.DC
			)
			)

			builder
		}

		addUtilModule { AIDebugModule(it) }

		build()
	}

	val gunship = registerFactory("GUNSHIP") {
		setCoreModuleBuilder { controller, difficulty ->
			val builder = AIControllerFactory.Builder.ModuleBuilder()

			val targeting = builder.addModule(TargetingModule::class, ClosestTargetingModule(controller, 500.0, null).apply { sticky = false })

			val difficultyManager = builder.addModule(DifficultyModule::class, DifficultyModule(controller, internalDifficulty = difficulty))
			builder.addModule(CombatModule::class, StarfighterCombatModule(controller, difficultyManager, targeting::findTarget))

			val distance = builder.addModule(DistancePositioningModule::class, DistancePositioningModule(controller, difficultyManager, aiSteeringConfig.starfighterDistanceConfiguration))

			val steering = builder.addModule(SteeringModule::class, GunshipSteeringModule(
				controller,
				difficultyManager,
				targeting::findTarget,
				distance::calcDistance
			))

			builder.addModule(
				SteeringSolverModule::class, SteeringSolverModule(
				controller,
				steering,
				difficultyManager,
				targeting::findTarget ,
				SteeringSolverModule.MovementType.DC
			)
			)

			builder
		}

		addUtilModule { AIDebugModule(it) }

		build()
    }

	val gunship_pulse = registerFactory("GUNSHIP_PULSE") {
		setCoreModuleBuilder { controller, difficulty ->
			val builder = AIControllerFactory.Builder.ModuleBuilder()

			val targeting = builder.addModule(TargetingModule::class, ClosestTargetingModule(controller, 500.0, null).apply { sticky = false })

			val difficultyManager = builder.addModule(DifficultyModule::class, DifficultyModule(controller, internalDifficulty = difficulty))
			builder.addModule(CombatModule::class, StarfighterCombatModule(controller, difficultyManager, targeting::findTarget))

			val distance = builder.addModule(DistancePositioningModule::class, DistancePositioningModule(controller, difficultyManager, aiSteeringConfig.starfighterDistanceConfiguration))

			val steering = builder.addModule(SteeringModule::class, GunshipSteeringModule(
				controller,
				difficultyManager,
				targeting::findTarget,
				distance::calcDistance
			))

			builder.addModule(
				SteeringSolverModule::class, SteeringSolverModule(
				controller,
				steering,
				difficultyManager,
				targeting::findTarget ,
				SteeringSolverModule.MovementType.DC
			)
			)

			builder
		}

		addUtilModule { AIDebugModule(it) }

		build()
	}

	val goonship = registerFactory("GOONSHIP") {
		setCoreModuleBuilder { controller, difficulty ->
			val builder = AIControllerFactory.Builder.ModuleBuilder()

			val targeting = builder.addModule(TargetingModule::class, ClosestTargetingModule(controller, 500.0, null).apply { sticky = false })

			val difficultyManager = builder.addModule(DifficultyModule::class, DifficultyModule(controller, internalDifficulty = difficulty))
			builder.addModule(CombatModule::class, GoonCombatModule(controller, difficultyManager, targeting::findTarget))

			val steering = builder.addModule(SteeringModule::class, BasicSteeringModule(
				controller,
				difficultyManager,
				targeting::findTarget
			)
			)

			builder.addModule(
				SteeringSolverModule::class, SteeringSolverModule(
				controller,
				steering,
				difficultyManager,
				targeting::findTarget ,
				SteeringSolverModule.MovementType.DC
			)
			)

			builder
		}

		addUtilModule { AIDebugModule(it) }

		build()
	}

	val jammingGunship = registerFactory("JAMMING_GUNSHIP") {
		setCoreModuleBuilder { controller, difficulty ->
			val builder = AIControllerFactory.Builder.ModuleBuilder()
			val targeting = builder.addModule(TargetingModule::class, ClosestPlayerTargetingModule(controller, 5000.0))

			val difficultyManager = builder.addModule(DifficultyModule::class, DifficultyModule(controller, internalDifficulty = difficulty))
			builder.addModule(CombatModule::class, StarfighterCombatModule(controller, difficultyManager, targeting::findTarget))


			val distance = builder.addModule(DistancePositioningModule::class, DistancePositioningModule(controller, difficultyManager, aiSteeringConfig.starfighterDistanceConfiguration))
			val steering = builder.addModule(SteeringModule::class, GunshipSteeringModule(
				controller,
				difficultyManager,
				targeting::findTarget,
				distance::calcDistance
			))

			builder.addModule(
				SteeringSolverModule::class, SteeringSolverModule(
				controller,
				steering,
				difficultyManager,
				targeting::findTarget ,
				SteeringSolverModule.MovementType.DC
			)
			)

			builder.addModule(ContactsJammerModule::class, ContactsJammerModule(controller, 300.0, controller.getCoreModuleSupplier<TargetingModule>(TargetingModule::class).map { it.findTargets() }))
			builder
		}

		addUtilModule { AIDebugModule(it) }

		build()
	}

	val corvette = registerFactory("CORVETTE") {
		setCoreModuleBuilder { controller, difficulty ->
			val builder = AIControllerFactory.Builder.ModuleBuilder()

			val targeting = builder.addModule(TargetingModule::class, ClosestTargetingModule(controller, 500.0, null).apply { sticky = false })

			val difficultyManager = builder.addModule(DifficultyModule::class, DifficultyModule(controller, internalDifficulty = difficulty))

			builder.addModule(CombatModule::class, StarfighterCombatModule(controller, difficultyManager, targeting::findTarget))

			val distance = builder.addModule(DistancePositioningModule::class, DistancePositioningModule(controller, difficultyManager, aiSteeringConfig.starfighterDistanceConfiguration))

			val steering = builder.addModule(SteeringModule::class, GunshipSteeringModule(
				controller,
				difficultyManager,
				targeting::findTarget,
				distance::calcDistance,
				config = aiSteeringConfig.corvetteBasicSteeringConfiguration
			))

			builder.addModule(
				SteeringSolverModule::class, SteeringSolverModule(
				controller,
				steering,
				difficultyManager,
				targeting::findTarget ,
				SteeringSolverModule.MovementType.DC
			)
			)

			builder
		}

		addUtilModule { AIDebugModule(it) }

		build()
	}

	val interdictionCorvette = registerFactory("INTERDICTION_CORVETTE") {
		setCoreModuleBuilder { controller, difficulty ->
			val builder = AIControllerFactory.Builder.ModuleBuilder()

			val targeting = builder.addModule(TargetingModule::class, ClosestTargetingModule(controller, 2000.0, null).apply { sticky = false })
			val difficultyManager = builder.addModule(DifficultyModule::class, DifficultyModule(controller, internalDifficulty = difficulty))
			builder.addModule(CombatModule::class, StarfighterCombatModule(controller, difficultyManager, targeting::findTarget))

			val distance = builder.addModule(DistancePositioningModule::class, DistancePositioningModule(controller, difficultyManager, aiSteeringConfig.interdictionCorvetteDistanceConfiguration))

			val steering = builder.addModule(SteeringModule::class, GunshipSteeringModule(
				controller,
				difficultyManager,
				targeting::findTarget,
				distance::calcDistance,
				config = aiSteeringConfig.corvetteBasicSteeringConfiguration
			))

			builder.addModule(
				SteeringSolverModule::class, SteeringSolverModule(
				controller,
				steering,
				difficultyManager,
				targeting::findTarget ,
				SteeringSolverModule.MovementType.DC
			)
			)

			builder
		}

		addUtilModule { AIDebugModule(it) }
		addUtilModule { controller -> GravityWellModule(controller, 1800.0, true, controller.getCoreModuleSupplier<TargetingModule>(TargetingModule::class).map { it.findTarget() }) }

		build()
	}

	val logisticCorvette = registerFactory("LOGISTIC_CORVETTE") {
		setCoreModuleBuilder { controller, difficulty ->
			val builder = AIControllerFactory.Builder.ModuleBuilder()

			val targeting = builder.addModule(TargetingModule::class, ClosestLargeStarshipTargetingModule(controller, 2000.0, null, true).apply { sticky = false })
			val difficultyManager = builder.addModule(DifficultyModule::class, DifficultyModule(controller, internalDifficulty = difficulty))
			builder.addModule(CombatModule::class, MultiTargetFrigateCombatModule(controller, difficultyManager, toggleRandomTargeting = true) { targeting.findTargets() })

			val distance = builder.addModule(DistancePositioningModule::class, DistancePositioningModule(controller, difficultyManager, aiSteeringConfig.logisticCorvetteDistanceConfiguration))

			val steering = builder.addModule(SteeringModule::class, GunshipSteeringModule(
				controller,
				difficultyManager,
				targeting::findTarget,
				distance::calcDistance,
				config = aiSteeringConfig.corvetteBasicSteeringConfiguration
			))

			builder.addModule(
				SteeringSolverModule::class, SteeringSolverModule(
				controller,
				steering,
				difficultyManager,
				targeting::findTarget ,
				SteeringSolverModule.MovementType.DC
			)
			)


			builder
		}

		addUtilModule { AIDebugModule(it) }

		build()
	}

	val miniFrigate = registerFactory("MINI_FRIGATE") {//for 4.9k ships
		setCoreModuleBuilder { controller, difficulty ->
			val builder = AIControllerFactory.Builder.ModuleBuilder()

			val targeting = builder.addModule(TargetingModule::class, ClosestTargetingModule(controller, 1500.0, null).apply { sticky = true })
			val difficultyManager = builder.addModule(DifficultyModule::class, DifficultyModule(controller, internalDifficulty = difficulty))
			builder.addModule(CombatModule::class, FrigateCombatModule(controller, difficultyManager, toggleRandomTargeting = true, targeting::findTarget))

			val distance = builder.addModule(DistancePositioningModule::class, DistancePositioningModule(controller, difficultyManager, aiSteeringConfig.miniFrigateDistanceConfiguration))

			val steering = builder.addModule(SteeringModule::class, GunshipSteeringModule(
				controller,
				difficultyManager,
				targeting::findTarget,
				distance::calcDistance,
				config = aiSteeringConfig.miniFrigateBasicSteeringConfiguration
			))

			builder.addModule(
				SteeringSolverModule::class, SteeringSolverModule(
				controller,
				steering,
				difficultyManager,
				targeting::findTarget ,
				SteeringSolverModule.MovementType.DC
			)
			)

			builder
		}

		addUtilModule { AIDebugModule(it) }

		build()
	}

	val frigate = registerFactory("FRIGATE") {
        setCoreModuleBuilder { controller, difficulty ->
			val builder = AIControllerFactory.Builder.ModuleBuilder()

			val targeting = builder.addModule(TargetingModule::class, ClosestTargetingModule(controller, 1500.0, null).apply { sticky = true })
			val difficultyManager = builder.addModule(DifficultyModule::class, DifficultyModule(controller, internalDifficulty = difficulty))
			builder.addModule(CombatModule::class, FrigateCombatModule(controller, difficultyManager, toggleRandomTargeting = true, targeting::findTarget))

			val distance = builder.addModule(DistancePositioningModule::class, DistancePositioningModule(controller, difficultyManager, aiSteeringConfig.capitalDistanceConfiguration))
			val steering = builder.addModule(SteeringModule::class, CapitalSteeringModule(
				controller,
				difficultyManager,
				targeting::findTarget,
				distance::calcDistance
			))

			builder.addModule(
				SteeringSolverModule::class, SteeringSolverModule(
				controller,
				steering,difficultyManager,
				targeting::findTarget,
				SteeringSolverModule.MovementType.CRUISE
			)
			)


			builder
        }

		addUtilModule { AIDebugModule(it) }

        build()
    }

	val advancedFrigate = registerFactory("ADVANCED_FRIGATE") {
        setCoreModuleBuilder { controller, difficulty ->
			val builder = AIControllerFactory.Builder.ModuleBuilder()

			val targeting = builder.addModule(TargetingModule::class, ClosestSmallStarshipTargetingModule(controller, 700.0, null).apply { sticky = true })
			builder.addModule(TrackingModule::class, TrackingModule(controller, 5, 1800.0, 15.0, targeting::findTarget))
			val difficultyManager = builder.addModule(DifficultyModule::class, DifficultyModule(controller, internalDifficulty = difficulty))
			builder.addModule(CombatModule::class, FrigateCombatModule(controller, difficultyManager, toggleRandomTargeting = true, targeting::findTarget))

			val distance = builder.addModule(DistancePositioningModule::class, DistancePositioningModule(controller, difficultyManager, aiSteeringConfig.advancedCapitalDistanceConfiguration))
			val steering = builder.addModule(SteeringModule::class, CapitalSteeringModule(
				controller,
				difficultyManager,
				controller.getCoreModuleSupplier<TargetingModule>(TargetingModule::class).map { it.findTarget() },
				distance::calcDistance
			))

			builder.addModule(
				SteeringSolverModule::class, SteeringSolverModule(
				controller,
				steering,difficultyManager,
				targeting::findTarget,
				SteeringSolverModule.MovementType.CRUISE
			)
			)


			builder
        }

		addUtilModule { AIDebugModule(it) }
		addUtilModule { controller -> GravityWellModule(controller, 1800.0, true, controller.getCoreModuleSupplier<TargetingModule>(TargetingModule::class).map { it.findTarget() }) }

        build()
	}

	val destroyer = registerFactory("DESTROYER") {
		setCoreModuleBuilder { controller, difficulty ->
			val builder = AIControllerFactory.Builder.ModuleBuilder()

			val targeting = builder.addModule(TargetingModule::class, ClosestTargetingModule(controller, 5000.0, null).apply { sticky = true })
			val difficultyManager = builder.addModule(DifficultyModule::class, DifficultyModule(controller, internalDifficulty = difficulty))
			builder.addModule(CombatModule::class, FrigateCombatModule(controller, difficultyManager, toggleRandomTargeting = true, targeting::findTarget))

			val distance = builder.addModule(DistancePositioningModule::class, DistancePositioningModule(controller, difficultyManager, aiSteeringConfig.capitalDistanceConfiguration))
			val steering = builder.addModule(SteeringModule::class, CapitalSteeringModule(
				controller,
				difficultyManager,
				targeting::findTarget,
				distance::calcDistance,
				config = aiSteeringConfig.destroyerBasicSteeringConfiguration
			))

			builder.addModule(
				SteeringSolverModule::class, SteeringSolverModule(
				controller,
				steering,difficultyManager,
				targeting::findTarget,
				SteeringSolverModule.MovementType.CRUISE
			)
			)


			builder
		}

		addUtilModule { AIDebugModule(it) }

		build()
	}

	val advancedDestroyer = registerFactory("ADVANCED_DESTROYER") {
		setCoreModuleBuilder { controller, difficulty ->
			val builder = AIControllerFactory.Builder.ModuleBuilder()

			val targeting = builder.addModule(TargetingModule::class, ClosestLargeStarshipTargetingModule(controller, 5000.0, null).apply { sticky = true })
			val difficultyManager = builder.addModule(DifficultyModule::class, DifficultyModule(controller, internalDifficulty = difficulty))
			builder.addModule(CombatModule::class, FrigateCombatModule(controller, difficultyManager, toggleRandomTargeting = false, targeting::findTarget))

			val distance = builder.addModule(DistancePositioningModule::class, DistancePositioningModule(controller, difficultyManager, aiSteeringConfig.advancedCapitalDistanceConfiguration))
			val steering = builder.addModule(SteeringModule::class, CapitalSteeringModule(
				controller,
				difficultyManager,
				targeting::findTarget,
				distance::calcDistance,
				config = aiSteeringConfig.destroyerBasicSteeringConfiguration
			))

			builder.addModule(
				SteeringSolverModule::class, SteeringSolverModule(
				controller,
				steering,difficultyManager,
				targeting::findTarget,
				SteeringSolverModule.MovementType.CRUISE
			)
			)


			builder
		}

		addUtilModule { AIDebugModule(it) }

		build()
	}

	val battlecruiser = registerFactory("BATTLECRUISER") {
		setCoreModuleBuilder { controller, difficulty ->
			val builder = AIControllerFactory.Builder.ModuleBuilder()

			val targeting = builder.addModule(TargetingModule::class, ClosestLargeStarshipTargetingModule(controller, 5000.0, null, focusRange = 200.0).apply { sticky = true })
			builder.addModule(TrackingModule::class, TrackingModule(controller, 5, 87.5, 35.0, targeting::findTarget))
			val difficultyManager = builder.addModule(DifficultyModule::class, DifficultyModule(controller, internalDifficulty = difficulty))
			builder.addModule(CombatModule::class, FrigateCombatModule(controller, difficultyManager, toggleRandomTargeting = true, targeting::findTarget))

			val distance = builder.addModule(DistancePositioningModule::class, DistancePositioningModule(controller, difficultyManager, aiSteeringConfig.advancedCapitalDistanceConfiguration))

			val steering = builder.addModule(SteeringModule::class, CapitalSteeringModule(
				controller,
				difficultyManager,
				targeting::findTarget,
				distance::calcDistance,
				config = aiSteeringConfig.battlecruiserBasicSteeringConfiguration
			))

			builder.addModule(SteeringSolverModule::class, SteeringSolverModule(
				controller,
				steering,difficultyManager,
				targeting::findTarget,
				SteeringSolverModule.MovementType.CRUISE
			))

			builder
		}

		addUtilModule { AIDebugModule(it) }

		build()
	}

	val passive_cruise = registerFactory("EXPLORER_CRUISE") {
		val cruiseEndpoint: (AIController) -> Optional<Vec3i> = lambda@{ controller ->
			var iterations = 0
			val origin = controller.getCenter()

			val world = controller.getWorld()
			val border = world.worldBorder

			val minX = (border.center.x - border.size).toInt()
			val minZ = (border.center.z - border.size).toInt()
			val maxX = (border.center.x + border.size).toInt()
			val maxZ = (border.center.z + border.size).toInt()

			while (iterations < 15) {
				iterations++

				val endPointX = Random.nextInt(minX, maxX)
				val endPointZ = Random.nextInt(minZ, maxZ)
				val endPoint = Vec3i(endPointX, origin.y, endPointZ)

				val planets = Space.getPlanets().filter { it.spaceWorld == world }.map { it.location.toVector() }

				val minDistance = planets.minOfOrNull {
					val direction = endPoint.minus(origin)

					distanceToVector(origin.toVector(), direction.toVector(), it)
				}

				// If there are planets, and the distance to any of them along the path of travel is less than 500, discard
				if (minDistance != null && minDistance <= 500.0) continue

				return@lambda Optional.of(endPoint)
			}

			Optional.empty()
		}

		setCoreModuleBuilder { controller, difficulty ->
			val builder = AIControllerFactory.Builder.ModuleBuilder()

			// Combat handling
			val targeting = builder.addModule(TargetingModule::class, HighestDamagerTargetingModule(controller))
			val difficultyManager = builder.addModule(DifficultyModule::class, DifficultyModule(controller, internalDifficulty = difficulty))
			builder.addModule(CombatModule::class, DefensiveCombatModule(controller ,difficultyManager, targeting::findTarget))

			// Movement handling
			val distance = builder.addModule(DistancePositioningModule::class, DistancePositioningModule(controller, difficultyManager, aiSteeringConfig.starfighterDistanceConfiguration))
			val steering = builder.addModule(SteeringModule::class, TravelSteeringModule(
				controller,
				difficultyManager,
				targeting::findTarget,
				distance::calcDistance,
				cruiseEndpoint.invoke(controller).orNull() ?: Vec3i(0, 0, 0)
			)
			)

			builder.addModule(
				SteeringSolverModule::class, SteeringSolverModule(
				controller,
				steering,
				difficultyManager,
				targeting::findTarget ,
				SteeringSolverModule.MovementType.DC
			))

			builder
		}

		addUtilModule { AIDebugModule(it) }

		build()
	}

	operator fun get(identifier: String) = presetControllers[identifier] ?: throw NoSuchElementException("Controller factory $identifier does not exist!")

	fun registerFactory(
		identifier: String,
		factory: AIControllerFactory.Builder.() -> AIControllerFactory
	): AIControllerFactory {
		val built = factory(AIControllerFactory.Builder(identifier))

		presetControllers[identifier] = built
		return built
	}
}
