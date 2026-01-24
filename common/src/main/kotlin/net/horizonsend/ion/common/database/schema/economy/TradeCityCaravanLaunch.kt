package net.horizonsend.ion.common.database.schema.economy

import net.horizonsend.ion.common.database.DbObject
import net.horizonsend.ion.common.database.Oid
import net.horizonsend.ion.common.database.OidDbObjectCompanion
import net.horizonsend.ion.common.database.objId
import net.horizonsend.ion.common.database.schema.economy.TradeCityCaravanHangarEntry.Companion.col
import net.horizonsend.ion.common.database.schema.misc.SLPlayerId
import net.horizonsend.ion.common.database.schema.nations.Territory
import net.horizonsend.ion.common.database.schema.starships.PlayerSoldShip
import net.horizonsend.ion.common.database.trx
import org.litote.kmongo.Id
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.ensureIndex
import org.litote.kmongo.ensureUniqueIndex
import java.time.Instant
import java.util.Date


/** Database entry for the persistent tracking of a caravan lifecycle*/
class TradeCityCaravanLaunch(
	override val _id: Oid<TradeCityCaravanLaunch>,
	/** where the caravan launched from*/
	val originTerritory: Oid<Territory>,
	/** the set route of the caravan*/
	val route : List<Oid<Territory>>,
	/** where the caravan is going to */
	var routeIndex : Int = 0,
	/** if this launch is currently active or completed */
	var active : Boolean = true,
	/** the amount of danger associated with this caravan (in terms of extra ai attacks) */
	var riskFactor: Int,
	/** how fast this caravan moves to the next destination (ie wait time at cities or stalling for catchups) */
	var speedFactor: Int,

	/** credits earned while in transit, this sum gets added to the origin when the caravan is completed*/
	var accumulatedBal: Double = 0.0,

	val launchedAt : Date = Date.from(Instant.now()),

	) : DbObject {
	companion object : OidDbObjectCompanion<TradeCityCaravanLaunch>(
		TradeCityCaravanLaunch::class,
		setup = {
			ensureIndex(TradeCityCaravanLaunch::originTerritory)
		}
	) {
		fun delete(id: Oid<TradeCityCaravanLaunch>) = trx { sess ->
			col.deleteOneById(sess, id)
		}


		fun markCompleted() {}

		fun depositMoney() {}

		fun returnToHangar() {}

		fun markDestroyed() {}


		fun create(
			originTerritory: Oid<Territory>,
			route : List<Oid<Territory>>,
			riskFactor: Int,
			speedFactor: Int,
		): Oid<TradeCityCaravanLaunch> = trx { sess ->

			val id = objId<TradeCityCaravanLaunch>()
			col.insertOne(
				sess, TradeCityCaravanLaunch(
					_id = id,
					originTerritory = originTerritory,
					route = route,
					riskFactor = riskFactor,
					speedFactor = speedFactor,
				)
			)
			id
		}
	}
}
