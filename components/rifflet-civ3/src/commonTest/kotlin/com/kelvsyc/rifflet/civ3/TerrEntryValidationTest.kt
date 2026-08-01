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
    allowCities: Byte = 0,
    terrainFlags: Int = 0,
    landmark: TerrLandmark? = null,
): TerrEntry = TerrEntry(
    numberOfPossibleResources = 0,
    possibleResources = ByteString.of(),
    name = name,
    civilopediaEntry = "",
    terraformBonuses = TerrTerraformBonuses(irrigationBonus = 0, miningBonus = 0, roadBonus = 0),
    defenseBonus = 0,
    movementCost = 0,
    tileValues = TerrTileValues(food = 0, shields = 0, commerce = 0),
    workerJobAllowed = workerJobAllowed,
    pollutionEffect = pollutionEffect,
    allowances = TerrAllowances(
        allowCities = allowCities,
        allowColonies = 0,
        impassable = 0,
        impassableByWheeled = 0,
        allowAirfields = 0,
        allowForts = 0,
        allowOutposts = 0,
        allowRadarTowers = 0,
    ),
    unknown = ByteString.of(*ByteArray(4)),
    landmark = landmark,
    unknown2 = ByteString.of(*ByteArray(4)),
    terrainFlags = terrainFlags,
    diseaseStrength = 0,
)

private fun validTerrLandmark(landmarkEnabled: Byte = 0): TerrLandmark = TerrLandmark(
    landmarkEnabled = landmarkEnabled,
    tileValues = TerrTileValues(food = 0, shields = 0, commerce = 0),
    terraformBonuses = TerrTerraformBonuses(irrigationBonus = 0, miningBonus = 0, roadBonus = 0),
    landmarkMovementBonus = 0,
    landmarkDefensiveBonus = 0,
    landmarkName = "",
    landmarkCivilopediaEntry = "",
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

    test("validateCuredBySanitationRequiresCausesDisease returns no issues when neither is set") {
        val file = fileWithTerrains(listOf(validTerrEntry(name = "Desert", terrainFlags = 0)))

        validateCuredBySanitationRequiresCausesDisease(file) shouldBe emptyList()
    }

    test("validateCuredBySanitationRequiresCausesDisease returns no issues when both are set") {
        val file = fileWithTerrains(listOf(validTerrEntry(name = "Desert", terrainFlags = (1 shl 2) or (1 shl 3))))

        validateCuredBySanitationRequiresCausesDisease(file) shouldBe emptyList()
    }

    test("validateCuredBySanitationRequiresCausesDisease returns no issues when only causesDisease is set") {
        val file = fileWithTerrains(listOf(validTerrEntry(name = "Marsh", terrainFlags = 1 shl 2)))

        validateCuredBySanitationRequiresCausesDisease(file) shouldBe emptyList()
    }

    test("validateCuredBySanitationRequiresCausesDisease flags curedBySanitation set without causesDisease") {
        val file = fileWithTerrains(listOf(validTerrEntry(name = "Desert", terrainFlags = 1 shl 3)))

        validateCuredBySanitationRequiresCausesDisease(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TERR,
                0,
                "curedBySanitation",
                "curedBySanitation is set but causesDisease isn't; the Rules Editor only allows enabling " +
                    "Cured by Sanitation when Causes Disease is also checked",
            ),
        )
    }

    test("validateCuredBySanitationRequiresCausesDisease returns no issues when TERR is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateCuredBySanitationRequiresCausesDisease(file) shouldBe emptyList()
    }

    test("validateLandmarkEnabledOnlyOnSupportedTerrainTypes returns no issues when landmark is absent (VANILLA/PTW)") {
        val file = fileWithTerrains(listOf(validTerrEntry(name = "Desert", landmark = null)))

        validateLandmarkEnabledOnlyOnSupportedTerrainTypes(file) shouldBe emptyList()
    }

    test("validateLandmarkEnabledOnlyOnSupportedTerrainTypes returns no issues when a supported index is enabled") {
        val terrains = List(12) { validTerrEntry(name = "Filler$it", landmark = validTerrLandmark(landmarkEnabled = 0)) } +
            validTerrEntry(name = "Sea", landmark = validTerrLandmark(landmarkEnabled = 1))
        val file = fileWithTerrains(terrains)

        validateLandmarkEnabledOnlyOnSupportedTerrainTypes(file) shouldBe emptyList()
    }

    test("validateLandmarkEnabledOnlyOnSupportedTerrainTypes returns no issues when an unsupported index is disabled") {
        val file = fileWithTerrains(listOf(validTerrEntry(name = "Tundra", landmark = validTerrLandmark(landmarkEnabled = 0))))

        validateLandmarkEnabledOnlyOnSupportedTerrainTypes(file) shouldBe emptyList()
    }

    test("validateLandmarkEnabledOnlyOnSupportedTerrainTypes flags an unsupported index with landmark enabled") {
        val terrains = List(3) { validTerrEntry(name = "Filler$it") } +
            validTerrEntry(name = "Tundra", landmark = validTerrLandmark(landmarkEnabled = 1))
        val file = fileWithTerrains(terrains)

        validateLandmarkEnabledOnlyOnSupportedTerrainTypes(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TERR,
                3,
                "landmark",
                "TERR[3] (Tundra) has landmark.landmarkEnabled set, but the Rules Editor only offers " +
                    "landmark information for Desert/Plains/Grassland/Hills/Mountains/Forest/Sea",
            ),
        )
    }

    test("validateLandmarkEnabledOnlyOnSupportedTerrainTypes returns no issues when TERR is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateLandmarkEnabledOnlyOnSupportedTerrainTypes(file) shouldBe emptyList()
    }
})
