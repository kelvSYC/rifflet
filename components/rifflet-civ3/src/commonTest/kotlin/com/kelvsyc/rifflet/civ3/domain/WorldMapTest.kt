package com.kelvsyc.rifflet.civ3.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

private fun grid(width: Int, height: Int): WorldMap {
    val tileCount = width * height / 2
    return WorldMap(width = width, height = height, tiles = (0 until tileCount).map { i -> Tile(textureLocation = i.toByte()) }.toMutableList())
}

class WorldMapTest : FunSpec({

    test("get/set round-trip through the same (x, y) coordinate") {
        val map = grid(6, 6)
        val replacement = Tile(pollution = true)

        map[2, 2] = replacement

        map[2, 2] shouldBe replacement
    }

    test("coordinatesOf is the inverse of get's indexing across a whole small map") {
        val map = grid(6, 6)
        for (y in 0 until 6) {
            for (x in 0 until 6) {
                if ((x - y) % 2 != 0) continue
                val tile = map[x, y]
                val index = map.tiles.indexOf(tile)
                map.coordinatesOf(index) shouldBe (x to y)
            }
        }
    }

    test("neighbors returns all 8 neighbors for an interior tile") {
        val map = grid(6, 6)
        val expected = listOf(-1 to -1, 1 to -1, -1 to 1, 1 to 1, -2 to 0, 2 to 0, 0 to -2, 0 to 2)
            .map { (dx, dy) -> map[2 + dx, 2 + dy] }

        map.neighbors(2, 2) shouldBe expected
    }

    test("neighbors omits a neighbor past a non-wrapping edge") {
        val map = grid(6, 6)

        map.neighbors(0, 2).size shouldBe 8 - 3
    }

    test("neighbors wraps a neighbor past the edge when xWrapping is set") {
        val map = grid(6, 6)
        map.xWrapping = true

        map.neighbors(0, 2) shouldContain map[4, 2]
    }

    test("neighbors wraps a neighbor past the edge when yWrapping is set") {
        val map = grid(6, 6)
        map.yWrapping = true

        map.neighbors(2, 0) shouldContain map[2, 4]
    }
})
