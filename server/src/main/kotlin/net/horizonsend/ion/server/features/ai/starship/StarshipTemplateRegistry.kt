package net.horizonsend.ion.server.features.ai.starship

import net.horizonsend.ion.common.utils.configuration.Configuration
import net.horizonsend.ion.common.utils.text.colors.ABYSSAL_DARK_RED
import net.horizonsend.ion.common.utils.text.colors.ABYSSAL_DESATURATED_RED
import net.horizonsend.ion.common.utils.text.colors.ABYSSAL_LIGHT_RED
import net.horizonsend.ion.common.utils.text.colors.EXPLORER_MEDIUM_CYAN
import net.horizonsend.ion.common.utils.text.colors.HEColorScheme.Companion.HE_LIGHT_GRAY
import net.horizonsend.ion.common.utils.text.colors.MINING_CORP_DARK_ORANGE
import net.horizonsend.ion.common.utils.text.colors.MINING_CORP_LIGHT_ORANGE
import net.horizonsend.ion.common.utils.text.colors.PIRATE_LIGHT_RED
import net.horizonsend.ion.common.utils.text.colors.PIRATE_SATURATED_RED
import net.horizonsend.ion.common.utils.text.colors.PRIVATEER_DARK_TEAL
import net.horizonsend.ion.common.utils.text.colors.PRIVATEER_LIGHT_TEAL
import net.horizonsend.ion.common.utils.text.colors.PRIVATEER_MEDIUM_TEAL
import net.horizonsend.ion.common.utils.text.colors.TSAII_DARK_ORANGE
import net.horizonsend.ion.common.utils.text.colors.TSAII_VERY_DARK_ORANGE
import net.horizonsend.ion.common.utils.text.colors.WATCHER_STANDARD
import net.horizonsend.ion.common.utils.text.colors.吃饭人_STANDARD
import net.horizonsend.ion.common.utils.text.miniMessage
import net.horizonsend.ion.common.utils.text.serialize
import net.horizonsend.ion.server.configuration.ConfigurationFiles
import net.horizonsend.ion.server.core.IonServerComponent
import net.horizonsend.ion.server.features.ai.configuration.WeaponSet
import net.horizonsend.ion.server.features.starship.StarshipType.AI_BARGE
import net.horizonsend.ion.server.features.starship.StarshipType.AI_BATTLECRUISER
import net.horizonsend.ion.server.features.starship.StarshipType.AI_CORVETTE
import net.horizonsend.ion.server.features.starship.StarshipType.AI_CORVETTE_LOGISTIC
import net.horizonsend.ion.server.features.starship.StarshipType.AI_DESTROYER
import net.horizonsend.ion.server.features.starship.StarshipType.AI_FRIGATE
import net.horizonsend.ion.server.features.starship.StarshipType.AI_GUNSHIP
import net.horizonsend.ion.server.features.starship.StarshipType.AI_HEAVY_FREIGHTER
import net.horizonsend.ion.server.features.starship.StarshipType.AI_INTERCEPTOR
import net.horizonsend.ion.server.features.starship.StarshipType.AI_LIGHT_FREIGHTER
import net.horizonsend.ion.server.features.starship.StarshipType.AI_MEDIUM_FREIGHTER
import net.horizonsend.ion.server.features.starship.StarshipType.AI_SHUTTLE
import net.horizonsend.ion.server.features.starship.StarshipType.AI_STARFIGHTER
import net.horizonsend.ion.server.features.starship.StarshipType.AI_TRANSPORT
import net.horizonsend.ion.server.features.starship.StarshipType.UNIDENTIFIEDSHIP
import net.kyori.adventure.text.Component.text

/**
 * Predefined starship templates.
 *
 * These are not AI on their own, just enough instructions to spawn, detect, and pilot the ship.
 *
 * Some other details tied to the ship, such as its name and weapon sets are included.
 *
 * Starship templates may be used for multiple AI templates, such as reinforced or non-reinforced variants.
 *
 * All behavior will be handled by AI Templates
 * @see AITemplateRegistry
 **/
object StarshipTemplateRegistry : IonServerComponent(true) {
	private val TEMPLATE_DIRECTORY = ConfigurationFiles.configurationFolder.resolve("starship_templates").apply { mkdirs() }

