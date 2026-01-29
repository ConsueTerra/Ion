package net.horizonsend.ion.server.features.ai.starship

import com.sk89q.worldedit.extent.clipboard.Clipboard
import net.horizonsend.ion.common.database.Oid
import net.horizonsend.ion.common.database.schema.economy.TradeCityCaravanHangarEntry
import net.horizonsend.ion.common.database.schema.starships.PlayerSoldShip
import net.horizonsend.ion.server.configuration.starship.StarshipWeaponBalancing
import net.horizonsend.ion.server.features.ai.configuration.WeaponSet
import net.horizonsend.ion.server.miscellaneous.utils.actualType
import net.horizonsend.ion.server.miscellaneous.utils.loadClipboard

class StarshipPlayerSoldTemplate(
	val hangarEntryId : Oid<TradeCityCaravanHangarEntry>
) : StarshipTemplate {
	val solShipId = TradeCityCaravanHangarEntry.findById(hangarEntryId)!!.soldShipId
	override val type = PlayerSoldShip.findById(solShipId)!!.type.actualType

	override val miniMessageName: String = PlayerSoldShip.findById(solShipId)!!.name

	//guess the weapon sets on creation
	override val autoWeaponSets: MutableSet<WeaponSet> = mutableSetOf()
	override val manualWeaponSets: MutableSet<WeaponSet> = mutableSetOf()
	//not applicable
	override val balancingOverrides: List<StarshipWeaponBalancing<*>> = listOf()

	override fun getClipboard(): Clipboard? {
		return PlayerSoldShip.findById(solShipId)?.loadClipboard()
	}
}
