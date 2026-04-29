package com.kelvsyc.rifflet.riff

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletParseException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

private fun id(name: String) = ChunkId(name)

private fun <T> formParser(block: (ListMultimap<ChunkId, RiffChunk>) -> T) =
    object : RiffFormChunkParser<T> {
        override fun parse(chunks: ListMultimap<ChunkId, RiffChunk>) = block(chunks)
    }

/** Encodes a RIFF container: "RIFF" + LE size + formType + sub-chunks. */
private fun riffBinary(formType: String, vararg subChunks: Buffer): Buffer {
    val body = Buffer().apply {
        writeString(formType, Charsets.ISO_8859_1)
        subChunks.forEach { writeAll(it) }
    }
    return Buffer().apply {
        writeString("RIFF", Charsets.ISO_8859_1)
        writeIntLe(body.size.toInt())
        writeAll(body)
    }
}

/** Encodes a bare local chunk: type + LE size + body. */
private fun localChunkBinary(type: String, data: ByteArray = byteArrayOf()): Buffer = Buffer().apply {
    writeString(type, Charsets.ISO_8859_1)
    writeIntLe(data.size)
    write(data)
    if (data.size % 2 != 0) writeByte(0)
}

/** Encodes a LIST container: "LIST" + LE size + listType. */
private fun listBinary(listType: String): Buffer {
    val body = Buffer().apply { writeString(listType, Charsets.ISO_8859_1) }
    return Buffer().apply {
        writeString("LIST", Charsets.ISO_8859_1)
        writeIntLe(body.size.toInt())
        writeAll(body)
    }
}

class RiffRootParserTest : FunSpec({

    context("RIFF root") {
        test("is dispatched to its registered parser") {
            val parser = RiffRootParser.newParser<String> {
                root = RiffRootParser.Root(id("WAVE"))
                core { addFormParser(id("WAVE"), formParser { "parsed" }) }
            }
            parser.parse(riffBinary("WAVE")) shouldBe "parsed"
        }

        test("passes sub-chunks to the registered parser") {
            var receivedChunks: ListMultimap<ChunkId, RiffChunk>? = null
            val parser = RiffRootParser.newParser<Unit> {
                root = RiffRootParser.Root(id("WAVE"))
                core { addFormParser(id("WAVE"), formParser { chunks -> receivedChunks = chunks }) }
            }
            parser.parse(riffBinary("WAVE", localChunkBinary("fmt ")))
            receivedChunks?.keys shouldBe setOf(id("fmt "))
        }

        test("throws when no parser is registered for the form type") {
            val parser = RiffRootParser.newParser<String> {
                root = RiffRootParser.Root(id("WAVE"))
                core {}
            }
            shouldThrow<RiffletParseException> { parser.parse(riffBinary("WAVE")) }
        }

        test("throws when root form-type does not match declared root") {
            val parser = RiffRootParser.newParser<String> {
                root = RiffRootParser.Root(id("WAVE"))
                core { addFormParser(id("WAVE"), formParser { "parsed" }) }
            }
            shouldThrow<RiffletParseException> { parser.parse(riffBinary("AVI ")) }
        }

        test("throws when root chunk is not a RIFF chunk") {
            val parser = RiffRootParser.newParser<String> {
                root = RiffRootParser.Root(id("WAVE"))
                core {}
            }
            // A plain local chunk at root is not a valid RIFF root.
            shouldThrow<RiffletParseException> { parser.parse(localChunkBinary("WAVE")) }
        }
    }

    context("truncated input") {
        test("source ending before the size field throws RiffletParseException") {
            val parser = RiffRootParser.newParser<String> {
                root = RiffRootParser.Root(id("WAVE"))
                core {}
            }
            val truncated = Buffer().apply { writeString("RIFF", Charsets.ISO_8859_1) }
            shouldThrow<RiffletParseException> { parser.parse(truncated) }
        }

        test("source ending before all declared data bytes throws RiffletParseException") {
            val parser = RiffRootParser.newParser<String> {
                root = RiffRootParser.Root(id("WAVE"))
                core { addFormParser(id("WAVE"), formParser { "parsed" }) }
            }
            val truncated = Buffer().apply {
                writeString("RIFF", Charsets.ISO_8859_1)
                writeIntLe(100)
                writeString("WAVE", Charsets.ISO_8859_1)
            }
            shouldThrow<RiffletParseException> { parser.parse(truncated) }
        }
    }

    context("sub-chunk forwarding") {
        test("nested LIST sub-chunk is accessible via the chunks multimap") {
            var receivedChunks: ListMultimap<ChunkId, RiffChunk>? = null
            val parser = RiffRootParser.newParser<Unit> {
                root = RiffRootParser.Root(id("WAVE"))
                core { addFormParser(id("WAVE"), formParser { chunks -> receivedChunks = chunks }) }
            }
            parser.parse(riffBinary("WAVE", listBinary("INFO")))
            receivedChunks?.keys shouldBe setOf(id("INFO"))
        }
    }
})