	// START_TEST
	val TEST_JAMMER = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "test_jammer",
			type = AI_GUNSHIP,
			miniMessageName = text("Test Jammer", WATCHER_STANDARD).serialize(miniMessage)
		)
	)

	val TEST_LOGISTIC = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "test_logistic",
			type = AI_CORVETTE_LOGISTIC,
			miniMessageName = text("Test Logistic", WATCHER_STANDARD).serialize(miniMessage)
		)
	)

	val TEST_BATTLECRUISER = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "test_battlecruiser",
			type = AI_BATTLECRUISER,
			miniMessageName = text("Test Battlecruiser", WATCHER_STANDARD).serialize(miniMessage)
		)
	)

	val TEST_DISINTEGRATOR = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "test_disintegrator",
			type = AI_DESTROYER,
			miniMessageName = text("Test Disintegrator", WATCHER_STANDARD).serialize(miniMessage)
		)
	)

	val TEST_CYCLE = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "test_cycle",
			type = AI_FRIGATE,
			miniMessageName = text("Test Cycle", WATCHER_STANDARD).serialize(miniMessage)
		)
	)
	// END_TEST

	// START_WATCHERS

	val VERDOLITH = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Verdolith",
			type = AI_FRIGATE,
			miniMessageName = text("Verdolith", WATCHER_STANDARD).serialize(miniMessage),
			manualWeaponSets = mutableSetOf(
				WeaponSet(
					name = "phaser",
					engagementRangeMin = 0.0,
					engagementRangeMax = 220.0
				),
				WeaponSet(
					name = "manual",
					engagementRangeMin = 220.0,
					engagementRangeMax = 550.0
				),
			),
			autoWeaponSets = mutableSetOf(
				WeaponSet(
					name = "auto",
					engagementRangeMin = 250.0,
					engagementRangeMax = 550.0
				)
			),
		)
	)

	val TERALITH = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Teralith",
			type = AI_FRIGATE,
			miniMessageName = text("Teralith", WATCHER_STANDARD).serialize(miniMessage),
			manualWeaponSets = mutableSetOf(
				WeaponSet(
					name = "phaser",
					engagementRangeMin = 0.0,
					engagementRangeMax = 220.0
				),
				WeaponSet(
					name = "manual",
					engagementRangeMin = 220.0,
					engagementRangeMax = 550.0
				),
			),
			autoWeaponSets = mutableSetOf(
				WeaponSet(
					name = "auto",
					engagementRangeMin = 250.0,
					engagementRangeMax = 550.0
				)
			)
		)
	)

	val ARBOREALITH = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Arborealith",
			type = AI_DESTROYER,
			miniMessageName = text("Arborealith", WATCHER_STANDARD).serialize(miniMessage),
			manualWeaponSets = mutableSetOf(
				WeaponSet(
					name = "phaser",
					engagementRangeMin = 0.0,
					engagementRangeMax = 220.0
				),
				WeaponSet(
					name = "manual",
					engagementRangeMin = 220.0,
					engagementRangeMax = 550.0
				),
			),
			autoWeaponSets = mutableSetOf(
				WeaponSet(
					name = "auto",
					engagementRangeMin = 250.0,
					engagementRangeMax = 550.0
				),
				WeaponSet(
					name = "tt",
					engagementRangeMin = 250.0,
					engagementRangeMax = 550.0
				)
			)

		)
	)

	// END_WATCHERS
	//START 吃饭人

	val MIANBAOZHA = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Mianbaozha",
			type = AI_STARFIGHTER,
			miniMessageName = text("Mianbaozha", 吃饭人_STANDARD).serialize(miniMessage),
		)
	)

	val LOUMAI = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Loumai",
			type = AI_GUNSHIP,
			miniMessageName = text("Loumai", 吃饭人_STANDARD).serialize(miniMessage),
			manualWeaponSets = mutableSetOf(
				WeaponSet(
					name = "manual",
					engagementRangeMin = 0.0,
					engagementRangeMax = 550.0
				)
			),
			autoWeaponSets = mutableSetOf(
				WeaponSet(
					name = "auto",
					engagementRangeMin = 0.0,
					engagementRangeMax = 550.0
				)
			)
		)
	)

	val MIANBAO = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Mianbao",
			type = AI_CORVETTE,
			miniMessageName = text("Mianbao", 吃饭人_STANDARD).serialize(miniMessage),
			manualWeaponSets = mutableSetOf(
				WeaponSet(
					name = "manual",
					engagementRangeMin = 0.0,
					engagementRangeMax = 550.0
				)
			),
			autoWeaponSets = mutableSetOf(
				WeaponSet(
					name = "auto",
					engagementRangeMin = 250.0,
					engagementRangeMax = 550.0
				)
			)
		)
	)

	val MALINGSHU = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Malingshu",
			type = AI_FRIGATE,
			miniMessageName = text("Malingshu", 吃饭人_STANDARD).serialize(miniMessage),
			manualWeaponSets = mutableSetOf(
				WeaponSet(
					name = "Manual",
					engagementRangeMin = 0.0,
					engagementRangeMax = 250.0
				)
			),
			autoWeaponSets = mutableSetOf(
				WeaponSet(
					name = "auto",
					engagementRangeMin = 0.0,
					engagementRangeMax = 250.0
				),
				WeaponSet(
					name = "TT",
					engagementRangeMin = 250.0,
					engagementRangeMax = 550.0
				)
			)
		)
	)

	val FASHIGUN = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Fashigun",
			type = AI_DESTROYER,
			miniMessageName = text("Fashigun", 吃饭人_STANDARD).serialize(miniMessage),
			manualWeaponSets = mutableSetOf(
				WeaponSet(
					name = "HL",
					engagementRangeMin = 0.0,
					engagementRangeMax = 500.0
				),
				WeaponSet(
					name = "HLs",
					engagementRangeMin = 200.0,
					engagementRangeMax = 300.0
				),
				WeaponSet(
					name = "PH",
					engagementRangeMin = 0.0,
					engagementRangeMax = 200.0
				)
			),
			autoWeaponSets = mutableSetOf(
				WeaponSet(
					name = "LT",
					engagementRangeMin = 0.0,
					engagementRangeMax = 250.0
				),
				WeaponSet(
					name = "TT",
					engagementRangeMin = 300.0,
					engagementRangeMax = 550.0
				)
			)
		)
	)

	// END_吃饭人
	// START_PIRATE

	val ISKAT = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Iskat",
			type = AI_STARFIGHTER,
			miniMessageName = "<${PIRATE_LIGHT_RED.asHexString()}>Iskat"
		)
	)

	val VOSS = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Voss",
			type = AI_STARFIGHTER,
			miniMessageName = "<${PIRATE_LIGHT_RED.asHexString()}>Voss"
		)
	)

	val HECTOR = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Hector",
			type = AI_STARFIGHTER,
			miniMessageName = "<${PIRATE_LIGHT_RED.asHexString()}>Hector"
		)
	)

	val HIRO = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Hiro",
			type = AI_STARFIGHTER,
			miniMessageName = "<${PIRATE_LIGHT_RED.asHexString()}>Hiro"
		)
	)

	val WASP = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Wasp",
			type = AI_STARFIGHTER,
			miniMessageName = "<${PIRATE_LIGHT_RED.asHexString()}>Wasp"
		)
	)

	val FRENZ = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Frenz",
			type = AI_STARFIGHTER,
			miniMessageName = "<${PIRATE_LIGHT_RED.asHexString()}>Frenz"
		)
	)

	val TEMPEST = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Tempest",
			type = AI_STARFIGHTER,
			miniMessageName = "<${PIRATE_LIGHT_RED.asHexString()}>Tempest"
		)
	)

	val VELASCO = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Velasco",
			type = AI_STARFIGHTER,
			miniMessageName = "<${PIRATE_LIGHT_RED.asHexString()}>Velasco"
		)
	)

	val ANAAN = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Anaan",
			type = AI_GUNSHIP,
			miniMessageName = "<${PIRATE_SATURATED_RED.asHexString()}>Anaan",
		)
	)

	val VENDETTA = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Vendetta",
			type = AI_GUNSHIP,
			miniMessageName = "<${PIRATE_SATURATED_RED.asHexString()}>Vendetta",
		)
	)

	val CORMORANT = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Cormorant",
			type = AI_GUNSHIP,
			miniMessageName = "<${PIRATE_SATURATED_RED.asHexString()}>Cormorant",
		)
	)

	val MANTIS = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Mantis",
			type = AI_GUNSHIP,
			miniMessageName = "<${PIRATE_SATURATED_RED.asHexString()}>Mantis",
			manualWeaponSets = mutableSetOf(WeaponSet(name = "Main", engagementRangeMin = 0.0, engagementRangeMax = 500.0)),
			autoWeaponSets = mutableSetOf(WeaponSet(name = "lts", engagementRangeMin = 0.0, engagementRangeMax = 500.0))
		)
	)

	val HERNSTEIN = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Hernstein",
			type = AI_GUNSHIP,
			miniMessageName = "<${PIRATE_SATURATED_RED.asHexString()}>Hernstein",
			manualWeaponSets = mutableSetOf(WeaponSet(name = "main", engagementRangeMin = 0.0, engagementRangeMax = 500.0)),
			autoWeaponSets = mutableSetOf(WeaponSet(name = "lts", engagementRangeMin = 0.0, engagementRangeMax = 500.0))
		)
	)

	val FYR = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Fyr",
			type = AI_GUNSHIP,
			miniMessageName = "<${PIRATE_SATURATED_RED.asHexString()}>Fyr",
		)
	)

	val BLOODSTAR = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Bloodstar",
			type = AI_CORVETTE,
			miniMessageName = "<${PIRATE_SATURATED_RED.asHexString()}>Bloodstar",
			manualWeaponSets = mutableSetOf(
				WeaponSet(name = "Manual", engagementRangeMin = 0.0, engagementRangeMax = 500.0),
				WeaponSet(name = "hl", engagementRangeMin = 0.0, engagementRangeMax = 200.0)
			),
			autoWeaponSets = mutableSetOf(
				WeaponSet(name = "tt", engagementRangeMin = 200.0, engagementRangeMax = 500.0),
				WeaponSet(name = "lts", engagementRangeMin = 0.0, engagementRangeMax = 250.0),)
		)
	)

	// END_PIRATE
	// START_EXPLORER

	val WAYFINDER = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Wayfinder",
			type = AI_TRANSPORT,
			miniMessageName = "<${EXPLORER_MEDIUM_CYAN.asHexString()}>Wayfinder",
		)
	)

	val SPARROW = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Sparrow",
			type = AI_SHUTTLE,
			miniMessageName = "<${EXPLORER_MEDIUM_CYAN.asHexString()}>Sparrow",
		)
	)

	val NIMBLE = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Nimble",
			type = AI_SHUTTLE,
			miniMessageName = "<${EXPLORER_MEDIUM_CYAN.asHexString()}>Nimble",
		)
	)

	val DESSLE = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Dessle",
			type = AI_LIGHT_FREIGHTER,
			miniMessageName = "<${EXPLORER_MEDIUM_CYAN.asHexString()}>Dessle <${HE_LIGHT_GRAY.asHexString()}>OldOreData Transporter",
		)
	)

	val MINHAUL_CHETHERITE = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Minhaul_chetherite",
			type = AI_SHUTTLE,
			miniMessageName = "<${EXPLORER_MEDIUM_CYAN.asHexString()}>Minhaul <${HE_LIGHT_GRAY.asHexString()}>[<light_purple>Chetherite<${HE_LIGHT_GRAY.asHexString()}>]",
		)
	)

	val MINHAUL_REDSTONE = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Minhaul_redstone",
			type = AI_SHUTTLE,
			miniMessageName = "<${EXPLORER_MEDIUM_CYAN.asHexString()}>Minhaul <${HE_LIGHT_GRAY.asHexString()}>[<red>Redstone<${HE_LIGHT_GRAY.asHexString()}>]",
		)
	)

	val MINHAUL_TITANIUM = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Minhaul_chetherite",
			type = AI_SHUTTLE,
			miniMessageName = "<${EXPLORER_MEDIUM_CYAN.asHexString()}>Minhaul <${HE_LIGHT_GRAY.asHexString()}>[<gray>Titanium<${HE_LIGHT_GRAY.asHexString()}>]",
		)
	)

	val EXOTRAN_TITANIUM = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Exotran_titanium",
			type = AI_LIGHT_FREIGHTER,
			miniMessageName = "<${EXPLORER_MEDIUM_CYAN.asHexString()}>Exotran <${HE_LIGHT_GRAY.asHexString()}>[<gray>Titanium<${HE_LIGHT_GRAY.asHexString()}>]",
		)
	)

	val EXOTRAN_CHETHERITE = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Exotran_chetherite",
			type = AI_LIGHT_FREIGHTER,
			miniMessageName = "<${EXPLORER_MEDIUM_CYAN.asHexString()}>Exotran <${HE_LIGHT_GRAY.asHexString()}>[<light_purple>Chetherite<${HE_LIGHT_GRAY.asHexString()}>]",
		)
	)

	val EXOTRAN_REDSTONE = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Exotran_redstone",
			type = AI_LIGHT_FREIGHTER,
			miniMessageName = "<${EXPLORER_MEDIUM_CYAN.asHexString()}>Exotran <${HE_LIGHT_GRAY.asHexString()}>[<red>Redstone<${HE_LIGHT_GRAY.asHexString()}>]",
		)
	)

	val AMPH = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Amph",
			type = AI_TRANSPORT,
			miniMessageName = "<${EXPLORER_MEDIUM_CYAN.asHexString()}>Amph",
		)
	)

	// END_EXPLORER
	// START_PRIVATEER

	val BULWARK = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Bulwark",
			type = AI_CORVETTE,
			miniMessageName = "<$PRIVATEER_DARK_TEAL>Bulwark",
			manualWeaponSets = mutableSetOf(
				WeaponSet(name = "Main", engagementRangeMin = 0.0, engagementRangeMax = 550.0)
			),
			autoWeaponSets = mutableSetOf(
				WeaponSet(name = "auto", engagementRangeMin = 0.0, engagementRangeMax = 550.0)
			)
		)
	)

	val RESOLUTE = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Resolute",
			type = AI_DESTROYER,
			miniMessageName = "<$PRIVATEER_DARK_TEAL>Resolute",
			manualWeaponSets = mutableSetOf(
				WeaponSet(name = "Manual", engagementRangeMin = 0.0, engagementRangeMax = 550.0)
			),
			autoWeaponSets = mutableSetOf(
				WeaponSet(name = "auto", engagementRangeMin = 220.0, engagementRangeMax = 550.0)
			)
		)
	)

	val CONTRACTOR = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Contractor",
			type = AI_GUNSHIP,
			miniMessageName = "<$PRIVATEER_MEDIUM_TEAL>Contractor",
			manualWeaponSets = mutableSetOf(
				WeaponSet(name = "Manual", engagementRangeMin = 0.0, engagementRangeMax = 550.0)
			),
			autoWeaponSets = mutableSetOf(
				WeaponSet(name = "auto", engagementRangeMin = 0.0, engagementRangeMax = 550.0)
			)
		)
	)

	val DAGGER = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Dagger",
			type = AI_STARFIGHTER,
			miniMessageName = "<$PRIVATEER_LIGHT_TEAL>Dagger",
		)
	)

	val DAYBREAK = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Daybreak",
			type = AI_CORVETTE,
			miniMessageName = "<$PRIVATEER_LIGHT_TEAL>Daybreak",
			manualWeaponSets = mutableSetOf(
				WeaponSet(name = "Manual", engagementRangeMin = 0.0, engagementRangeMax = 550.0)
			),
			autoWeaponSets = mutableSetOf(
				WeaponSet(name = "lt", engagementRangeMin = 0.0, engagementRangeMax = 550.0)
			)
		)
	)

	val PATROLLER = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Patroller",
			type = AI_GUNSHIP,
			miniMessageName = "<$PRIVATEER_LIGHT_TEAL>Patroller",
		)
	)

	val PROTECTOR = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Protector",
			type = AI_GUNSHIP,
			miniMessageName = "<$PRIVATEER_LIGHT_TEAL>Protector",
		)
	)

	val VETERAN = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Veteran",
			type = AI_GUNSHIP,
			miniMessageName = "<$PRIVATEER_LIGHT_TEAL>Veteran",
		)
	)

	val TENETA = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Teneta",
			type = AI_STARFIGHTER,
			miniMessageName = "<$PRIVATEER_LIGHT_TEAL>Teneta",
		)
	)

	val FURIOUS = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Furious",
			type = AI_STARFIGHTER,
			miniMessageName = "<$PRIVATEER_LIGHT_TEAL>Furious",
		)
	)

	val INFLICT = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Inflict",
			type = AI_STARFIGHTER,
			miniMessageName = "<$PRIVATEER_LIGHT_TEAL>Inflict",
		)
	)

	val PIONEER = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Pioneer",
			type = AI_INTERCEPTOR,
			miniMessageName = "<$PRIVATEER_LIGHT_TEAL>Pioneer",
		)
	)

	// END_PRIVATEER
	// START_MINING_GUILD

	val ANGLE = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Angle",
			type = AI_BARGE,
			miniMessageName = "<$MINING_CORP_LIGHT_ORANGE>Angle",
			autoWeaponSets = mutableSetOf(WeaponSet(name = "tt", engagementRangeMin = 0.0, engagementRangeMax = 500.0)),
			manualWeaponSets = mutableSetOf(WeaponSet(name = "ht", engagementRangeMin = 0.0, engagementRangeMax = 500.0))
		)
	)

	val DUNKLEOSTEUS = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Dunkleosteus",
			type = AI_HEAVY_FREIGHTER,
			miniMessageName = "<$MINING_CORP_LIGHT_ORANGE>Dunkleosteus",
			autoWeaponSets = mutableSetOf(
				WeaponSet(name = "lt", engagementRangeMin = 0.0, engagementRangeMax = 250.0),
				WeaponSet(name = "tt", engagementRangeMin = 0.0, engagementRangeMax = 550.0)
			),
			manualWeaponSets = mutableSetOf(WeaponSet(name = "main", engagementRangeMin = 0.0, engagementRangeMax = 500.0))
		)
	)

	val GROUPER = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Grouper",
			type = AI_MEDIUM_FREIGHTER,
			miniMessageName = "<$MINING_CORP_LIGHT_ORANGE>Grouper",
			autoWeaponSets = mutableSetOf(WeaponSet(name = "lt", engagementRangeMin = 0.0, engagementRangeMax = 250.0)),
			manualWeaponSets = mutableSetOf(WeaponSet(name = "weapons", engagementRangeMin = 0.0, engagementRangeMax = 500.0))
		)
	)

	val OSTRICH = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Ostrich",
			type = AI_LIGHT_FREIGHTER,
			miniMessageName = "<$MINING_CORP_DARK_ORANGE>Ostrich",
			autoWeaponSets = mutableSetOf(
				WeaponSet(name = "lts", engagementRangeMin = 0.0, engagementRangeMax = 250.0),
				WeaponSet(name = "tt", engagementRangeMin = 300.0, engagementRangeMax = 500.0)),
			manualWeaponSets = mutableSetOf(
				WeaponSet(name = "manual", engagementRangeMin = 0.0, engagementRangeMax = 500.0),
				WeaponSet(name = "hl", engagementRangeMin = 0.0, engagementRangeMax = 300.0))
		)
	)

	val WOODPECKER = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Woodpecker",
			type = AI_SHUTTLE,
			miniMessageName = "<$MINING_CORP_LIGHT_ORANGE>Woodpecker",
		)
	)

	val BEAVER = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Beaver",
			type = AI_TRANSPORT,
			miniMessageName = "<$MINING_CORP_LIGHT_ORANGE>Beaver",
			autoWeaponSets = mutableSetOf(WeaponSet(name = "lt", engagementRangeMin = 0.0, engagementRangeMax = 500.0)),
			manualWeaponSets = mutableSetOf(WeaponSet(name = "lw", engagementRangeMin = 0.0, engagementRangeMax = 200.0))
		)
	)

	val BADGER = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Badger",
			type = AI_LIGHT_FREIGHTER,
			miniMessageName = "<$MINING_CORP_DARK_ORANGE>Badger",
			autoWeaponSets = mutableSetOf(WeaponSet(name = "lt", engagementRangeMin = 0.0, engagementRangeMax = 500.0))
		)
	)

	val TYPE_V11 = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "typeV11",
			type = AI_STARFIGHTER,
			miniMessageName = "<$MINING_CORP_LIGHT_ORANGE>Type <$HE_LIGHT_GRAY>V-11",
		)
	)

	val TYPEA21B = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "typeA21b",
			type = AI_SHUTTLE,
			miniMessageName = "<$MINING_CORP_LIGHT_ORANGE>Type <$HE_LIGHT_GRAY>A-21b",
		)
	)

	val TYPEI41 = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "typeI41",
			type = AI_SHUTTLE,
			miniMessageName = "<$MINING_CORP_LIGHT_ORANGE>Type <$HE_LIGHT_GRAY>I-41",
		)
	)

	// END_MINING_GUILD
	// START_TSAII

	val SWARMER = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Swarmer",
			type = AI_STARFIGHTER,
			miniMessageName = "<$TSAII_DARK_ORANGE>Swarmer",
		)
	)

	val SCYTHE = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Scythe",
			type = AI_STARFIGHTER,
			miniMessageName = "<$TSAII_DARK_ORANGE>Scythe",
		)
	)

	val RAIDER = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Raider",
			type = AI_GUNSHIP,
			miniMessageName = "<$TSAII_VERY_DARK_ORANGE>Raider",
		)
	)

	val REAVER = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Reaver",
			type = AI_FRIGATE,
			miniMessageName = "<$TSAII_VERY_DARK_ORANGE>Reaver",
			manualWeaponSets = mutableSetOf(
				WeaponSet(
					name = "manual",
					engagementRangeMin = 0.0,
					engagementRangeMax = 220.0
				)
			),
			autoWeaponSets = mutableSetOf(
				WeaponSet(
					name = "auto",
					engagementRangeMin = 250.0,
					engagementRangeMax = 550.0
				)
			)
		)
	)

	val BASTION = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Swarmer",
			type = AI_BATTLECRUISER,
			miniMessageName = "<$TSAII_VERY_DARK_ORANGE>Bastion",
		)
	)

	// END_TSAII
	val SKUTTLE = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Skuttle",
			type = AI_STARFIGHTER,
			miniMessageName = "<dark_red>Skuttle",
		)
	)

	val PUMPKIN_DEVOURER = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "pumpkin_devourer",
			type = UNIDENTIFIEDSHIP,
			miniMessageName = "<#FFA500>Pumpkin Devourer",
		)
	)

	val PUMPKIN_KIN = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "small_pumpkin",
			type = UNIDENTIFIEDSHIP,
			miniMessageName = "<#FFA500>Pumpkin Kin",
		)
	)

	val DREDGE = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Dredge",
			type = UNIDENTIFIEDSHIP,
			miniMessageName = "<$ABYSSAL_DESATURATED_RED>Dredge",
		)
	)

	val EMPEROR = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Emperor",
			type = UNIDENTIFIEDSHIP,
			miniMessageName = "<$ABYSSAL_LIGHT_RED>Emperor",
		)
	)

	val HIGH_PRIESTESS = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "HighPriestess",
			type = UNIDENTIFIEDSHIP,
			miniMessageName = "<$ABYSSAL_DARK_RED>High Priestess",
		)
	)

	val GRAFT = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Graft",
			type = UNIDENTIFIEDSHIP,
			miniMessageName = "<$ABYSSAL_DARK_RED>Graft",
		)
	)

	val CHARM = registerTemplate(
		StarshipSchematicTemplate(
			schematicName = "Charm",
			type = UNIDENTIFIEDSHIP,
			miniMessageName = "<$ABYSSAL_DARK_RED>Charm",
		)
	)

	private fun registerTemplate(default: StarshipSchematicTemplate): StarshipSchematicTemplate {
		return Configuration.loadOrDefault(TEMPLATE_DIRECTORY, "${default.schematicName}.json", default)
	}
}
