package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validTerrEntry(
    name: String = "",
    workerJobAllowed: Int = -1,
    pollutionEffect: Int = -1,
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
        allowCities = 0,
        allowColonies = 0,
        impassable = 0,
        impassableByWheeled = 0,
        allowAirfields = 0,
        allowForts = 0,
        allowOutposts = 0,
        allowRadarTowers = 0,
    ),
    unknown = ByteString.of(*ByteArray(4)),
    landmark = null,
    unknown2 = ByteString.of(*ByteArray(4)),
    terrainFlags = 0,
    diseaseStrength = 0,
)

class TerrEntryReferencesTest : FunSpec({

    val terrains = listOf(validTerrEntry(name = "Desert"), validTerrEntry(name = "Plains"))

    test("pollutionEffectResolved is None for -1") {
        validTerrEntry(pollutionEffect = -1).pollutionEffectResolved(terrains) shouldBe TerrPollutionEffect.None
    }

    test("pollutionEffectResolved is BaseTerrainType when equal to the TERR section's own entry count") {
        validTerrEntry(pollutionEffect = terrains.size).pollutionEffectResolved(terrains) shouldBe
            TerrPollutionEffect.BaseTerrainType
    }

    test("pollutionEffectResolved resolves a valid index to the matching Terrain") {
        validTerrEntry(pollutionEffect = 1).pollutionEffectResolved(terrains) shouldBe
            TerrPollutionEffect.Terrain(terrains[1])
    }

    test("pollutionEffectResolved resolves an out-of-range index to a null Terrain") {
        validTerrEntry(pollutionEffect = 99).pollutionEffectResolved(terrains) shouldBe
            TerrPollutionEffect.Terrain(null)
    }

    test("workerJobAllowedTfrm resolves against the TFRM section") {
        val jobs = listOf(validTfrmEntry(name = "Mine"), validTfrmEntry(name = "Clear Forest"))
        validTerrEntry(workerJobAllowed = 1).workerJobAllowedTfrm(jobs) shouldBe jobs[1]
    }

    test("workerJobAllowedTfrm is null for -1 (no worker job allowed)") {
        validTerrEntry(workerJobAllowed = -1).workerJobAllowedTfrm(emptyList()) shouldBe null
    }
})

private fun validTfrmEntry(name: String): TfrmEntry = TfrmEntry(
    name = name,
    civilopediaEntry = "",
    turnsToComplete = 0,
    required = -1,
    requiredResource1 = -1,
    requiredResource2 = -1,
    order = "",
)
