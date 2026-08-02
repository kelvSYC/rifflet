package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validWmapEntry(
    resourceIds: List<Int> = emptyList(),
    width: Int = 0,
    height: Int = 0,
    flags: Int = 0,
): WmapEntry = WmapEntry(
    resourceIds = resourceIds,
    numberOfContinents = 0,
    height = height,
    distanceBetweenCivs = 0,
    numberOfCivs = 0,
    unknown1 = ByteString.of(*ByteArray(8)),
    width = width,
    unknown2 = ByteString.of(*ByteArray(128)),
    mapSeed = 0,
    flags = flags,
)

private fun validGoodEntry(): GoodEntry = GoodEntry(
    name = "",
    civilopediaEntry = "",
    type = GoodResourceType.BONUS,
    appearanceRatio = 0,
    disappearanceProbability = 0,
    icon = 0,
    prerequisite = 0,
    foodBonus = 0,
    shieldsBonus = 0,
    commerceBonus = 0,
)

class WmapEntryReferencesTest : FunSpec({

    test("resourceIdsGood resolves each id, preserving position and length") {
        val good = validGoodEntry()
        val entry = validWmapEntry(resourceIds = listOf(0, 5))
        entry.resourceIdsGood(listOf(good)) shouldBe listOf(good, null)
    }

    test("tileCoordinates is the inverse of tileIndex across a whole small map") {
        val entry = validWmapEntry(width = 6, height = 6)
        for (y in 0 until 6) {
            for (x in 0 until 6) {
                if ((x - y) % 2 != 0) continue
                val index = entry.tileIndex(x, y)
                entry.tileCoordinates(index) shouldBe (x to y)
            }
        }
    }

    test("neighborTileIndices returns all 8 neighbors for an interior tile") {
        val entry = validWmapEntry(width = 6, height = 6)
        val deltas = listOf(-1 to -1, 1 to -1, -1 to 1, 1 to 1, -2 to 0, 2 to 0, 0 to -2, 0 to 2)
        val expected = deltas.map { (dx, dy) -> entry.tileIndex(2 + dx, 2 + dy) }
        entry.neighborTileIndices(2, 2) shouldBe expected
    }

    test("neighborTileIndices omits a neighbor past a non-wrapping edge") {
        val entry = validWmapEntry(width = 6, height = 6, flags = 0)
        // At x=0, the -1 and -2 dx deltas fall outside 0 until width and are omitted.
        entry.neighborTileIndices(0, 2).size shouldBe 8 - 3
    }

    test("neighborTileIndices wraps a neighbor past the edge when xWrapping is set") {
        val entry = validWmapEntry(width = 6, height = 6, flags = 1 shl 0)
        // At x=0 with xWrapping, dx=-2 wraps to x=width-2=4 instead of being omitted.
        entry.neighborTileIndices(0, 2) shouldContain entry.tileIndex(4, 2)
    }

    test("neighborTileIndices wraps a neighbor past the edge when yWrapping is set") {
        val entry = validWmapEntry(width = 6, height = 6, flags = 1 shl 1)
        // At y=0 with yWrapping, dy=-2 wraps to y=height-2=4 instead of being omitted.
        entry.neighborTileIndices(2, 0) shouldContain entry.tileIndex(2, 4)
    }
})
