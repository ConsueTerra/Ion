package net.horizonsend.ion.server.features.ai.module.misc

import net.horizonsend.ion.common.database.Oid
import net.horizonsend.ion.common.database.schema.economy.TradeCityCaravanHangarEntry
import net.horizonsend.ion.common.database.schema.economy.TradeCityCaravanLaunch
import net.horizonsend.ion.server.features.ai.module.AIModule
import net.horizonsend.ion.server.features.starship.control.controllers.ai.AIController

/**
 * Controls the communication of an active ship with the hangar database
 * marks leasing, retuning and destruction.
 * */
class LeasedModule(
	controller: AIController,
	val entry : TradeCityCaravanHangarEntry,
	val launch : Oid<TradeCityCaravanLaunch>
) : AIModule(controller) {
	//TODO: Full implementations
}
