package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RawChunk
import com.kelvsyc.rifflet.internal.iff.IffBufferedChunkParser
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.toByteString

class IffChunkWriterTest : FunSpec({

    fun Buffer.readChunkId() = ChunkId(readInt())

    context("even-sized data") {
        test("type, size, and data are written with no pad byte") {
            val data = byteArrayOf(0x01, 0x02, 0x03, 0x04)
            val chunk = RawChunk(ChunkId("TEST"), data.toByteString())
            val out = Buffer()
            IffChunkWriter.writeChunk(chunk, out)
            out.readChunkId() shouldBe ChunkId("TEST")
            out.readInt() shouldBe 4
            out.readByteArray() shouldBe data
        }
    }

    context("odd-sized data") {
        test("a zero pad byte is appended after odd-sized data") {
            val data = byteArrayOf(0x01, 0x02, 0x03)
            val chunk = RawChunk(ChunkId("TEST"), data.toByteString())
            val out = Buffer()
            IffChunkWriter.writeChunk(chunk, out)
            out.readChunkId() shouldBe ChunkId("TEST")
            out.readInt() shouldBe 3
            out.readByte() shouldBe 0x01
            out.readByte() shouldBe 0x02
            out.readByte() shouldBe 0x03
            out.readByte() shouldBe 0x00 // pad
            out.exhausted() shouldBe true
        }
    }

    context("empty data") {
        test("size is written as zero and no data or pad bytes follow") {
            val chunk = RawChunk(ChunkId("EMPT"), ByteString.EMPTY)
            val out = Buffer()
            IffChunkWriter.writeChunk(chunk, out)
            out.readChunkId() shouldBe ChunkId("EMPT")
            out.readInt() shouldBe 0
            out.exhausted() shouldBe true
        }
    }

    context("declared size override") {
        test("declared size larger than actual data is zero-filled to the declared size") {
            val chunk = RawChunk(ChunkId("TEST"), byteArrayOf(0xAB.toByte(), 0xCD.toByte()).toByteString())
            val out = Buffer()
            IffChunkWriter.writeChunk(chunk, 4, out)
            out.readChunkId() shouldBe ChunkId("TEST")
            out.readInt() shouldBe 4
            out.readByte() shouldBe 0xAB.toByte()
            out.readByte() shouldBe 0xCD.toByte()
            out.readByte() shouldBe 0x00
            out.readByte() shouldBe 0x00
            out.exhausted() shouldBe true
        }

        test("odd declared size produces zero-fill and then a pad byte") {
            val chunk = RawChunk(ChunkId("TEST"), byteArrayOf(0x01, 0x02).toByteString())
            val out = Buffer()
            IffChunkWriter.writeChunk(chunk, 3, out) // ckSize = max(3, 2) = 3 (odd)
            out.readChunkId() shouldBe ChunkId("TEST")
            out.readInt() shouldBe 3
            out.readByte() shouldBe 0x01
            out.readByte() shouldBe 0x02
            out.readByte() shouldBe 0x00 // zero-fill
            out.readByte() shouldBe 0x00 // pad byte for odd ckSize
            out.exhausted() shouldBe true
        }

        test("declared size smaller than actual data is ignored and actual size is used") {
            val data = byteArrayOf(0x01, 0x02, 0x03, 0x04)
            val chunk = RawChunk(ChunkId("TEST"), data.toByteString())
            val out = Buffer()
            IffChunkWriter.writeChunk(chunk, 2, out)
            out.readChunkId() shouldBe ChunkId("TEST")
            out.readInt() shouldBe 4 // max(2, 4) = 4
            out.readByteArray() shouldBe data
        }
    }

    context("round-trip with IffBufferedChunkParser") {
        test("a chunk written by IffChunkWriter is read back intact") {
            val data = byteArrayOf(0x01, 0x02, 0x03) // odd size exercises padding
            val original = RawChunk(ChunkId("RTST"), data.toByteString())
            val buf = Buffer()
            IffChunkWriter.writeChunk(original, buf)
            val parsed = IffBufferedChunkParser.parse(buf)
            parsed.type shouldBe ChunkId("RTST")
            parsed.declaredSize shouldBe 3u
            parsed.data.readByteString() shouldBe data.toByteString()
            buf.exhausted() shouldBe true
        }
    }
})
