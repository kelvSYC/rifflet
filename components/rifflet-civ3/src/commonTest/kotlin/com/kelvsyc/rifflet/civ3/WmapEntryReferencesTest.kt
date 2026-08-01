package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validWmapEntry(resourceIds: List<Int> = emptyList()): WmapEntry = WmapEntry(
    resourceIds = resourceIds,
    numberOfContinents = 0,
    height = 0,
    distanceBetweenCivs = 0,
    numberOfCivs = 0,
    unknown1 = ByteString.of(*ByteArray(8)),
    width = 0,
    unknown2 = ByteString.of(*ByteArray(128)),
    mapSeed = 0,
    flags = 0,
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
})
