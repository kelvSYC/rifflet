package com.kelvsyc.rifflet.rifx

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.internal.rifx.RifxBufferedChunkParser
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

class RifxBufferedChunkParserTest : FunSpec({

    context("even-sized chunk") {
        test("type, declared size, and data are all read correctly") {
            val source = Buffer().apply {
                writeString("name", Charsets.ISO_8859_1)
                writeInt(4)
                writeString("DATA", Charsets.ISO_8859_1)
            }
            val chunk = RifxBufferedChunkParser.parse(source)
            chunk.type shouldBe ChunkId("name")
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
            val chunk = RifxBufferedChunkParser.parse(source)
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
            val chunk = RifxBufferedChunkParser.parse(source)
            chunk.type shouldBe ChunkId("ZERO")
            chunk.declaredSize shouldBe 0u
            chunk.data.exhausted() shouldBe true
            source.exhausted() shouldBe true
        }
    }

    context("size field endianness") {
        test("size is read as big-endian") {
            // Size = 256 (0x00000100) in big-endian is bytes [00 00 01 00]
            val source = Buffer().apply {
                writeString("TEST", Charsets.ISO_8859_1)
                writeByte(0x00); writeByte(0x00); writeByte(0x01); writeByte(0x00) // BE 256
                write(ByteArray(256))
            }
            val chunk = RifxBufferedChunkParser.parse(source)
            chunk.declaredSize shouldBe 256u
            chunk.data.size shouldBe 256
            source.exhausted() shouldBe true
        }

        test("same byte sequence is misread as 65536 by a little-endian parser") {
            // Confirms that [00 00 01 00] means 256 big-endian but 65536 little-endian
            val source = Buffer().apply {
                writeString("TEST", Charsets.ISO_8859_1)
                writeByte(0x00); writeByte(0x00); writeByte(0x01); writeByte(0x00) // BE 256 / LE 65536
                write(ByteArray(256))
            }
            val chunk = RifxBufferedChunkParser.parse(source)
            chunk.declaredSize shouldBe 256u // not 65536
        }
    }
})
