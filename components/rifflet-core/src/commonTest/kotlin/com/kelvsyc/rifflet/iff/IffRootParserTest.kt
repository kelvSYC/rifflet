package com.kelvsyc.rifflet.iff

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

private fun id(name: String) = ChunkId(name)

private fun <T> formParser(block: (ListMultimap<ChunkId, IffChunk>, ListMultimap<ChunkId, LocalChunk>) -> T) =
    object : FormChunkParser<T> {
        override fun parse(chunks: ListMultimap<ChunkId, IffChunk>, properties: ListMultimap<ChunkId, LocalChunk>) =
            block(chunks, properties)
    }

private fun formBinary(formType: String): Buffer {
    val body = Buffer().apply { writeString(formType, Charsets.ISO_8859_1) }
    return Buffer().apply {
        writeString("FORM", Charsets.ISO_8859_1)
        writeInt(body.size.toInt())
        writeAll(body)
    }
}

private fun localChunkBinary(type: String): Buffer {
    return Buffer().apply {
        writeString(type, Charsets.ISO_8859_1)
        writeInt(0)
    }
}

class IffRootParserTest : FunSpec({

    test("FormChunk root is dispatched to its registered parser") {
        val source = formBinary("TEST")
        val parser = IffRootParser.newParser<String> {
            addFormParser(id("TEST"), formParser { _, _ -> "parsed" })
        }
        parser.parse(source) shouldBe "parsed"
    }

    test("FormChunk root passes sub-chunks to the registered parser") {
        val innerChunk = Buffer().apply {
            writeString("NAME", Charsets.ISO_8859_1)
            writeInt(0)
        }
        val body = Buffer().apply {
            writeString("TEST", Charsets.ISO_8859_1)
            writeAll(innerChunk)
        }
        val source = Buffer().apply {
            writeString("FORM", Charsets.ISO_8859_1)
            writeInt(body.size.toInt())
            writeAll(body)
        }
        var receivedChunks: ListMultimap<ChunkId, IffChunk>? = null
        val parser = IffRootParser.newParser<Unit> {
            addFormParser(id("TEST"), formParser { chunks, _ -> receivedChunks = chunks })
        }
        parser.parse(source)
        receivedChunks?.keys shouldBe setOf(id("NAME"))
    }

    test("FormChunk root with no registered parser throws") {
        val source = formBinary("UNKN")
        val parser = IffRootParser.newParser<String> {}
        shouldThrow<IllegalStateException> { parser.parse(source) }
    }

    test("non-GroupChunk root throws") {
        val source = localChunkBinary("NAME")
        val parser = IffRootParser.newParser<String> {}
        shouldThrow<IllegalStateException> { parser.parse(source) }
    }
})
