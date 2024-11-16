package net.horizonsend.ion.server.features.ai.module.combat

import net.horizonsend.ion.server.command.admin.debug
import net.horizonsend.ion.server.features.ai.module.misc.DifficultyModule
import net.horizonsend.ion.server.features.ai.util.AITarget
import net.horizonsend.ion.server.features.starship.control.controllers.ai.AIController
import net.horizonsend.ion.server.miscellaneous.utils.Vec3i
import net.horizonsend.ion.server.miscellaneous.utils.debugAudience
import net.horizonsend.ion.server.miscellaneous.utils.getDirection
import net.horizonsend.ion.server.miscellaneous.utils.leftFace
import net.horizonsend.ion.server.miscellaneous.utils.rightFace
import net.horizonsend.ion.server.miscellaneous.utils.vectorToBlockFace
import java.util.function.Supplier

class FrigateCombatModule(
	controller: AIController,
	difficulty : DifficultyModule,
	private val toggleRandomTargeting: Boolean = true,
	targetingSupplier: Supplier<AITarget?>
) : CombatModule(controller,difficulty, targetingSupplier) {
	var leftFace: Boolean = false
	var ticks = 0
	private var aimAtRandom = false

	override fun tick() {
		ticks++
		val target = targetingSupplier.get() ?: return

		val distance = target.getLocation().toVector().distance(getCenter().toVector())
		if (distance > 750) {return}

		val direction = getDirection(Vec3i(getCenter()), target.getVec3i(false)).normalize()

		handleAutoWeapons(starship.centerOfMass, target)
		fireAllWeapons(
			origin = starship.centerOfMass,
			target = target.getVec3i(aimAtRandom).toVector(),
			direction = direction
		)

		if (toggleRandomTargeting && ticks % 40 == 0) {
			aimAtRandom = !aimAtRandom
		}
	}
}
