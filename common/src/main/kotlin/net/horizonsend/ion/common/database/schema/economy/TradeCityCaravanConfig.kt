package net.horizonsend.ion.common.database.schema.economy

import net.horizonsend.ion.common.database.DbObject
import net.horizonsend.ion.common.database.Oid
import net.horizonsend.ion.common.database.OidDbObjectCompanion
import net.horizonsend.ion.common.database.objId
import net.horizonsend.ion.common.database.schema.nations.Territory
import net.horizonsend.ion.common.database.trx
import org.bson.conversions.Bson
import org.litote.kmongo.combine
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.ensureIndex
import org.litote.kmongo.ensureUniqueIndex
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.setValue
import java.time.DayOfWeek
import java.time.Instant
import java.util.Date

/** Database-backed schema for caravan information for trade cities */
class TradeCityCaravanConfig(
	override val _id: Oid<TradeCityCaravanConfig>,
	/** trade city that this caravan belongs to*/
	val cityTerritory: Oid<Territory>,
	var enabled: Boolean = true,
	var hour: Int,
	var dayOfWeek : DayOfWeek,
	/** which cites this caravan will travel to*/
	var allowedDestinations: List<Oid<Territory>> = listOf(), // should these be mutable lists?
	/** the buckets (ship categories) to draw from includes for example:
	 * - leading ship
	 * - small and large trade ships
	 * - small and large escorts*/
	var buckets : Map<String,Int> = mapOf(),
	/** the amount of danger associated with this caravan (in terms of extra ai attacks) */
	var riskFactor: Int = 0,
	/** how fast this caravan moves to the next destination (ie wait time at cities or stalling for catchups) */
	var speedFactor: Int = 0,
	/** store when the last update was*/
	var updatedAt: Date = Date(),

	) : DbObject {

	companion object : OidDbObjectCompanion<TradeCityCaravanConfig>(
		TradeCityCaravanConfig::class,
		setup = {
			ensureUniqueIndex(TradeCityCaravanConfig::cityTerritory)
			ensureIndex(TradeCityCaravanConfig::hour)
			ensureIndex(TradeCityCaravanConfig::dayOfWeek)
		}
	) {
		fun delete(id: Oid<TradeCityCaravanConfig>) = trx { sess ->
			col.deleteOneById(sess, id)
			//TODO: update members
		}
		fun get(owner: Territory): TradeCityCaravanConfig? {
			return col.findOne { TradeCityCaravanConfig::cityTerritory eq owner._id }
		}

		fun create(
			cityTerritory: Oid<Territory>,
			hour: Int,
			dayOfWeek: DayOfWeek,
		): Oid<TradeCityCaravanConfig> = trx { sess ->

			val id = objId<TradeCityCaravanConfig>()
			col.insertOne(sess, TradeCityCaravanConfig(
				_id = id,
				cityTerritory = cityTerritory,
				hour = hour,
				dayOfWeek = dayOfWeek
			))
			id
		}

		private fun alsoUpdateTime(update: Bson): Bson {
			val date = Date.from(Instant.now())
			return combine(update, setValue(TradeCityCaravanConfig::updatedAt, date))
		}

		fun setEnabled() {
			//TODO(Not Implemented)
		}
		fun setSchedule(hour: Int, dayOfWeek: DayOfWeek) {
			//TODO(Not Implemented)
		}

		fun setupBuckets(bucket: String, budget : Int) {
			//TODO(Not Implemented)
		}

		fun setAllowedDestinations(destinations: List<Oid<Territory>>) {
			//TODO(Not Implemented)
		}
	}

}
