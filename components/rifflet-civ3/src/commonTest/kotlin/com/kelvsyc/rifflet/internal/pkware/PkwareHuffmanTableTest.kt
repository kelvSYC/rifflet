package com.kelvsyc.rifflet.internal.pkware

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PkwareHuffmanTableTest : FunSpec({

    test("length code lengths expand into the expected canonical Huffman table") {
        val table = constructHuffmanTable(LENGTH_CODE_LENGTHS, 16)
        table.counts shouldBe intArrayOf(0, 0, 1, 3, 3, 4, 3, 2, 0, 0, 0, 0, 0, 0)
        table.symbols shouldBe intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
    }

    test("literal code lengths construct without an over-subscribed error, covering all 256 byte values") {
        val table = constructHuffmanTable(LITERAL_CODE_LENGTHS, 256)
        table.counts[0] shouldBe 0
    }

    test("distance code lengths construct without an over-subscribed error, covering all 64 distance codes") {
        val table = constructHuffmanTable(DISTANCE_CODE_LENGTHS, 64)
        table.counts[0] shouldBe 0
    }
})
