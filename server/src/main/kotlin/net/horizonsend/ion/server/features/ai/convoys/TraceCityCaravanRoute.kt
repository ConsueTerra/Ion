package net.horizonsend.ion.server.features.ai.convoys

import net.horizonsend.ion.common.database.Oid
import net.horizonsend.ion.common.database.schema.economy.TradeCityCaravanLaunch
import net.horizonsend.ion.common.database.schema.nations.Territory
import net.horizonsend.ion.server.features.economy.city.TradeCities
import net.horizonsend.ion.server.features.economy.city.TradeCityData
import net.horizonsend.ion.server.features.nations.region.Regions
import net.horizonsend.ion.server.features.nations.region.types.RegionTerritory
import org.bukkit.Bukkit
import org.bukkit.Location

class TraceCityCaravanRoute(
	val cites: MutableList<Territory>,
	val source: Territory,
	val recordId:  Oid<TradeCityCaravanLaunch>
) : ConvoyRoute {
	var routeIndex = 0

	init {
		cites.addFirst(source)
		cites.addLast(source)//make the loop closed
	}

	override fun advanceDestination(): Location? {
		while (cites.size > routeIndex) {
			val next = cites[routeIndex]
			routeIndex++

			val territory = Regions.get<RegionTerritory>(next._id)

			val location = Location(Bukkit.getWorld(next.world), territory.centerX.toDouble(), 200.0, territory.centerZ.toDouble())

			return location
		}

		// If we ran out of destinations, disband
		return null
	}

	override fun getSourceLocation(): Location {
		val territory = Regions.get<RegionTerritory>(source._id)

		val location = Location(Bukkit.getWorld(territory.world), territory.centerX.toDouble(), 200.0, territory.centerZ.toDouble())

		return location
	}
}
