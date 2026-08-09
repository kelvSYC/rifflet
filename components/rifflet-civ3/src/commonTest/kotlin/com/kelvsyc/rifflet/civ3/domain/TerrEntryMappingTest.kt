package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.TerrAllowances
import com.kelvsyc.rifflet.civ3.TerrEntry
import com.kelvsyc.rifflet.civ3.TerrLandmark
import com.kelvsyc.rifflet.civ3.TerrTerraformBonuses
import com.kelvsyc.rifflet.civ3.TerrTileValues
import com.kelvsyc.rifflet.civ3.TerrainSlot
import com.kelvsyc.rifflet.civ3.TfrmEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun terrEntry(
    name: String = "",
    possibleResources: ByteString? = null,
    numberOfPossibleResources: Int = 0,
    workerJobAllowed: Int = -1,
    pollutionEffect: Int = -1,
    landmark: TerrLandmark? = null,
    terrainFlags: Int = 0,
    unknown: ByteString = ByteString.of(0, 0, 0, 0),
    unknown2: ByteString = ByteString.of(0, 0, 0, 0),
): TerrEntry = TerrEntry(
    numberOfPossibleResources = numberOfPossibleResources,
    possibleResources = possibleResources ?: ByteString.of(*ByteArray((numberOfPossibleResources + 7) / 8)),
    name = name,
    civilopediaEntry = "",
    terraformBonuses = TerrTerraformBonuses(irrigationBonus = 1, miningBonus = 2, roadBonus = 3),
    defenseBonus = 10,
    movementCost = 1,
    tileValues = TerrTileValues(food = 1, shields = 0, commerce = 0),
    workerJobAllowed = workerJobAllowed,
    pollutionEffect = pollutionEffect,
    allowances = TerrAllowances(
        allowCities = 1, allowColonies = 1, impassable = 0, impassableByWheeled = 0,
        allowAirfields = 0, allowForts = 0, allowOutposts = 0, allowRadarTowers = 0,
    ),
    unknown = unknown,
    landmark = landmark,
    unknown2 = unknown2,
    terrainFlags = terrainFlags,
    diseaseStrength = 0,
)

// 9 filler slots (Desert..Jungle) so a real TerrainSlot lands at a chosen index.
private fun fillerTerrains(count: Int, numberOfPossibleResources: Int = 0): List<TerrEntry> =
    List(count) { terrEntry(name = "Filler$it", numberOfPossibleResources = numberOfPossibleResources) }

private fun resource(name: String): Resource = Resource(name = name)

private fun tfrmJob(name: String): TfrmEntry = TfrmEntry(
    name = name, civilopediaEntry = "", turnsToComplete = 1, required = -1,
    requiredResource1 = -1, requiredResource2 = -1, order = "",
)

