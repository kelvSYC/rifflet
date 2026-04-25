package com.kelvsyc.rifflet.iff

import com.kelvsyc.collections.emptyListMultimap
import com.kelvsyc.collections.listMultimapOf
import com.kelvsyc.collections.toListMultimap
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RawChunk
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import okio.ByteString
import okio.ByteString.Companion.encodeUtf8

private fun id(name: String) = ChunkId(name)
private fun local(name: String, data: ByteString = ByteString.EMPTY) = LocalChunk(RawChunk(id(name), data))
private fun form(name: String, vararg chunks: IffChunk): FormChunk {
    return FormChunk(IffChunkIds.FORM, id(name), chunks.map { it.chunkId to it }.toListMultimap())
}

class IffParserCoreTest : FunSpec({

    context("addLocalParser") {
        test("lambda overload routes chunk data to the parser function") {
            val payload = "Alice".encodeUtf8()
            var received: ByteString? = null
            val core = IffParserCore.newCore {
                addLocalParser(id("NAME")) { data: ByteString -> received = data; "parsed" }
            }
            val parser = FormParser(core) { it }
            val chunk = local("NAME", payload)

            parser.parse(listMultimapOf(chunk.chunkId to chunk))

            received shouldBe payload
        }

        test("direct LocalChunkParser overload is used as-is") {
            var called = false
            val directParser = object : LocalChunkParser<String> {
                override fun parse(data: ByteString): String { called = true; return "direct" }
            }
            val core = IffParserCore.newCore {
                addLocalParser(id("NAME"), directParser)
            }
            val parser = FormParser(core) { it }
            val chunk = local("NAME")

            val result = parser.parse(listMultimapOf(chunk.chunkId to chunk))

            called shouldBe true
            result[id("NAME")] shouldBe listOf("direct")
        }

        test("local chunk with no registered parser is left as LocalChunk") {
            val core = IffParserCore.newCore { }
            val parser = FormParser(core) { it }
            val chunk = local("NAME")

            val result = parser.parse(listMultimapOf(chunk.chunkId to chunk))

            result[id("NAME")].single().shouldBeInstanceOf<LocalChunk>()
        }
    }

    context("addFormParser assembler convenience") {
        test("creates a FormParser wired to the core's local parsers") {
            val core = IffParserCore.newCore {
                addLocalParser(id("NAME")) { _: ByteString -> "parsed-name" }
                addFormParser(id("BODY")) { chunks -> chunks[id("NAME")].first() as String }
            }

            val nameChunk = local("NAME")
            val bodyForm = form("BODY", nameChunk)

            val result = core.formParsers[id("BODY")]!!.parse(bodyForm.chunks)

            result shouldBe "parsed-name"
        }

        test("nested FORM is dispatched through the core recursively") {
            val core = IffParserCore.newCore {
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

        test("direct FormChunkParser overload is used as-is") {
            var called = false
            val customParser = object : FormChunkParser<String> {
                override fun parse(
                    chunks: com.kelvsyc.collections.ListMultimap<ChunkId, IffChunk>,
                    properties: com.kelvsyc.collections.ListMultimap<ChunkId, LocalChunk>,
                ): String {
                    called = true
                    return "custom"
                }
            }
            val core = IffParserCore.newCore {
                addFormParser(id("BODY"), customParser)
            }

            core.formParsers[id("BODY")]!!.parse(emptyListMultimap())

            called shouldBe true
        }
    }

    context("addListParser assembler convenience") {
        test("creates a ListParser wired to the core's form parsers") {
            val core = IffParserCore.newCore {
                addFormParser(id("BODY")) { _ -> "parsed-form" }
                addListParser(id("COMP")) { items -> items.joinToString(",") }
            }

            val bodyForm = form("BODY")
            val result = core.listParsers[id("COMP")]!!.parse(listOf(bodyForm))

            result shouldBe "parsed-form"
        }
    }

    context("addCatParser assembler convenience") {
        test("creates a CatParser wired to the core's form parsers") {
            val core = IffParserCore.newCore {
                addFormParser(id("BODY")) { _ -> "parsed-form" }
                addCatParser(id("HINT")) { items -> items.joinToString(",") }
            }

            val bodyForm = form("BODY")
            val result = core.catParsers[id("HINT")]!!.parse(listOf(bodyForm))

            result shouldBe "parsed-form"
        }
    }
})
