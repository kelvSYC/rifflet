package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validTechEntry(flags: Int = 0): TechEntry = TechEntry(
    name = "",
    civilopediaEntry = "",
    cost = 0,
    era = 0,
    advanceIcon = 0,
    x = 0,
    y = 0,
    prerequisite1 = 0,
    prerequisite2 = 0,
    prerequisite3 = 0,
    prerequisite4 = 0,
    flags = flags,
    flavors = 0,
    unknown = ByteString.of(0, 0, 0, 0),
)

class TechEntryFlagsTest : FunSpec({

    val properties: List<Pair<Int, (TechEntry) -> Boolean>> = listOf(
        0 to TechEntry::enablesDiplomats,
        1 to TechEntry::enablesIrrigationWithoutFreshWater,
        2 to TechEntry::enablesBridges,
        3 to TechEntry::disablesDiseasesFromFloodPlains,
        4 to TechEntry::enablesConscriptionOfUnits,
        5 to TechEntry::enablesMobilizationLevels,
        6 to TechEntry::enablesRecycling,
        7 to TechEntry::enablesPrecisionBombing,
        8 to TechEntry::enablesMutualProtectionPacts,
        9 to TechEntry::enablesRightOfPassageTreaties,
        10 to TechEntry::enablesMilitaryAlliances,
        11 to TechEntry::enablesTradeEmbargoes,
        12 to TechEntry::doublesEffectOfWealthImprovement,
        13 to TechEntry::enablesTradeOverSeaTiles,
        14 to TechEntry::enablesTradeOverOceanTiles,
        15 to TechEntry::enablesMapTrading,
        16 to TechEntry::enablesCommunicationTrading,
        17 to TechEntry::notRequiredForEraAdvancement,
        18 to TechEntry::doublesWorkRateOfWorkers,
    )

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validTechEntry(flags = 1 shl bit)
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validTechEntry(flags = allBits)
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validTechEntry(flags = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})
