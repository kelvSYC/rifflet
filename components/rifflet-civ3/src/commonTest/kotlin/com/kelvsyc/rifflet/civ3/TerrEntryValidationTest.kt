package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validTerrEntry(
    name: String = "",
    pollutionEffect: Int = -1,
    workerJobAllowed: Int = -1,
): TerrEntry = TerrEntry(
    numberOfPossibleResources = 0,
    possibleResources = ByteString.of(),
    name = name,
    civilopediaEntry = "",
    irrigationBonus = 0,
    miningBonus = 0,
    roadBonus = 0,
    defenseBonus = 0,
    movementCost = 0,
    food = 0,
    shields = 0,
    commerce = 0,
    workerJobAllowed = workerJobAllowed,
    pollutionEffect = pollutionEffect,
    allowCities = 0,
    allowColonies = 0,
    impassable = 0,
    impassableByWheeled = 0,
    allowAirfields = 0,
    allowForts = 0,
    allowOutposts = 0,
    allowRadarTowers = 0,
    unknown = ByteString.of(*ByteArray(4)),
    landmarkEnabled = 0,
    landmarkFood = 0,
    landmarkShields = 0,
    landmarkCommerce = 0,
    landmarkIrrigationBonus = 0,
    landmarkMiningBonus = 0,
    landmarkRoadBonus = 0,
    landmarkMovementBonus = 0,
    landmarkDefensiveBonus = 0,
    landmarkName = "",
    landmarkCivilopediaEntry = "",
    unknown2 = ByteString.of(*ByteArray(4)),
    terrainFlags = 0,
    diseaseStrength = 0,
)

private fun fileWithTerrains(terrains: List<TerrEntry>): Civ3File = Civ3File(
    Civ3Header(major = 12, minor = 0, description = "", title = ""),
    listOf(TerrSection(terrains)),
)

// Index 7 is Forest (the fixed slot); indices 0-6 are filler terrain.
private fun fillerTerrains(count: Int = 7): List<TerrEntry> = List(count) { validTerrEntry(name = "Filler$it") }

class TerrEntryValidationTest : FunSpec({

    test("returns no issues for the None sentinel (-1)") {
        val file = fileWithTerrains(listOf(validTerrEntry(name = "Desert", pollutionEffect = -1)))

        validatePollutionEffect(file) shouldBe emptyList()
    }

    test("returns no issues for the BaseTerrainType sentinel (equal to the TERR section's own size)") {
        val file = fileWithTerrains(listOf(validTerrEntry(name = "Forest", pollutionEffect = 1)))

        validatePollutionEffect(file) shouldBe emptyList()
    }

    test("returns no issues for a valid TERR index") {
        val file = fileWithTerrains(
            listOf(
                validTerrEntry(name = "Desert", pollutionEffect = 1),
                validTerrEntry(name = "Plains", pollutionEffect = -1),
            ),
        )

        validatePollutionEffect(file) shouldBe emptyList()
    }

    test("flags an out-of-range index that matches no sentinel") {
        val file = fileWithTerrains(listOf(validTerrEntry(name = "Desert", pollutionEffect = 99)))

        validatePollutionEffect(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TERR,
                0,
                "pollutionEffect",
                "pollutionEffect=99 is not -1, not the base-terrain sentinel (1), and not a valid TERR index (0..<1)",
            ),
        )
    }

    test("returns no issues when the TERR section is absent from the file") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validatePollutionEffect(file) shouldBe emptyList()
    }

    test("validateClearForestExclusiveToForest returns no issues when only Forest allows it") {
        val terrains = fillerTerrains() + validTerrEntry(name = "Forest", workerJobAllowed = 6)
        val file = fileWithTerrains(terrains)

        validateClearForestExclusiveToForest(file) shouldBe emptyList()
    }

    test("validateClearForestExclusiveToForest returns no issues when no terrain allows it") {
        val file = fileWithTerrains(fillerTerrains() + validTerrEntry(name = "Forest"))

        validateClearForestExclusiveToForest(file) shouldBe emptyList()
    }

    test("validateClearForestExclusiveToForest flags a non-Forest terrain that allows it") {
        val terrains = fillerTerrains(3) + validTerrEntry(name = "Jungle", workerJobAllowed = 6) +
            fillerTerrains(3) + validTerrEntry(name = "Forest", workerJobAllowed = 6)
        val file = fileWithTerrains(terrains)

        validateClearForestExclusiveToForest(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TERR,
                3,
                "workerJobAllowed",
                "TERR[3] (Jungle) has workerJobAllowed=6 (Clear Forest), which only functions on " +
                    "the Forest terrain type (TERR[7])",
            ),
        )
    }

    test("validateClearForestExclusiveToForest returns no issues when TERR is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateClearForestExclusiveToForest(file) shouldBe emptyList()
    }
})
