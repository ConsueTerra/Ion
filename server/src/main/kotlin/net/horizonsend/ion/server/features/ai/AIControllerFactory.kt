package net.horizonsend.ion.server.features.ai

import net.horizonsend.ion.server.features.ai.configuration.AIStarshipTemplate.WeaponSet
import net.horizonsend.ion.server.features.ai.module.AIModule
import net.horizonsend.ion.server.features.starship.active.ActiveStarship
import net.horizonsend.ion.server.features.starship.control.controllers.ai.AIController
import net.horizonsend.ion.server.features.starship.damager.AIShipDamager
import net.kyori.adventure.text.Component
import kotlin.reflect.KClass

class AIControllerFactory private constructor(
	val identifier: String,
	private val coreModules: (AIController) -> Builder.ModuleBuilder,
	private var utilModules: (AIController) -> Set<AIModule>
) {
	/** Build the controller */
	operator fun invoke(
		starship: ActiveStarship,
		pilotName: Component,
		autoSets: Set<WeaponSet>,
		manualSets: Set<WeaponSet>
	) : AIController {
		return AIController(
			starship = starship,
			damager = AIShipDamager(starship),
			pilotName = pilotName,
			setupCoreModules = coreModules,
			setupUtilModules = utilModules,
			manualWeaponSets = autoSets,
			autoWeaponSets = manualSets,
		)
	}

	operator fun invoke(starship: ActiveStarship, pilotName: Component) = invoke(starship, pilotName, setOf(), setOf())

	class Builder(val identifier: String) {
		private var coreModules: (AIController) -> ModuleBuilder = { ModuleBuilder() }
		private var utilModules: MutableSet<(AIController) -> AIModule> = mutableSetOf()

		fun setCoreModuleBuilder(moduleBuilder: (AIController) -> ModuleBuilder) = apply { coreModules = moduleBuilder }
		fun addUtilModule(builder: (AIController) -> AIModule) = utilModules.add(builder)

		fun build(): AIControllerFactory = AIControllerFactory(identifier, coreModules = coreModules) { controller -> this.utilModules.mapTo(mutableSetOf()) { it.invoke(controller) } }

		class ModuleBuilder {
			private val modules: MutableMap<KClass<out AIModule>, AIModule> = mutableMapOf()

			fun <T: AIModule> addModule(identifier: KClass<out AIModule>, module: T): T {
				modules[identifier] = module
				return module
			}

			fun build(): Map<KClass<out AIModule>, AIModule> = modules
		}
	}
}
