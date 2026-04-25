package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.internal.core.BufferedRawChunk
import com.kelvsyc.rifflet.internal.iff.RawIffChunkParser
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import okio.Buffer

private fun emptyRaw(id: ChunkId): BufferedRawChunk =
    BufferedRawChunk(id, Buffer(), 0u)

private fun shortRaw(id: ChunkId, vararg bytes: Byte): BufferedRawChunk {
    val buf = Buffer().apply { write(bytes) }
    return BufferedRawChunk(id, buf, bytes.size.toUInt())
}

class RawIffChunkParserTest : FunSpec({

    context("FORM chunk minimum size") {
        test("throws when declared size is 0") {
            shouldThrow<RiffletParseException> { RawIffChunkParser.parse(emptyRaw(IffChunkIds.FORM)) }
        }

        test("throws when declared size is 3 (one byte short of content type ID)") {
            shouldThrow<RiffletParseException> {
                RawIffChunkParser.parse(shortRaw(IffChunkIds.FORM, 0, 0, 0))
            }
        }
    }

    context("LIST chunk minimum size") {
        test("throws when declared size is 0") {
            shouldThrow<RiffletParseException> { RawIffChunkParser.parse(emptyRaw(IffChunkIds.LIST)) }
        }

        test("throws when declared size is 3") {
            shouldThrow<RiffletParseException> {
                RawIffChunkParser.parse(shortRaw(IffChunkIds.LIST, 0, 0, 0))
            }
        }
    }

    context("CAT chunk minimum size") {
        test("throws when declared size is 0") {
            shouldThrow<RiffletParseException> { RawIffChunkParser.parse(emptyRaw(IffChunkIds.CAT)) }
        }

        test("throws when declared size is 3") {
            shouldThrow<RiffletParseException> {
                RawIffChunkParser.parse(shortRaw(IffChunkIds.CAT, 0, 0, 0))
            }
        }
    }

    context("PROP chunk minimum size inside LIST") {
        test("throws when PROP declared size is too small for content type ID") {
            val prop = Buffer().apply {
                writeString("PROP", Charsets.ISO_8859_1)
                writeInt(2)
                write(byteArrayOf(0, 0))
                write(byteArrayOf(0))  // pad byte for odd size
            }
            val listBody = Buffer().apply {
                writeString("TEST", Charsets.ISO_8859_1)
                writeAll(prop)
            }
            val listRaw = BufferedRawChunk(IffChunkIds.LIST, listBody, listBody.size.toUInt())
            shouldThrow<RiffletParseException> { RawIffChunkParser.parse(listRaw) }
        }
    }

    context("PROP chunk minimum size inside CAT") {
        test("throws when PROP declared size is too small for content type ID") {
            val prop = Buffer().apply {
                writeString("PROP", Charsets.ISO_8859_1)
                writeInt(2)
                write(byteArrayOf(0, 0))
                write(byteArrayOf(0))  // pad byte for odd size
            }
            val catBody = Buffer().apply {
                writeString("TEST", Charsets.ISO_8859_1)
                writeAll(prop)
            }
            val catRaw = BufferedRawChunk(IffChunkIds.CAT, catBody, catBody.size.toUInt())
            shouldThrow<RiffletParseException> { RawIffChunkParser.parse(catRaw) }
        }
    }
})
