package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.TerrAllowances
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun validTerrain(terrainFlags: Int = 0): Terrain = Terrain(
    name = "Desert",
    allowances = TerrAllowances(
        allowCities = 0, allowColonies = 0, impassable = null, impassableByWheeled = null,
        allowAirfields = null, allowForts = null, allowOutposts = null, allowRadarTowers = null,
    ),
    terrainFlags = terrainFlags,
)

class TerrainFlagsTest : FunSpec({

    test("causesDisease is settable and backed by terrainFlags bit 2") {
        val terrain = validTerrain()

        terrain.causesDisease shouldBe false
        terrain.causesDisease = true
        terrain.causesDisease shouldBe true
        terrain.terrainFlags shouldBe (1 shl 2)
    }

    test("curedBySanitation is settable and backed by terrainFlags bit 3") {
        val terrain = validTerrain()

        terrain.curedBySanitation = true
        terrain.terrainFlags shouldBe (1 shl 3)
        terrain.curedBySanitation shouldBe true
    }

    test("setting causesDisease/curedBySanitation preserves other terrainFlags bits") {
        val terrain = validTerrain(terrainFlags = 1 shl 1) // an unrelated, unnamed bit already set

        terrain.causesDisease = true
        terrain.curedBySanitation = true

        terrain.terrainFlags shouldBe ((1 shl 1) or (1 shl 2) or (1 shl 3))
    }

    test("clearing causesDisease clears only that bit") {
        val terrain = validTerrain()
        terrain.causesDisease = true
        terrain.curedBySanitation = true

        terrain.causesDisease = false

        terrain.causesDisease shouldBe false
        terrain.curedBySanitation shouldBe true
    }
})
