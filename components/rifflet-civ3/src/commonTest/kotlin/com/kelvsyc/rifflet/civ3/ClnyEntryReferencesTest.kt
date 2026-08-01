package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun validClnyEntry(
    ownerType: Int = 0,
    owner: Int = 0,
    improvementType: ClnyImprovementType = ClnyImprovementType.COLONY,
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