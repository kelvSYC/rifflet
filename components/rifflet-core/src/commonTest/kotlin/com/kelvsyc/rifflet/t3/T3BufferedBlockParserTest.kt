package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.internal.t3.T3BufferedBlockParser
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

class T3BufferedBlockParserTest : FunSpec({

    context("even-sized block") {
        test("type, size, flags, and data are all read correctly") {
            val source = Buffer().apply {
                writeString("ENTP", Charsets.ISO_8859_1)
                writeIntLe(4)
                writeShortLe(0x0001)
                writeString("DATA", Charsets.ISO_8859_1)
            }
            val block = T3BufferedBlockParser.parse(source)
            block.type shouldBe ChunkId("ENTP")
            block.declaredSize shouldBe 4u
            block.flags shouldBe 0x0001
            block.data.readUtf8() shouldBe "DATA"
            source.exhausted() shouldBe true
        }
    }

    context("odd-sized block") {
        test("no pad byte is consumed") {
            val source = Buffer().apply {
                writeString("MCLD", Charsets.ISO_8859_1)
                writeIntLe(3)
                writeShortLe(0x0000)
                write(byteArrayOf(0x01, 0x02, 0x03))
                writeByte(0x7F) // start of the next block; must NOT be consumed as padding
            }
            val block = T3BufferedBlockParser.parse(source)
            block.declaredSize shouldBe 3u
            block.data.size shouldBe 3
            source.exhausted() shouldBe false
            source.readByte() shouldBe 0x7F.toByte()
        }
    }

    context("zero-size block") {
        test("block with size 0 has an empty data buffer") {
            val source = Buffer().apply {
                writeString("EOF ", Charsets.ISO_8859_1)
                writeIntLe(0)
                writeShortLe(0x0001)
            }
            val block = T3BufferedBlockParser.parse(source)
            block.declaredSize shouldBe 0u
            block.flags shouldBe 0x0001
            block.data.exhausted() shouldBe true
            source.exhausted() shouldBe true
        }
    }
})
