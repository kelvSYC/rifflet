package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.internal.core.BufferedRawChunk
import com.kelvsyc.rifflet.internal.iff.RawIffChunkParser
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

/** Encodes a single IFF sub-chunk (type + size + data + optional pad byte) into a Buffer. */
private fun subChunk(type: String, data: ByteArray = byteArrayOf()): Buffer = Buffer().apply {
    writeString(type, Charsets.ISO_8859_1)
    writeInt(data.size)
    write(data)
    if (data.size % 2 != 0) writeByte(0)
}

/** Builds a BufferedRawChunk whose body is [contentType] followed by the concatenated [chunks] buffers. */
private fun groupRaw(id: ChunkId, contentType: String, vararg chunks: Buffer): BufferedRawChunk {
    val body = Buffer().apply {
        writeString(contentType, Charsets.ISO_8859_1)
        chunks.forEach { writeAll(it) }
    }
    return BufferedRawChunk(id, body, body.size.toUInt())
}

/** Encodes a PROP sub-chunk with [formType] and optional inner local-chunk buffers. */
private fun propChunk(formType: String, vararg localChunks: Buffer): Buffer {
    val inner = Buffer().apply {
        writeString(formType, Charsets.ISO_8859_1)
        localChunks.forEach { writeAll(it) }
    }
    val size = inner.size.toInt()
    return Buffer().apply {
        writeString("PROP", Charsets.ISO_8859_1)
        writeInt(size)
        writeAll(inner)
        if (size % 2 != 0) writeByte(0)
    }
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

    context("FORM happy path") {
        test("empty FORM body produces a FormChunk with the correct type and no sub-chunks") {
            val raw = groupRaw(IffChunkIds.FORM, "TEST")
            val result = RawIffChunkParser.parse(raw).shouldBeInstanceOf<FormChunk>()
            result.outerChunkId shouldBe IffChunkIds.FORM
            result.type shouldBe ChunkId("TEST")
            result.chunks.size shouldBe 0
        }

        test("local sub-chunk is recorded in the FormChunk's chunk map") {
            val raw = groupRaw(IffChunkIds.FORM, "TEST", subChunk("NAME"))
            val result = RawIffChunkParser.parse(raw) as FormChunk
            result.chunks.keys shouldBe setOf(ChunkId("NAME"))
        }

        test("blank sub-chunk inside FORM is silently dropped") {
            val raw = groupRaw(IffChunkIds.FORM, "TEST", subChunk("    ", byteArrayOf(0, 0, 0, 0)))
            val result = RawIffChunkParser.parse(raw) as FormChunk
            result.chunks.size shouldBe 0
        }

        test("PROP sub-chunk inside FORM throws") {
            val raw = groupRaw(IffChunkIds.FORM, "TEST", propChunk("BODY"))
            shouldThrow<RiffletParseException> { RawIffChunkParser.parse(raw) }
        }
    }

    context("LIST happy path") {
        test("empty LIST body produces a ListChunk with no properties and no items") {
            val raw = groupRaw(IffChunkIds.LIST, "TEST")
            val result = RawIffChunkParser.parse(raw).shouldBeInstanceOf<ListChunk>()
            result.outerChunkId shouldBe IffChunkIds.LIST
            result.type shouldBe ChunkId("TEST")
            result.properties shouldBe emptyMap()
            result.items shouldBe emptyList()
        }

        test("PROP chunk captures its local chunks as properties keyed by form-type") {
            val raw = groupRaw(IffChunkIds.LIST, "TEST", propChunk("BODY", subChunk("AUTH")))
            val result = RawIffChunkParser.parse(raw) as ListChunk
            result.properties.keys shouldBe setOf(ChunkId("BODY"))
            result.properties[ChunkId("BODY")]!!.map { it.chunkId } shouldBe listOf(ChunkId("AUTH"))
        }

        test("group item after PROP is added to the items list") {
            val raw = groupRaw(IffChunkIds.LIST, "TEST",
                propChunk("ATYP"),
                subChunk("FORM", "ATYP".toByteArray(Charsets.ISO_8859_1))
            )
            val result = RawIffChunkParser.parse(raw) as ListChunk
            result.items.size shouldBe 1
        }

        test("PROP after a group chunk throws") {
            val raw = groupRaw(IffChunkIds.LIST, "TEST",
                subChunk("FORM", "ATYP".toByteArray(Charsets.ISO_8859_1)),
                propChunk("BODY")
            )
            shouldThrow<RiffletParseException> { RawIffChunkParser.parse(raw) }
        }

        test("duplicate PROP for the same form-type throws") {
            val raw = groupRaw(IffChunkIds.LIST, "TEST", propChunk("BODY"), propChunk("BODY"))
            shouldThrow<RiffletParseException> { RawIffChunkParser.parse(raw) }
        }

        test("non-group local chunk after PROP throws") {
            val raw = groupRaw(IffChunkIds.LIST, "TEST", propChunk("BODY"), subChunk("NAME"))
            shouldThrow<RiffletParseException> { RawIffChunkParser.parse(raw) }
        }

        test("group chunk inside PROP throws") {
            val raw = groupRaw(IffChunkIds.LIST, "TEST",
                propChunk("BODY", subChunk("FORM", "ATYP".toByteArray(Charsets.ISO_8859_1)))
            )
            shouldThrow<RiffletParseException> { RawIffChunkParser.parse(raw) }
        }
    }

    context("CAT happy path") {
        test("empty CAT body produces a CatChunk with no properties and no chunks") {
            val raw = groupRaw(IffChunkIds.CAT, "HINT")
            val result = RawIffChunkParser.parse(raw).shouldBeInstanceOf<CatChunk>()
            result.outerChunkId shouldBe IffChunkIds.CAT
            result.hint shouldBe ChunkId("HINT")
            result.properties shouldBe emptyMap()
            result.chunks shouldBe emptyList()
        }

        test("PROP after a group chunk in CAT throws") {
            val raw = groupRaw(IffChunkIds.CAT, "HINT",
                subChunk("FORM", "ATYP".toByteArray(Charsets.ISO_8859_1)),
                propChunk("BODY")
            )
            shouldThrow<RiffletParseException> { RawIffChunkParser.parse(raw) }
        }

        test("duplicate PROP for the same form-type in CAT throws") {
            val raw = groupRaw(IffChunkIds.CAT, "HINT", propChunk("BODY"), propChunk("BODY"))
            shouldThrow<RiffletParseException> { RawIffChunkParser.parse(raw) }
        }

        test("non-group local chunk in CAT throws") {
            val raw = groupRaw(IffChunkIds.CAT, "HINT", subChunk("NAME"))
            shouldThrow<RiffletParseException> { RawIffChunkParser.parse(raw) }
        }

        test("group chunk inside PROP in CAT throws") {
            val raw = groupRaw(IffChunkIds.CAT, "HINT",
                propChunk("BODY", subChunk("FORM", "ATYP".toByteArray(Charsets.ISO_8859_1)))
            )
            shouldThrow<RiffletParseException> { RawIffChunkParser.parse(raw) }
        }
    }

    context("local and blank at root") {
        test("local chunk produces a LocalChunk wrapping the raw data") {
            val raw = BufferedRawChunk(ChunkId("NAME"), Buffer(), 0u)
            RawIffChunkParser.parse(raw).shouldBeInstanceOf<LocalChunk>()
        }

        test("blank chunk produces a BlankChunk with the declared size") {
            val raw = BufferedRawChunk(IffChunkIds.blank, Buffer(), 8u)
            RawIffChunkParser.parse(raw).shouldBeInstanceOf<BlankChunk>().size shouldBe 8u
        }
    }

    context("variant chunk IDs") {
        test("FOR1 minimum size is enforced") {
            shouldThrow<RiffletParseException> { RawIffChunkParser.parse(emptyRaw(ChunkId("FOR1"))) }
        }

        test("FOR1 is parsed as a FORM variant with outerChunkId = FOR1") {
            val raw = groupRaw(ChunkId("FOR1"), "TEST")
            val result = RawIffChunkParser.parse(raw).shouldBeInstanceOf<FormChunk>()
            result.outerChunkId shouldBe ChunkId("FOR1")
            result.type shouldBe ChunkId("TEST")
        }

        test("LIS1 is parsed as a LIST variant with outerChunkId = LIS1") {
            val raw = groupRaw(ChunkId("LIS1"), "TEST")
            RawIffChunkParser.parse(raw).shouldBeInstanceOf<ListChunk>().outerChunkId shouldBe ChunkId("LIS1")
        }

        test("CAT1 is parsed as a CAT variant with outerChunkId = CAT1") {
            val raw = groupRaw(ChunkId("CAT1"), "HINT")
            RawIffChunkParser.parse(raw).shouldBeInstanceOf<CatChunk>().outerChunkId shouldBe ChunkId("CAT1")
        }
    }
})
