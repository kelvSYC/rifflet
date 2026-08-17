package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.GoodResourceType
import com.kelvsyc.rifflet.civ3.WmapEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun wmapEntry(
    resourceIds: List<Int> = emptyList(),
    width: Int = 0,
    height: Int = 0,
    flags: Int = 0,
): WmapEntry = WmapEntry(
    resourceIds = resourceIds, numberOfContinents = 1, height = height, distanceBetweenCivs = 2,
    numberOfCivs = 3, unknown1 = ByteString.of(*ByteArray(8)), width = width,
    unknown2 = ByteString.of(*ByteArray(128)), mapSeed = 4, flags = flags,
)

private fun resource(name: String): Resource = Resource(name = name, type = GoodResourceType.BONUS)

class WmapEntryMappingTest : FunSpec({

    test("toDomain maps scalar fields straight across") {
        val entry = wmapEntry(width = 10, height = 10, flags = 5)

        val worldMap = listOf(entry).toDomain(emptyList(), emptyList()).single()

        worldMap.width shouldBe 10
        worldMap.height shouldBe 10
        worldMap.numberOfContinents shouldBe 1
        worldMap.distanceBetweenCivs shouldBe 2
        worldMap.numberOfCivs shouldBe 3
        worldMap.mapSeed shouldBe 4
        worldMap.flags shouldBe 5
    }

    test("toDomain carries the supplied tiles through unchanged") {
        val tiles = listOf(Tile(), Tile(pollution = true))
        val entry = wmapEntry()

        val worldMap = listOf(entry).toDomain(tiles, emptyList()).single()

        worldMap.tiles shouldBe tiles
    }

    test("toDomain resolves resources positionally, null for a dangling id") {
        val wine = resource("Wine")
        val entry = wmapEntry(resourceIds = listOf(0, 5))

        val worldMap = listOf(entry).toDomain(emptyList(), listOf(wine)).single()

        worldMap.resources shouldBe mutableListOf(wine, null)
    }

    test("toDomain().toWire() round-trips") {
        val wine = resource("Wine")
        val entries = listOf(wmapEntry(resourceIds = listOf(0), width = 6, height = 6, flags = 3))

        val roundTripped = entries.toDomain(emptyList(), listOf(wine)).toWire(listOf(wine))

        roundTripped shouldBe entries
    }

    test("toWire writes -1 for a null resource entry") {
        val entry = wmapEntry(resourceIds = listOf(-1))

        val worldMap = listOf(entry).toDomain(emptyList(), emptyList()).single()
        val wire = listOf(worldMap).toWire(emptyList()).single()

        wire.resourceIds shouldBe listOf(-1)
    }

    test("toWire throws on a dangling resource reference") {
        val wine = resource("Wine")
        val worldMap = listOf(wmapEntry(resourceIds = listOf(0))).toDomain(emptyList(), listOf(wine)).single()
        val outsider = resource("Outsider")
        worldMap.resources = mutableListOf(outsider)

        shouldThrow<IllegalArgumentException> { listOf(worldMap).toWire(listOf(wine)) }
    }
})
