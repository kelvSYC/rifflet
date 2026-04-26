package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.internal.iff.IffBufferedChunkParser
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

class IffBufferedChunkParserTest : FunSpec({

    context("even-sized chunk") {
        test("type, declared size, and data are all read correctly") {
            val source = Buffer().apply {
                writeString("NAME", Charsets.ISO_8859_1)
                writeInt(4)
                writeString("DATA", Charsets.ISO_8859_1)
            }
            val chunk = IffBufferedChunkParser.parse(source)
            chunk.type shouldBe ChunkId("NAME")
            chunk.declaredSize shouldBe 4u
            chunk.data.readUtf8() shouldBe "DATA"
            source.exhausted() shouldBe true
        }
    }

    context("odd-sized chunk") {
        test("pad byte is consumed and not included in the data buffer") {
            val source = Buffer().apply {
                writeString("TEST", Charsets.ISO_8859_1)
                writeInt(3)
                write(byteArrayOf(0x01, 0x02, 0x03))
                writeByte(0x00) // pad byte
            }
            val chunk = IffBufferedChunkParser.parse(source)
            chunk.type shouldBe ChunkId("TEST")
            chunk.declaredSize shouldBe 3u
            chunk.data.size shouldBe 3
            source.exhausted() shouldBe true
        }
    }

    context("zero-size chunk") {
        test("chunk with size 0 has an empty data buffer") {
            val source = Buffer().apply {
                writeString("ZERO", Charsets.ISO_8859_1)
                writeInt(0)
            }
            val chunk = IffBufferedChunkParser.parse(source)
            chunk.type shouldBe ChunkId("ZERO")
            chunk.declaredSize shouldBe 0u
            chunk.data.exhausted() shouldBe true
            source.exhausted() shouldBe true
        }
    }

    context("blank chunk") {
        test("data bytes are skipped so the data buffer is empty") {
            val source = Buffer().apply {
                writeString("    ", Charsets.ISO_8859_1)
                writeInt(4)
                write(byteArrayOf(0x01, 0x02, 0x03, 0x04)) // will be skipped
            }
            val chunk = IffBufferedChunkParser.parse(source)
            chunk.type shouldBe IffChunkIds.blank
            chunk.declaredSize shouldBe 4u
            chunk.data.exhausted() shouldBe true
            source.exhausted() shouldBe true
        }

        test("odd-size blank chunk also consumes its pad byte") {
            val source = Buffer().apply {
                writeString("    ", Charsets.ISO_8859_1)
                writeInt(3)
                write(byteArrayOf(0x01, 0x02, 0x03)) // skipped
                writeByte(0x00) // pad byte
            }
            IffBufferedChunkParser.parse(source)
            source.exhausted() shouldBe true
        }
    }
})
