package com.kelvsyc.rifflet.rifx

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.riff.RiffChunk
import com.kelvsyc.rifflet.riff.RiffFormChunkParser
import com.kelvsyc.rifflet.riff.RiffParserCore
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

private fun id(name: String) = ChunkId(name)

private fun <T> formParser(block: (ListMultimap<ChunkId, RiffChunk>) -> T) =
    object : RiffFormChunkParser<T> {
        override fun parse(chunks: ListMultimap<ChunkId, RiffChunk>) = block(chunks)
    }

/** Encodes a RIFX container: "RIFX" + BE size + formType + sub-chunks. */
private fun rifxBinary(formType: String, vararg subChunks: Buffer): Buffer {
    val body = Buffer().apply {
        writeString(formType, Charsets.ISO_8859_1)
        subChunks.forEach { writeAll(it) }
    }
    return Buffer().apply {
        writeString("RIFX", Charsets.ISO_8859_1)
        writeInt(body.size.toInt())
        writeAll(body)
    }
}

/** Encodes a bare local chunk: type + BE size + body + optional pad. */
private fun localChunkBinary(type: String, data: ByteArray = byteArrayOf()): Buffer = Buffer().apply {
    writeString(type, Charsets.ISO_8859_1)
    writeInt(data.size)
    write(data)
    if (data.size % 2 != 0) writeByte(0)
}

/** Encodes a LIST container: "LIST" + BE size + listType. */
private fun listBinary(listType: String): Buffer {
    val body = Buffer().apply { writeString(listType, Charsets.ISO_8859_1) }
    return Buffer().apply {
        writeString("LIST", Charsets.ISO_8859_1)
        writeInt(body.size.toInt())
        writeAll(body)
    }
}

class RifxRootParserTest : FunSpec({

    context("RIFX root") {
        test("is dispatched to its registered parser") {
            val parser = RifxRootParser.newParser<String> {
                root = RifxRootParser.Root(id("WAVE"))
                core { addFormParser(id("WAVE"), formParser { "parsed" }) }
            }
            parser.parse(rifxBinary("WAVE")) shouldBe "parsed"
        }

        test("passes sub-chunks to the registered parser") {
            var receivedChunks: ListMultimap<ChunkId, RiffChunk>? = null
            val parser = RifxRootParser.newParser<Unit> {
                root = RifxRootParser.Root(id("WAVE"))
                core { addFormParser(id("WAVE"), formParser { chunks -> receivedChunks = chunks }) }
            }
            parser.parse(rifxBinary("WAVE", localChunkBinary("fmt ")))
            receivedChunks?.keys shouldBe setOf(id("fmt "))
        }

        test("throws when no parser is registered for the form type") {
            val parser = RifxRootParser.newParser<String> {
                root = RifxRootParser.Root(id("WAVE"))
                core {}
            }
            shouldThrow<RiffletParseException> { parser.parse(rifxBinary("WAVE")) }
        }

        test("throws when root form-type does not match declared root") {
            val parser = RifxRootParser.newParser<String> {
                root = RifxRootParser.Root(id("WAVE"))
                core { addFormParser(id("WAVE"), formParser { "parsed" }) }
            }
            shouldThrow<RiffletParseException> { parser.parse(rifxBinary("AVI ")) }
        }

        test("throws when root chunk is not a RIFX chunk") {
            val parser = RifxRootParser.newParser<String> {
                root = RifxRootParser.Root(id("WAVE"))
                core {}
            }
            // A plain local chunk at root is not a valid RIFX root.
            shouldThrow<RiffletParseException> { parser.parse(localChunkBinary("WAVE")) }
        }

        test("throws when root carries RIFF FourCC instead of RIFX") {
            val riffRoot = Buffer().apply {
                writeString("RIFF", Charsets.ISO_8859_1)
                writeInt(4) // BE size (valid RIFX framing)
                writeString("WAVE", Charsets.ISO_8859_1)
            }
            val parser = RifxRootParser.newParser<String> {
                root = RifxRootParser.Root(id("WAVE"))
                core { addFormParser(id("WAVE"), formParser { "parsed" }) }
            }
            shouldThrow<RiffletParseException> { parser.parse(riffRoot) }
        }
    }

    context("truncated input") {
        test("source ending before the size field throws RiffletParseException") {
            val parser = RifxRootParser.newParser<String> {
                root = RifxRootParser.Root(id("WAVE"))
                core {}
            }
            val truncated = Buffer().apply { writeString("RIFX", Charsets.ISO_8859_1) }
            shouldThrow<RiffletParseException> { parser.parse(truncated) }
        }

        test("source ending before all declared data bytes throws RiffletParseException") {
            val parser = RifxRootParser.newParser<String> {
                root = RifxRootParser.Root(id("WAVE"))
                core { addFormParser(id("WAVE"), formParser { "parsed" }) }
            }
            val truncated = Buffer().apply {
                writeString("RIFX", Charsets.ISO_8859_1)
                writeInt(100)
                writeString("WAVE", Charsets.ISO_8859_1)
            }
            shouldThrow<RiffletParseException> { parser.parse(truncated) }
        }
    }

    context("sub-chunk forwarding") {
        test("nested LIST sub-chunk is accessible via the chunks multimap") {
            var receivedChunks: ListMultimap<ChunkId, RiffChunk>? = null
            val parser = RifxRootParser.newParser<Unit> {
                root = RifxRootParser.Root(id("WAVE"))
                core { addFormParser(id("WAVE"), formParser { chunks -> receivedChunks = chunks }) }
            }
            parser.parse(rifxBinary("WAVE", listBinary("INFO")))
            receivedChunks?.keys shouldBe setOf(id("INFO"))
        }
    }

    context("core builder overloads") {
        test("core(RiffParserCore) overload is accepted") {
            val core = RiffParserCore.newCore {
                addFormParser(id("WAVE"), formParser { "direct-core" })
            }
            val parser = RifxRootParser.newParser<String> {
                root = RifxRootParser.Root(id("WAVE"))
                core(core)
            }
            parser.parse(rifxBinary("WAVE")) shouldBe "direct-core"
        }
    }
})
