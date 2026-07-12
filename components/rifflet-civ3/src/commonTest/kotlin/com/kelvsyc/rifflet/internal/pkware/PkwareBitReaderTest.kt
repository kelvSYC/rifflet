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

    test("decode reads successive canonical Huffman codes from a small synthetic table") {
        // Table: symbol 0 has a 1-bit code, symbols 1 and 2 share 2-bit codes.
        // Compact repeat form: (0 repeats<<4 | length 1) = 0x01, (1 repeat<<4 | length 2) = 0x12.
        val table = constructHuffmanTable(intArrayOf(0x01, 0x12), 3)
        val reader = PkwareBitReader(Buffer().write("05".decodeHex()))
        reader.decode(table) shouldBe 0
        reader.decode(table) shouldBe 1
        reader.decode(table) shouldBe 2
    }
})
