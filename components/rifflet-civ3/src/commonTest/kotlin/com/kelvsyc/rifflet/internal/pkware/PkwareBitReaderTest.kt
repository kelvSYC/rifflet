package com.kelvsyc.rifflet.internal.pkware

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString.Companion.decodeHex

class PkwareBitReaderTest : FunSpec({

    test("bits reads least-significant bit first within a byte") {
        val reader = PkwareBitReader(Buffer().write("01".decodeHex()))
        reader.bits(1) shouldBe 1
        repeat(7) { reader.bits(1) shouldBe 0 }
    }

    test("bits reads multi-bit fields from the low end of the byte first") {
        val reader = PkwareBitReader(Buffer().write("b4".decodeHex()))
        reader.bits(4) shouldBe 4
        reader.bits(4) shouldBe 11
    }

    test("bits spans a byte boundary, buffering leftover bits for the next read") {
        val reader = PkwareBitReader(Buffer().write("b403".decodeHex()))
        reader.bits(12) shouldBe 948
        reader.bits(4) shouldBe 0
    }
})
