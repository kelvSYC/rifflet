package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun validEspnEntry(missionFlags: Int = 0): EspnEntry = EspnEntry(
    description = "",
    name = "",
    civilopediaEntry = "",
    missionFlags = missionFlags,
    baseCost = 0,
)

class EspnEntryFlagsTest : FunSpec({

    val properties: List<Pair<Int, (EspnEntry) -> Boolean>> = listOf(
        0 to EspnEntry::diplomat,
        1 to EspnEntry::spy,
    )

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validEspnEntry(missionFlags = 1 shl bit)
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validEspnEntry(missionFlags = allBits)
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validEspnEntry(missionFlags = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})
