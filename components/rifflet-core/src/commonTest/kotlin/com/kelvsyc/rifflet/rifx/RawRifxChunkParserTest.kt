package com.kelvsyc.rifflet.rifx

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.internal.core.BufferedRawChunk
import com.kelvsyc.rifflet.internal.rifx.RawRifxChunkParser
import com.kelvsyc.rifflet.riff.RiffChunk
import com.kelvsyc.rifflet.riff.RiffChunkIds
import com.kelvsyc.rifflet.riff.RiffFormChunk
import com.kelvsyc.rifflet.riff.RiffListChunk
import com.kelvsyc.rifflet.riff.RiffLocalChunk
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

/** Encodes a single RIFX sub-chunk (type + BE size + data + optional pad byte) into a Buffer. */
private fun subChunk(type: String, data: ByteArray = byteArrayOf()): Buffer = Buffer().apply {
    writeString(type, Charsets.ISO_8859_1)
    writeInt(data.size)
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

class RawRifxChunkParserTest : FunSpec({

    context("RIFX chunk minimum size") {
        test("throws when declared size is 0") {
            shouldThrow<RiffletParseException> { RawRifxChunkParser.parse(emptyRaw(RifxChunkIds.RIFX)) }
        }

        test("throws when declared size is 3 (one byte short of content type ID)") {
            shouldThrow<RiffletParseException> {
                RawRifxChunkParser.parse(shortRaw(RifxChunkIds.RIFX, 0, 0, 0))
            }
        }
    }

    context("LIST chunk minimum size") {
        test("throws when declared size is 0") {
            shouldThrow<RiffletParseException> { RawRifxChunkParser.parse(emptyRaw(RiffChunkIds.LIST)) }
        }

        test("throws when declared size is 3") {
            shouldThrow<RiffletParseException> {
                RawRifxChunkParser.parse(shortRaw(RiffChunkIds.LIST, 0, 0, 0))
            }
        }
    }

    context("RIFX happy path") {
        test("empty body produces a RiffFormChunk with outerChunkId RIFX and no sub-chunks") {
            val raw = containerRaw(RifxChunkIds.RIFX, "WAVE")
            val result = RawRifxChunkParser.parse(raw).shouldBeInstanceOf<RiffFormChunk>()
            result.outerChunkId shouldBe RifxChunkIds.RIFX
            result.type shouldBe ChunkId("WAVE")
            result.chunks.size shouldBe 0
        }

        test("local sub-chunk is recorded in the chunk map") {
            val raw = containerRaw(RifxChunkIds.RIFX, "WAVE", subChunk("fmt "))
            val result = RawRifxChunkParser.parse(raw) as RiffFormChunk
            result.chunks.keys shouldBe setOf(ChunkId("fmt "))
        }

        test("nested LIST is parsed and recorded") {
            val listBody = Buffer().apply {
                writeString("INFO", Charsets.ISO_8859_1)
            }
            val listChunk = Buffer().apply {
                writeString("LIST", Charsets.ISO_8859_1)
                writeInt(listBody.size.toInt())
                writeAll(listBody)
            }
            val raw = containerRaw(RifxChunkIds.RIFX, "WAVE", listChunk)
            val result = RawRifxChunkParser.parse(raw) as RiffFormChunk
            result.chunks.keys shouldBe setOf(ChunkId("INFO"))
            result.chunks[ChunkId("INFO")].single().shouldBeInstanceOf<RiffListChunk>()
        }

        test("nested RIFX chunk inside RIFX throws") {
            val innerBody = Buffer().apply { writeString("WAVE", Charsets.ISO_8859_1) }
            val innerRifx = Buffer().apply {
                writeString("RIFX", Charsets.ISO_8859_1)
                writeInt(innerBody.size.toInt())
                writeAll(innerBody)
            }
            val raw = containerRaw(RifxChunkIds.RIFX, "AVI ", innerRifx)
            shouldThrow<RiffletParseException> { RawRifxChunkParser.parse(raw) }
        }

        test("JUNK padding chunk is silently dropped") {
            val raw = containerRaw(RifxChunkIds.RIFX, "WAVE",
                subChunk("JUNK", ByteArray(4)),
                subChunk("fmt "),
            )
            val result = RawRifxChunkParser.parse(raw) as RiffFormChunk
            result.chunks.keys shouldBe setOf(ChunkId("fmt "))
        }

        test("PAD padding chunk is silently dropped") {
            val raw = containerRaw(RifxChunkIds.RIFX, "WAVE",
                subChunk("PAD ", ByteArray(4)),
                subChunk("fmt "),
            )
            val result = RawRifxChunkParser.parse(raw) as RiffFormChunk
            result.chunks.keys shouldBe setOf(ChunkId("fmt "))
        }
    }

    context("LIST happy path") {
        test("empty body produces a RiffListChunk with the correct type and no sub-chunks") {
            val raw = containerRaw(RiffChunkIds.LIST, "INFO")
            val result = RawRifxChunkParser.parse(raw).shouldBeInstanceOf<RiffListChunk>()
            result.outerChunkId shouldBe RiffChunkIds.LIST
            result.type shouldBe ChunkId("INFO")
            result.chunks.size shouldBe 0
        }

        test("local sub-chunk inside LIST is recorded") {
            val raw = containerRaw(RiffChunkIds.LIST, "INFO", subChunk("INAM"))
            val result = RawRifxChunkParser.parse(raw) as RiffListChunk
            result.chunks.keys shouldBe setOf(ChunkId("INAM"))
        }

        test("nested LIST inside LIST is parsed and recorded") {
            val innerListBody = Buffer().apply { writeString("adtl", Charsets.ISO_8859_1) }
            val innerList = Buffer().apply {
                writeString("LIST", Charsets.ISO_8859_1)
                writeInt(innerListBody.size.toInt())
                writeAll(innerListBody)
            }
            val raw = containerRaw(RiffChunkIds.LIST, "INFO", innerList)
            val result = RawRifxChunkParser.parse(raw) as RiffListChunk
            result.chunks[ChunkId("adtl")].single().shouldBeInstanceOf<RiffListChunk>()
        }

        test("nested RIFX chunk inside LIST throws") {
            val innerBody = Buffer().apply { writeString("WAVE", Charsets.ISO_8859_1) }
            val innerRifx = Buffer().apply {
                writeString("RIFX", Charsets.ISO_8859_1)
                writeInt(innerBody.size.toInt())
                writeAll(innerBody)
            }
            val raw = containerRaw(RiffChunkIds.LIST, "INFO", innerRifx)
            shouldThrow<RiffletParseException> { RawRifxChunkParser.parse(raw) }
        }
    }

    context("local chunk at root") {
        test("unrecognised chunk produces a RiffLocalChunk") {
            val raw = BufferedRawChunk(ChunkId("fmt "), Buffer(), 0u)
            RawRifxChunkParser.parse(raw).shouldBeInstanceOf<RiffLocalChunk>()
        }
    }

    context("RIFF FourCC at root is treated as a local chunk, not a container") {
        test("a RIFF-tagged chunk is not parsed as a RIFX container") {
            val raw = BufferedRawChunk(RiffChunkIds.RIFF, Buffer().apply {
                writeString("WAVE", Charsets.ISO_8859_1)
            }, 4u)
            RawRifxChunkParser.parse(raw).shouldBeInstanceOf<RiffLocalChunk>()
        }
    }
})
