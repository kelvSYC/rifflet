package com.kelvsyc.rifflet.riff

import com.kelvsyc.kotlin.core.collections.ListMultimap
import com.kelvsyc.kotlin.core.collections.emptyListMultimap
import com.kelvsyc.kotlin.core.collections.listMultimapOf
import com.kelvsyc.kotlin.core.collections.toListMultimap
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.LocalChunkParser
import com.kelvsyc.rifflet.core.RawChunk
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import okio.ByteString

private fun id(name: String) = ChunkId(name)
private fun local(name: String) = RiffLocalChunk(RawChunk(id(name), ByteString.EMPTY))
private fun form(name: String, vararg chunks: RiffChunk): RiffFormChunk {
    val multimap = chunks.map { it.chunkId to it }.toListMultimap()
    return RiffFormChunk(RiffChunkIds.RIFF, id(name), multimap)
}
private fun list(name: String, vararg chunks: RiffChunk): RiffListChunk {
    val multimap = chunks.map { it.chunkId to it }.toListMultimap()
    return RiffListChunk(RiffChunkIds.LIST, id(name), multimap)
}

private fun localParser(block: (ByteString) -> Any) = object : LocalChunkParser<Any> {
    override fun parse(data: ByteString) = block(data)
}
private fun <T> formParser(block: (ListMultimap<ChunkId, RiffChunk>) -> T) =
    object : RiffFormChunkParser<T> {
        override fun parse(chunks: ListMultimap<ChunkId, RiffChunk>) = block(chunks)
    }
private fun <T> listParser(block: (ListMultimap<ChunkId, RiffChunk>) -> T) =
    object : RiffListChunkParser<T> {
        override fun parse(chunks: ListMultimap<ChunkId, RiffChunk>) = block(chunks)
    }

private fun core(
    formParsers: Map<ChunkId, RiffFormChunkParser<*>> = emptyMap(),
    listParsers: Map<ChunkId, RiffListChunkParser<*>> = emptyMap(),
    localParsers: Map<ChunkId, LocalChunkParser<*>> = emptyMap(),
): RiffParserCore = object : RiffParserCore {
    override val formParsers = formParsers
    override val listParsers = listParsers
    override val localParsers = localParsers
}

class RiffFormParserTest : FunSpec({

    context("local chunk parsing") {
        test("local chunk is parsed by its registered parser") {
            val chunk = local("fmt ")
            val core = core(localParsers = mapOf(id("fmt ") to localParser { "parsed" }))
            val parser = RiffFormParser(core) { it }
            val result = parser.parse(listMultimapOf(chunk.chunkId to chunk))
            result[id("fmt ")] shouldBe listOf("parsed")
        }

        test("local chunk with no registered parser is left unparsed") {
            val chunk = local("fmt ")
            val parser = RiffFormParser(core()) { it }
            val result = parser.parse(listMultimapOf(chunk.chunkId to chunk))
            result[id("fmt ")] shouldBe listOf(chunk)
        }
    }

    context("group chunk parsing") {
        test("nested RiffFormChunk is parsed by its registered formParser") {
            val inner = form("WAVE")
            val core = core(formParsers = mapOf(id("WAVE") to formParser { "parsed-form" }))
            val parser = RiffFormParser(core) { it }
            val result = parser.parse(listMultimapOf(inner.chunkId to inner))
            result[id("WAVE")] shouldBe listOf("parsed-form")
        }

        test("nested RiffFormChunk with no registered parser is left unparsed") {
            val inner = form("WAVE")
            val parser = RiffFormParser(core()) { it }
            val result = parser.parse(listMultimapOf(inner.chunkId to inner))
            result[id("WAVE")] shouldBe listOf(inner)
        }

        test("nested RiffListChunk is parsed by its registered listParser") {
            val inner = list("INFO")
            val core = core(listParsers = mapOf(id("INFO") to listParser { "parsed-list" }))
            val parser = RiffFormParser(core) { it }
            val result = parser.parse(listMultimapOf(inner.chunkId to inner))
            result[id("INFO")] shouldBe listOf("parsed-list")
        }

        test("nested RiffListChunk with no registered parser is left unparsed") {
            val inner = list("INFO")
            val parser = RiffFormParser(core()) { it }
            val result = parser.parse(listMultimapOf(inner.chunkId to inner))
            result[id("INFO")] shouldBe listOf(inner)
        }
    }

    context("assembler") {
        test("assembler receives the parsed multimap") {
            var received: ListMultimap<ChunkId, Any>? = null
            val parser = RiffFormParser<Unit>(core()) { received = it }
            parser.parse(emptyListMultimap())
            received shouldBe emptyListMultimap()
        }
    }
})

class RiffListParserTest : FunSpec({

    context("local chunk parsing") {
        test("local chunk is parsed by its registered parser") {
            val chunk = local("INAM")
            val core = core(localParsers = mapOf(id("INAM") to localParser { "parsed" }))
            val parser = RiffListParser(core) { it }
            val result = parser.parse(listMultimapOf(chunk.chunkId to chunk))
            result[id("INAM")] shouldBe listOf("parsed")
        }
    }

    context("nested container parsing") {
        test("nested RiffListChunk is parsed by its registered listParser") {
            val inner = list("adtl")
            val core = core(listParsers = mapOf(id("adtl") to listParser { "parsed-list" }))
            val parser = RiffListParser(core) { it }
            val result = parser.parse(listMultimapOf(inner.chunkId to inner))
            result[id("adtl")] shouldBe listOf("parsed-list")
        }
    }

    context("assembler") {
        test("assembler is called with all parsed items") {
            val a = local("INAM")
            val b = local("ICMT")
            val core = core(
                localParsers = mapOf(
                    id("INAM") to localParser { "name" },
                    id("ICMT") to localParser { "comment" },
                )
            )
            var received: ListMultimap<ChunkId, Any>? = null
            val parser = RiffListParser(core) { received = it; it }
            parser.parse(listMultimapOf(a.chunkId to a, b.chunkId to b))
            received?.get(id("INAM")) shouldBe listOf("name")
            received?.get(id("ICMT")) shouldBe listOf("comment")
        }
    }
})
