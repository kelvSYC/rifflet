package com.kelvsyc.rifflet.civ3

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validErasEntry(unknown: ByteString = ByteString.of(1, 0, 0, 0)) = ErasEntry(
    name = "Ancient",
    civilopediaEntry = "",
    researcher1 = "",
    researcher2 = "",
    researcher3 = "",
    researcher4 = "",
    researcher5 = "",
    numberOfUsedResearcherNames = 0,
    unknown = unknown,
)

class ErasEntryTest : FunSpec({

    test("a 4-byte unknown field is accepted") {
        validErasEntry().unknown.size shouldBe 4
    }

    test("an unknown field of any other size throws IllegalArgumentException") {
        shouldThrow<IllegalArgumentException> { validErasEntry(unknown = ByteString.of(1, 2)) }
    }
})
