package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun validSlocEntry(ownerType: Int = 0, owner: Int = 0): SlocEntry = SlocEntry(
    ownerType = ownerType,
    owner = owner,
    x = 0,
    y = 0,
)

class SlocEntryReferencesTest : FunSpec({

    test("resolveOwner delegates to the shared Owner resolution") {
        validSlocEntry(ownerType = 1, owner = 0).resolveOwner(emptyList()) shouldBe Owner.Barbarian
    }
})
