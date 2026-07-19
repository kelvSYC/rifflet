package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun validClnyEntry(ownerType: Int = 0, owner: Int = 0): ClnyEntry = ClnyEntry(
    ownerType = ownerType,
    owner = owner,
    x = 0,
    y = 0,
    improvementType = 0,
)

class ClnyEntryReferencesTest : FunSpec({

    test("resolveOwner delegates to the shared Owner resolution") {
        validClnyEntry(ownerType = 1, owner = 0).resolveOwner(emptyList()) shouldBe Owner.Barbarian
    }
})