class TerrEntryMappingTest : FunSpec({

    test("toDomain requires exactly 12 entries for VANILLA/PTW") {
        shouldThrow<IllegalArgumentException> {
            fillerTerrains(11).toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList())
        }
        fillerTerrains(12).toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList()).size shouldBe 12
    }

    test("toDomain requires exactly 14 entries for CONQUESTS") {
        shouldThrow<IllegalArgumentException> {
            fillerTerrains(12).toDomain(Civ3FormatEra.CONQUESTS, emptyList(), emptyList())
        }
        fillerTerrains(14).toDomain(Civ3FormatEra.CONQUESTS, emptyList(), emptyList()).size shouldBe 14
    }

    test("toDomain maps scalar fields and reused value groups straight across, keyed by TerrainSlot") {
        // Mountains is TerrainSlot index 6 in every era: 6 fillers (indices 0-5), Mountains
        // (index 6), 5 more fillers (indices 7-11) = 12 entries total for VANILLA.
        val entries = fillerTerrains(6) + terrEntry(name = "Mountains", terrainFlags = 5) + fillerTerrains(5)
        val byIndex = entries.toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList())

        val mountains = byIndex.getValue(TerrainSlot.MOUNTAINS)
        mountains.name shouldBe "Mountains"
        mountains.terraformBonuses shouldBe TerrTerraformBonuses(irrigationBonus = 1, miningBonus = 2, roadBonus = 3)
        mountains.defenseBonus shouldBe 10
        mountains.tileValues shouldBe TerrTileValues(food = 1, shields = 0, commerce = 0)
        mountains.terrainFlags shouldBe 5
    }

    test("toDomain resolves possibleResources against the domain-converted GOOD list") {
        val iron = resource("Iron")
        val wine = resource("Wine")
        // bit 0 (Iron) and bit 2 (a 3rd resource not present) set; bit 1 (Wine) unset.
        // Coast is TerrainSlot index 9 in VANILLA: 9 fillers (indices 0-8), Coast (index 9),
        // 2 more fillers (indices 10-11) = 12 entries total.
        val entries = fillerTerrains(9) +
            terrEntry(name = "Coast", possibleResources = ByteString.of(0b101), numberOfPossibleResources = 3) +
            fillerTerrains(2)

        val coast = entries.toDomain(Civ3FormatEra.VANILLA, listOf(iron, wine), emptyList()).getValue(TerrainSlot.COAST)

        coast.possibleResources shouldBe mutableSetOf(iron)
    }

    test("toDomain resolves workerJobAllowed against the TFRM list, null when -1") {
        val clearForest = tfrmJob("Clear Forest")
        // Coast is TerrainSlot index 9 in VANILLA: 9 fillers (indices 0-8), Coast (index 9),
        // 2 more fillers (indices 10-11) = 12 entries total.
        val entries = fillerTerrains(9) + terrEntry(name = "Coast", workerJobAllowed = 0) + fillerTerrains(2)

        val coast = entries.toDomain(Civ3FormatEra.VANILLA, emptyList(), listOf(clearForest)).getValue(TerrainSlot.COAST)
        val desert = entries.toDomain(Civ3FormatEra.VANILLA, emptyList(), listOf(clearForest)).getValue(TerrainSlot.DESERT)

        coast.workerJobAllowed shouldBe clearForest
        desert.workerJobAllowed shouldBe null
    }

    test("toDomain resolves pollutionEffect: None, BaseTerrainType, and a specific sibling Terrain") {
        val entries = listOf(
            terrEntry(name = "Desert", pollutionEffect = -1),
            terrEntry(name = "Plains", pollutionEffect = 12), // this section's own size: BaseTerrainType
            terrEntry(name = "Grassland", pollutionEffect = 0), // points at Desert
        ) + fillerTerrains(9)
        val byIndex = entries.toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList())

        byIndex.getValue(TerrainSlot.DESERT).pollutionEffect shouldBe TerrPollutionEffect.None
        byIndex.getValue(TerrainSlot.PLAINS).pollutionEffect shouldBe TerrPollutionEffect.BaseTerrainType
        byIndex.getValue(TerrainSlot.GRASSLAND).pollutionEffect shouldBe
            TerrPollutionEffect.SpecificTerrain(byIndex.getValue(TerrainSlot.DESERT))
    }

    test("toDomain resolves landmark straight across, null pre-Conquests") {
        val landmark = TerrLandmark(
            landmarkEnabled = 1, tileValues = TerrTileValues(0, 0, 0),
            terraformBonuses = TerrTerraformBonuses(0, 0, 0), landmarkMovementBonus = 0,
            landmarkDefensiveBonus = 0, landmarkName = "The Pillars", landmarkCivilopediaEntry = "",
        )
        // Sea is TerrainSlot index 12 in CONQUESTS: 12 fillers (indices 0-11), Sea (index 12),
        // 1 more filler (index 13) = 14 entries total.
        val entries = fillerTerrains(12) + terrEntry(name = "Sea", landmark = landmark) + fillerTerrains(1)

        val sea = entries.toDomain(Civ3FormatEra.CONQUESTS, emptyList(), emptyList()).getValue(TerrainSlot.SEA)

        sea.landmark shouldBe landmark
    }

    test("toDomain().toWire() round-trips scalar fields, possibleResources, workerJobAllowed, pollutionEffect, and unknown/unknown2") {
        val iron = resource("Iron")
        val clearForest = tfrmJob("Clear Forest")
        val entries = listOf(
            terrEntry(
                name = "Desert", possibleResources = ByteString.of(1), numberOfPossibleResources = 1,
                workerJobAllowed = 0, pollutionEffect = -1, terrainFlags = 9,
                unknown = ByteString.of(0xCC.toByte(), 0xCC.toByte(), 0xCC.toByte(), 0xCC.toByte()),
                unknown2 = ByteString.of(1, 2, 3, 4),
            ),
            terrEntry(name = "Plains", possibleResources = ByteString.of(0), numberOfPossibleResources = 1, pollutionEffect = 0),
        ) + fillerTerrains(10, numberOfPossibleResources = 1)

        val roundTripped = entries.toDomain(Civ3FormatEra.VANILLA, listOf(iron), listOf(clearForest))
            .toWire(Civ3FormatEra.VANILLA, listOf(iron), listOf(clearForest))

        roundTripped shouldBe entries
    }

    test("toWire requires exactly the slot set valid for era") {
        // toDomain() itself requires exactly 12 entries for VANILLA — build a complete, valid map
        // first, then remove one key to construct the incomplete map toWire() must reject.
        val incomplete = fillerTerrains(12).toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList())
            .filterKeys { it != TerrainSlot.JUNGLE }

        shouldThrow<IllegalArgumentException> {
            incomplete.toWire(Civ3FormatEra.VANILLA, emptyList(), emptyList())
        }
    }

    test("toWire throws on a dangling possibleResources/workerJobAllowed/pollutionEffect reference") {
        val outsiderResource = Resource(name = "Outsider")
        val outsiderJob = tfrmJob("Outsider")
        val outsiderTerrain = Terrain(name = "Outsider", allowances = TerrAllowances(0, 0, null, null, null, null, null, null))

        val withResource = fillerTerrains(12).toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList()).toMutableMap()
        withResource[TerrainSlot.DESERT] = withResource.getValue(TerrainSlot.DESERT).copy(possibleResources = mutableSetOf(outsiderResource))
        shouldThrow<IllegalArgumentException> { withResource.toWire(Civ3FormatEra.VANILLA, emptyList(), emptyList()) }

        val withJob = fillerTerrains(12).toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList()).toMutableMap()
        withJob[TerrainSlot.DESERT] = withJob.getValue(TerrainSlot.DESERT).copy(workerJobAllowed = outsiderJob)
        shouldThrow<IllegalArgumentException> { withJob.toWire(Civ3FormatEra.VANILLA, emptyList(), emptyList()) }

        val withPollution = fillerTerrains(12).toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList()).toMutableMap()
        withPollution[TerrainSlot.DESERT] = withPollution.getValue(TerrainSlot.DESERT)
            .copy(pollutionEffect = TerrPollutionEffect.SpecificTerrain(outsiderTerrain))
        shouldThrow<IllegalArgumentException> { withPollution.toWire(Civ3FormatEra.VANILLA, emptyList(), emptyList()) }
    }

    test("toOrderedList returns Terrain values ordered by wire index for the given era") {
        val entries = listOf(terrEntry(name = "Desert"), terrEntry(name = "Plains")) + fillerTerrains(10)
        val byIndex = entries.toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList())

        val ordered = byIndex.toOrderedList(Civ3FormatEra.VANILLA)

        ordered.size shouldBe 12
        ordered[0] shouldBe byIndex.getValue(TerrainSlot.DESERT)
        ordered[1] shouldBe byIndex.getValue(TerrainSlot.PLAINS)
    }
})
