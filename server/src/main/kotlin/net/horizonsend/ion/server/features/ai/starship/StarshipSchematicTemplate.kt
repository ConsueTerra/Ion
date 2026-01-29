package net.horizonsend.ion.server.features.ai.starship

import com.sk89q.worldedit.extent.clipboard.Clipboard
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.horizonsend.ion.common.utils.text.miniMessage
import net.horizonsend.ion.server.IonServer
import net.horizonsend.ion.server.configuration.starship.StarshipWeaponBalancing
import net.horizonsend.ion.server.features.ai.configuration.WeaponSet
import net.horizonsend.ion.server.features.ai.spawning.AISpawningManager
import net.horizonsend.ion.server.features.starship.StarshipType
import net.kyori.adventure.text.Component
import java.io.File
import kotlin.jvm.optionals.getOrNull

@Serializable
data class StarshipSchematicTemplate(
	val schematicName: String,
	override val type: StarshipType,
	override val miniMessageName: String,

	override val manualWeaponSets: MutableSet<WeaponSet> = mutableSetOf(),
	override val autoWeaponSets: MutableSet<WeaponSet> = mutableSetOf(),

	override val balancingOverrides: List<StarshipWeaponBalancing<*>> = listOf()
) : StarshipTemplate {
	@Transient
	val schematicFile: File = IonServer.dataFolder.resolve("aiShips").resolve("$schematicName.schem")

	fun getSchematic(): Clipboard? = AISpawningManager.schematicCache[schematicFile].getOrNull()
	override fun getClipboard(): Clipboard? {
		return getSchematic()
	}

}
