package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validTileEntry(
    overlayFlags: Byte = 0,
    bonusFlags: Byte = 0,
    riverConnections: Byte = 0,
    riverCrossingFlags: Byte = 0,
): TileEntry = TileEntry(
    riverConnections = riverConnections,
    border = 0.toByte(),
    resource = 0,
    textureLocation = 0.toByte(),
    textureFile = 0.toByte(),
    unknown = ByteString.of(0, 0),
    overlayFlags = overlayFlags,
    terrain = 0.toByte(),
    bonusFlags = bonusFlags,
    riverCrossingFlags = riverCrossingFlags,
    barbarianTribe = 0.toShort(),
    colony = 0.toShort(),
    city = 0.toShort(),
    continent = 0.toShort(),
    unknown2 = ByteString.of(0),
    victoryPointLocation = 0.toShort(),
    ruin = 0,
    c3cOverlays = ByteString.of(0, 0, 0, 0),
    unknown3 = ByteString.of(0),
    c3cTerrain = 0.toByte(),
    unknown4 = ByteString.of(0, 0),
    fogOfWar = 0.toShort(),
    c3cBonuses = ByteString.of(0, 0, 0, 0),
    unknown5 = ByteString.of(0, 0),
    unknown6 = ByteString.of(0, 0, 0, 0),
)

class TileEntryOverlayFlagsTest : FunSpec({

    val properties: List<Pair<Int, (TileEntry) -> Boolean>> = listOf(
        0 to TileEntry::road,
        1 to TileEntry::railroad,
        2 to TileEntry::mine,
        3 to TileEntry::irrigation,
        4 to TileEntry::fortress,
        5 to TileEntry::goodyHuts,
        6 to TileEntry::pollution,
        7 to TileEntry::barbarianCamp,
    )

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validTileEntry(overlayFlags = (1 shl bit).toByte())
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validTileEntry(overlayFlags = allBits.toByte())
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validTileEntry(overlayFlags = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})

class TileEntryBonusFlagsTest : FunSpec({

    val properties: List<Pair<Int, (TileEntry) -> Boolean>> = listOf(
        0 to TileEntry::bonusGrassland,
        3 to TileEntry::playerStart,
        4 to TileEntry::snowCappedMountains,
        5 to TileEntry::pineForest,
    )

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validTileEntry(bonusFlags = (1 shl bit).toByte())
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validTileEntry(bonusFlags = allBits.toByte())
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validTileEntry(bonusFlags = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})

class TileEntryRiverConnectionsFlagsTest : FunSpec({

    val properties: List<Pair<Int, (TileEntry) -> Boolean>> = listOf(
        0 to TileEntry::riverInNorth,
        1 to TileEntry::riverInWest,
        2 to TileEntry::riverInEast,
        3 to TileEntry::riverInSouth,
    )

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validTileEntry(riverConnections = (1 shl bit).toByte())
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validTileEntry(riverConnections = allBits.toByte())
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validTileEntry(riverConnections = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})

class TileEntryRiverCrossingFlagsTest : FunSpec({

    val properties: List<Pair<Int, (TileEntry) -> Boolean>> = listOf(
        0 to TileEntry::crossingN,
        1 to TileEntry::crossingNe,
        2 to TileEntry::crossingE,
        3 to TileEntry::crossingSe,
        4 to TileEntry::crossingS,
        5 to TileEntry::crossingSw,
        6 to TileEntry::crossingW,
        7 to TileEntry::crossingNw,
    )

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validTileEntry(riverCrossingFlags = (1 shl bit).toByte())
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validTileEntry(riverCrossingFlags = allBits.toByte())
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validTileEntry(riverCrossingFlags = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})
