package com.kelvsyc.rifflet.riff

import com.kelvsyc.collections.listMultimapOf
import com.kelvsyc.collections.toListMultimap
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.LocalChunkParser
import com.kelvsyc.rifflet.core.RawChunk
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import okio.ByteString
import okio.ByteString.Companion.encodeUtf8

private fun id(name: String) = ChunkId(name)
private fun local(name: String, data: ByteString = ByteString.EMPTY) = RiffLocalChunk(RawChunk(id(name), data))
private fun form(name: String, vararg chunks: RiffChunk): RiffFormChunk {
    return RiffFormChunk(RiffChunkIds.RIFF, id(name), chunks.map { it.chunkId to it }.toListMultimap())
}

class RiffParserCoreTest : FunSpec({

    context("addLocalParser") {
        test("lambda overload routes chunk data to the parser function") {
            val payload = "Alice".encodeUtf8()
            var received: ByteString? = null
            val core = RiffParserCore.newCore {
                addLocalParser(id("INAM")) { data: ByteString -> received = data; "parsed" }
            }
            val parser = RiffFormParser(core) { it }
            val chunk = local("INAM", payload)
            parser.parse(listMultimapOf(chunk.chunkId to chunk))
            received shouldBe payload
        }

        test("direct LocalChunkParser overload is used as-is") {
            var called = false
            val directParser = object : LocalChunkParser<String> {
                override fun parse(data: ByteString): String { called = true; return "direct" }
            }
            val core = RiffParserCore.newCore { addLocalParser(id("INAM"), directParser) }
            val parser = RiffFormParser(core) { it }
            val chunk = local("INAM")
            val result = parser.parse(listMultimapOf(chunk.chunkId to chunk))
            called shouldBe true
            result[id("INAM")] shouldBe listOf("direct")
        }

        test("local chunk with no registered parser is left as RiffLocalChunk") {
            val core = RiffParserCore.newCore {}
            val parser = RiffFormParser(core) { it }
            val chunk = local("INAM")
            val result = parser.parse(listMultimapOf(chunk.chunkId to chunk))
            result[id("INAM")].single().shouldBeInstanceOf<RiffLocalChunk>()
        }
    }

    context("addFormParser assembler convenience") {
        test("creates a RiffFormParser wired to the core's local parsers") {
            val core = RiffParserCore.newCore {
                addLocalParser(id("fmt ")) { _: ByteString -> "parsed-fmt" }
                addFormParser(id("WAVE")) { chunks -> chunks[id("fmt ")].first() as String }
            }
            val fmtChunk = local("fmt ")
            val waveForm = form("WAVE", fmtChunk)
            val result = core.formParsers[id("WAVE")]!!.parse(waveForm.chunks)
            result shouldBe "parsed-fmt"
        }

        test("nested form is dispatched through the core recursively") {
            val core = RiffParserCore.newCore {
                addLocalParser(id("DATA")) { _: ByteString -> "val" }
                addFormParser(id("INNR")) { chunks -> "inner:" + chunks[id("DATA")].first() }
                addFormParser(id("OUTR")) { chunks -> "outer:" + chunks[id("INNR")].first() }
            }
            val dataChunk = local("DATA")
            val innerForm = form("INNR", dataChunk)
            val outerForm = form("OUTR", innerForm)
            val result = core.formParsers[id("OUTR")]!!.parse(outerForm.chunks)
            result shouldBe "outer:inner:val"
        }

        test("direct RiffFormChunkParser overload is used as-is") {
            var called = false
            val customParser = object : RiffFormChunkParser<String> {
                override fun parse(chunks: com.kelvsyc.collections.ListMultimap<ChunkId, RiffChunk>): String {
                    called = true; return "custom"
                }
            }
            val core = RiffParserCore.newCore { addFormParser(id("WAVE"), customParser) }
            core.formParsers[id("WAVE")]!!.parse(
                com.kelvsyc.collections.emptyListMultimap()
            )
            called shouldBe true
        }
    }

    context("addListParser assembler convenience") {
        test("creates a RiffListParser wired to the core's local parsers") {
            val core = RiffParserCore.newCore {
                addLocalParser(id("INAM")) { _: ByteString -> "name" }
                addListParser(id("INFO")) { chunks -> chunks[id("INAM")].first() as String }
            }
            val inamChunk = local("INAM")
            val infoList = com.kelvsyc.rifflet.riff.RiffListChunk(
                RiffChunkIds.LIST, id("INFO"),
                listOf(inamChunk.chunkId to inamChunk as RiffChunk).toListMultimap()
            )
            val result = core.listParsers[id("INFO")]!!.parse(infoList.chunks)
            result shouldBe "name"
        }
    }
})
