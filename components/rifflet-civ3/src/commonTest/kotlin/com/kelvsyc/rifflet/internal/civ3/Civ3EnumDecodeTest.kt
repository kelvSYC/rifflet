package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3EnumDecodeException
import com.kelvsyc.rifflet.civ3.Civ3SectionIds
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private enum class DecodeTestFixture { FIRST, SECOND, THIRD }

class Civ3EnumDecodeTest : FunSpec({

    test("a raw value within range decodes to the matching entry") {
        decodeEnum("test.field", 1, DecodeTestFixture.entries) shouldBe DecodeTestFixture.SECOND
    }

    test("a negative raw value throws Civ3EnumDecodeException carrying the field and raw value") {
        val e = shouldThrow<Civ3EnumDecodeException> { decodeEnum("test.field", -1, DecodeTestFixture.entries) }
        e.field shouldBe "test.field"
        e.rawValue shouldBe -1
        e.section shouldBe null
        e.index shouldBe null
    }

    test("a raw value past the end throws Civ3EnumDecodeException") {
        shouldThrow<Civ3EnumDecodeException> { decodeEnum("test.field", 3, DecodeTestFixture.entries) }
    }

    test("a custom indexOf mapping is applied before lookup") {
        decodeEnum("test.field", -1, DecodeTestFixture.entries) { it + 1 } shouldBe DecodeTestFixture.FIRST
    }

    test("the exception message names the field, raw value, and enum") {
        val e = shouldThrow<Civ3EnumDecodeException> { decodeEnum("test.field", 5, DecodeTestFixture.entries) }
        e.message shouldBe "test.field=5 does not decode to a known DecodeTestFixture (3 possible values)"
    }

    test("enriching an exception with section and index prefixes the message and preserves the cause") {
        val inner = shouldThrow<Civ3EnumDecodeException> { decodeEnum("test.field", 5, DecodeTestFixture.entries) }
        val enriched = Civ3EnumDecodeException(Civ3SectionIds.GOOD, 2, inner)
        enriched.section shouldBe Civ3SectionIds.GOOD
        enriched.index shouldBe 2
        enriched.field shouldBe "test.field"
        enriched.rawValue shouldBe 5
        enriched.cause shouldBe inner
        enriched.message shouldBe "GOOD[2]: ${inner.message}"
    }
})
