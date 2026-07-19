package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun validClnyEntry(
    ownerType: Int = 0,
    owner: Int = 0,
    improvementType: Int = 0,
): ClnyEntry = ClnyEntry(
    ownerType = ownerType,
    owner = owner,
    x = 0,
    y = 0,
    improvementType = improvementType,
)

class ClnyEntryReferencesTest : FunSpec({

    test("resolveOwner delegates to the shared Owner resolution") {
        validClnyEntry(ownerType = 1, owner = 0).resolveOwner(emptyList()) shouldBe Owner.Barbarian
    }
})

class ClnyEntryImprovementTypeTest : FunSpec({

    test("improvementTypeEnum maps each documented value") {
        validClnyEntry(improvementType = 0).improvementTypeEnum shouldBe ClnyImprovementType.COLONY
        validClnyEntry(improvementType = 1).improvementTypeEnum shouldBe ClnyImprovementType.AIRFIELD
        validClnyEntry(improvementType = 2).improvementTypeEnum shouldBe ClnyImprovementType.RADAR_TOWER
        validClnyEntry(improvementType = 3).improvementTypeEnum shouldBe ClnyImprovementType.OUTPOST
    }

    test("improvementTypeEnum is null for an out-of-range value") {
        validClnyEntry(improvementType = 4).improvementTypeEnum shouldBe null
    }
})
