package net.horizonsend.ion.server.features.ai.starship

import com.sk89q.worldedit.extent.clipboard.Clipboard
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


interface StarshipTemplate{
	val type: StarshipType
	val miniMessageName: String
	val manualWeaponSets: MutableSet<WeaponSet>
	val autoWeaponSets: MutableSet<WeaponSet>

	val balancingOverrides: List<StarshipWeaponBalancing<*>>

	fun getClipboard(): Clipboard?
	fun componentName(): Component = miniMessage.deserialize(miniMessageName)
}
