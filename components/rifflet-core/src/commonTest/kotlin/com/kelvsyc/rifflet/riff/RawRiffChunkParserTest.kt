package com.kelvsyc.rifflet.riff

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.internal.core.BufferedRawChunk
import com.kelvsyc.rifflet.internal.riff.RawRiffChunkParser
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import okio.Buffer

private fun emptyRaw(id: ChunkId): BufferedRawChunk =
    BufferedRawChunk(id, Buffer(), 0u)

private fun shortRaw(id: ChunkId, vararg bytes: Byte): BufferedRawChunk {
    val buf = Buffer().apply { write(bytes) }
    return BufferedRawChunk(id, buf, bytes.size.toUInt())
}

/** Encodes a single RIFF sub-chunk (type + LE size + data + optional pad byte) into a Buffer. */
private fun subChunk(type: String, data: ByteArray = byteArrayOf()): Buffer = Buffer().apply {
    writeString(type, Charsets.ISO_8859_1)
    writeIntLe(data.size)
    write(data)
    if (data.size % 2 != 0) writeByte(0)
}

/** Builds a BufferedRawChunk whose body is [contentType] followed by the concatenated [chunks] buffers. */
private fun containerRaw(id: ChunkId, contentType: String, vararg chunks: Buffer): BufferedRawChunk {
    val body = Buffer().apply {
        writeString(contentType, Charsets.ISO_8859_1)
        chunks.forEach { writeAll(it) }
    }
    return BufferedRawChunk(id, body, body.size.toUInt())
}

class RawRiffChunkParserTest : FunSpec({

    context("RIFF chunk minimum size") {
        test("throws when declared size is 0") {
            shouldThrow<RiffletParseException> { RawRiffChunkParser.parse(emptyRaw(RiffChunkIds.RIFF)) }
        }

        test("throws when declared size is 3 (one byte short of content type ID)") {
            shouldThrow<RiffletParseException> {
                RawRiffChunkParser.parse(shortRaw(RiffChunkIds.RIFF, 0, 0, 0))
            }
        }
    }

    context("LIST chunk minimum size") {
        test("throws when declared size is 0") {
            shouldThrow<RiffletParseException> { RawRiffChunkParser.parse(emptyRaw(RiffChunkIds.LIST)) }
        }

        test("throws when declared size is 3") {
            shouldThrow<RiffletParseException> {
                RawRiffChunkParser.parse(shortRaw(RiffChunkIds.LIST, 0, 0, 0))
            }
        }
    }

    context("RIFF happy path") {
        test("empty body produces a RiffFormChunk with the correct type and no sub-chunks") {
            val raw = containerRaw(RiffChunkIds.RIFF, "WAVE")
            val result = RawRiffChunkParser.parse(raw).shouldBeInstanceOf<RiffFormChunk>()
            result.outerChunkId shouldBe RiffChunkIds.RIFF
            result.type shouldBe ChunkId("WAVE")
            result.chunks.size shouldBe 0
        }

        test("local sub-chunk is recorded in the chunk map") {
            val raw = containerRaw(RiffChunkIds.RIFF, "WAVE", subChunk("fmt "))
            val result = RawRiffChunkParser.parse(raw) as RiffFormChunk
            result.chunks.keys shouldBe setOf(ChunkId("fmt "))
        }

        test("nested LIST is parsed and recorded") {
            val listBody = Buffer().apply {
                writeString("INFO", Charsets.ISO_8859_1)
            }
            val listChunk = Buffer().apply {
                writeString("LIST", Charsets.ISO_8859_1)
                writeIntLe(listBody.size.toInt())
                writeAll(listBody)
            }
            val raw = containerRaw(RiffChunkIds.RIFF, "WAVE", listChunk)
            val result = RawRiffChunkParser.parse(raw) as RiffFormChunk
            result.chunks.keys shouldBe setOf(ChunkId("INFO"))
            result.chunks[ChunkId("INFO")].single().shouldBeInstanceOf<RiffListChunk>()
        }

        test("nested RIFF chunk inside RIFF throws") {
            val innerBody = Buffer().apply { writeString("WAVE", Charsets.ISO_8859_1) }
            val innerRiff = Buffer().apply {
                writeString("RIFF", Charsets.ISO_8859_1)
                writeIntLe(innerBody.size.toInt())
                writeAll(innerBody)
            }
            val raw = containerRaw(RiffChunkIds.RIFF, "AVI ", innerRiff)
            shouldThrow<RiffletParseException> { RawRiffChunkParser.parse(raw) }
        }

        test("JUNK padding chunk is silently dropped") {
            val raw = containerRaw(RiffChunkIds.RIFF, "WAVE",
                subChunk("JUNK", ByteArray(4)),
                subChunk("fmt "),
            )
            val result = RawRiffChunkParser.parse(raw) as RiffFormChunk
            result.chunks.keys shouldBe setOf(ChunkId("fmt "))
        }

        test("PAD padding chunk is silently dropped") {
            val raw = containerRaw(RiffChunkIds.RIFF, "WAVE",
                subChunk("PAD ", ByteArray(4)),
                subChunk("fmt "),
            )
            val result = RawRiffChunkParser.parse(raw) as RiffFormChunk
            result.chunks.keys shouldBe setOf(ChunkId("fmt "))
        }
    }

    context("LIST happy path") {
        test("empty body produces a RiffListChunk with the correct type and no sub-chunks") {
            val raw = containerRaw(RiffChunkIds.LIST, "INFO")
            val result = RawRiffChunkParser.parse(raw).shouldBeInstanceOf<RiffListChunk>()
            result.outerChunkId shouldBe RiffChunkIds.LIST
            result.type shouldBe ChunkId("INFO")
            result.chunks.size shouldBe 0
        }

        test("local sub-chunk inside LIST is recorded") {
            val raw = containerRaw(RiffChunkIds.LIST, "INFO", subChunk("INAM"))
            val result = RawRiffChunkParser.parse(raw) as RiffListChunk
            result.chunks.keys shouldBe setOf(ChunkId("INAM"))
        }

        test("nested LIST inside LIST is parsed and recorded") {
            val innerListBody = Buffer().apply { writeString("adtl", Charsets.ISO_8859_1) }
            val innerList = Buffer().apply {
                writeString("LIST", Charsets.ISO_8859_1)
                writeIntLe(innerListBody.size.toInt())
                writeAll(innerListBody)
            }
            val raw = containerRaw(RiffChunkIds.LIST, "INFO", innerList)
            val result = RawRiffChunkParser.parse(raw) as RiffListChunk
            result.chunks[ChunkId("adtl")].single().shouldBeInstanceOf<RiffListChunk>()
        }

        test("nested RIFF chunk inside LIST throws") {
            val innerBody = Buffer().apply { writeString("WAVE", Charsets.ISO_8859_1) }
            val innerRiff = Buffer().apply {
                writeString("RIFF", Charsets.ISO_8859_1)
                writeIntLe(innerBody.size.toInt())
                writeAll(innerBody)
            }
            val raw = containerRaw(RiffChunkIds.LIST, "INFO", innerRiff)
            shouldThrow<RiffletParseException> { RawRiffChunkParser.parse(raw) }
        }
    }

    context("local and padding at root") {
        test("unrecognised chunk produces a RiffLocalChunk") {
            val raw = BufferedRawChunk(ChunkId("fmt "), Buffer(), 0u)
            RawRiffChunkParser.parse(raw).shouldBeInstanceOf<RiffLocalChunk>()
        }
    }
})
