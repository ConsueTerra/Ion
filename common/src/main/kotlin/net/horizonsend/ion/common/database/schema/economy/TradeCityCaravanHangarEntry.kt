package net.horizonsend.ion.common.database.schema.economy

import net.horizonsend.ion.common.database.DbObject
import net.horizonsend.ion.common.database.Oid
import net.horizonsend.ion.common.database.OidDbObjectCompanion
import net.horizonsend.ion.common.database.objId
import net.horizonsend.ion.common.database.schema.misc.SLPlayerId
import net.horizonsend.ion.common.database.schema.nations.Territory
import net.horizonsend.ion.common.database.schema.starships.PlayerSoldShip
import net.horizonsend.ion.common.database.trx
import org.litote.kmongo.and
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.ensureIndex
import org.litote.kmongo.ensureUniqueIndex
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import java.time.Instant
import java.util.Date



/** database entry for a ship sitting in a virtual hangar for use in caravans*/
class TradeCityCaravanHangarEntry(
	override val _id: Oid<TradeCityCaravanHangarEntry>,
	val cityTerritory: Oid<Territory>,
	val soldShipId: Oid<PlayerSoldShip>, //replace with a dedicated entry
	/** which caravan bucket (trade ship, escort, lead ect this ship belongs to) */
	var bucket: String? = null,
	/** who is using the ship at the moment (if any) */
	var leasedByLaunchId: Oid<TradeCityCaravanLaunch>? = null,
	/** if the ship is usable (ships can ether despawn during restarts or be destroyed) **/
	var isIntact : Boolean = true,
	/** used only for NPC caravans, make this ship available again if it is lost */
	val respawnable: Boolean = false,

	/** cached print cost of the ship
	 *
	 * this is used for launching and returning a ship
	 *
	 * as well as calculating bonuses for revenue*/
	val costOverride: Int?  = null, //might be unnecessary
	val addedBy: SLPlayerId,
	val addedAt: Date = Date.from(Instant.now())
) : DbObject {
	companion object : OidDbObjectCompanion<TradeCityCaravanHangarEntry>(
		TradeCityCaravanHangarEntry::class,
		setup = {
			ensureIndex(TradeCityCaravanHangarEntry::cityTerritory)
			ensureIndex(TradeCityCaravanHangarEntry::bucket)
			ensureUniqueIndex(TradeCityCaravanHangarEntry::soldShipId)
		}
	) {
		fun delete(id: Oid<TradeCityCaravanHangarEntry>) = trx { sess ->
			col.deleteOneById(sess, id)
			//TODO: also delete stored ship entry
		}

		fun getAll(owner: Territory, bucket: String?): List<TradeCityCaravanHangarEntry> {
			return TODO("Not Implemented")
		}

		fun getAllLeasedShips(launch: TradeCityCaravanLaunch): List<TradeCityCaravanHangarEntry> {
			return TODO("Not Implemented")
		}

		fun removeAll(owner: Territory, bucket: String?) = trx { sess ->}

		fun updateBucket() {}

		fun launchFromHangar() {}

		fun returnToHangar() {}

		fun markDestroyed() {}


		fun create(
			cityTerritory: Oid<Territory>,
			soldShipId: Oid<PlayerSoldShip>,
			addedBy: SLPlayerId,
			respawnable: Boolean = false,
		): Oid<TradeCityCaravanHangarEntry> = trx { sess ->

			val id = objId<TradeCityCaravanHangarEntry>()
			col.insertOne(
				sess, TradeCityCaravanHangarEntry(
					_id = id,
					cityTerritory = cityTerritory,
					soldShipId = soldShipId,
					addedBy = addedBy,
					respawnable = respawnable,
				)
			)
			id
		}
	}
}
