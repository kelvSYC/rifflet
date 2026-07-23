package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun terrEntryWithPollutionEffect(pollutionEffect: Int): TerrEntry = TerrEntry(
    numberOfPossibleResources = 0,
    possibleResources = ByteString.of(),
    name = "",
    civilopediaEntry = "",
    irrigationBonus = 0,
    miningBonus = 0,
    roadBonus = 0,
    defenseBonus = 0,
    movementCost = 0,
    food = 0,
    shields = 0,
    commerce = 0,
    workerJobAllowed = -1,
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

class Civ3FileValidationTest : FunSpec({

    test("validate() surfaces the seed rule's issue for a file with an invalid pollutionEffect") {
        val file = Civ3File(
            Civ3Header(major = 12, minor = 0, description = "", title = ""),
            listOf(TerrSection(listOf(terrEntryWithPollutionEffect(pollutionEffect = 99)))),
        )

        file.validate() shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TERR,
                0,
                "pollutionEffect",
                "pollutionEffect=99 is not -1, not the base-terrain sentinel (1), and not a valid TERR index (0..<1)",
            ),
        )
    }

    test("validate() returns no issues for a file with no sections") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        file.validate() shouldBe emptyList()
    }
})
