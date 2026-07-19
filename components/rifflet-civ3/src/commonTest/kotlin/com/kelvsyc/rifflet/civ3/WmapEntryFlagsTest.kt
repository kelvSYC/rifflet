package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validWmapEntry(flags: Int = 0): WmapEntry = WmapEntry(
    resourceIds = emptyList(),
    numberOfContinents = 0,
    height = 0,
    distanceBetweenCivs = 0,
    numberOfCivs = 0,
    unknown1 = ByteString.of(*ByteArray(8)),
    width = 0,
    unknown2 = ByteString.of(*ByteArray(128)),
    mapSeed = 0,
    flags = flags,
)

class WmapEntryFlagsTest : FunSpec({

    val properties: List<Pair<Int, (WmapEntry) -> Boolean>> = listOf(
        0 to WmapEntry::xWrapping,
        1 to WmapEntry::yWrapping,
        2 to WmapEntry::polarIceCaps,
    )

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validWmapEntry(flags = 1 shl bit)
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validWmapEntry(flags = allBits)
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validWmapEntry(flags = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})
