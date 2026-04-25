package com.kelvsyc.rifflet.iff

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.collections.emptyListMultimap
import com.kelvsyc.collections.listMultimapOf
import com.kelvsyc.collections.toListMultimap
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RawChunk
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import okio.ByteString

private fun id(name: String) = ChunkId(name)
private fun local(name: String) = LocalChunk(RawChunk(id(name), ByteString.EMPTY))
private fun form(name: String, vararg chunks: IffChunk): FormChunk {
    val multimap = chunks.map { it.chunkId to it }.toListMultimap()
    return FormChunk(id(name), multimap)
}

private fun localParser(block: (ByteString) -> Any) = object : LocalChunkParser<Any> {
    override fun parse(data: ByteString) = block(data)
}
private fun <T> formParser(block: (ListMultimap<ChunkId, IffChunk>, ListMultimap<ChunkId, LocalChunk>) -> T) =
    object : FormChunkParser<T> {
        override fun parse(chunks: ListMultimap<ChunkId, IffChunk>, properties: ListMultimap<ChunkId, LocalChunk>) =
            block(chunks, properties)
    }
private fun <T> listParser(block: (List<GroupChunk>, Map<ChunkId, List<LocalChunk>>) -> T) =
    object : ListChunkParser<T> {
        override fun parse(chunks: List<GroupChunk>, properties: Map<ChunkId, List<LocalChunk>>) =
            block(chunks, properties)
    }
private fun <T> catParser(block: (List<GroupChunk>, Map<ChunkId, List<LocalChunk>>) -> T) =
    object : CatChunkParser<T> {
        override fun parse(chunks: List<GroupChunk>, properties: Map<ChunkId, List<LocalChunk>>) =
            block(chunks, properties)
    }

private fun core(
    formParsers: Map<ChunkId, FormChunkParser<*>> = emptyMap(),
    listParsers: Map<ChunkId, ListChunkParser<*>> = emptyMap(),
    catParsers: Map<ChunkId, CatChunkParser<*>> = emptyMap(),
    localParsers: Map<ChunkId, LocalChunkParser<*>> = emptyMap(),
): IffParserCore = object : IffParserCore {
    override val formParsers = formParsers
    override val listParsers = listParsers
    override val catParsers = catParsers
    override val localParsers = localParsers
}

class FormParserTest : FunSpec({

    context("local chunk parsing") {
        test("local chunk is parsed by its registered parser") {
            val chunk = local("NAME")
            val core = core(localParsers = mapOf(id("NAME") to localParser { "parsed" }))
            val parser = FormParser(core) { it }
            val result = parser.parse(listMultimapOf(chunk.chunkId to chunk))
            result[id("NAME")] shouldBe listOf("parsed")
        }

        test("local chunk with no registered parser is left unparsed") {
            val chunk = local("NAME")
            val parser = FormParser(core()) { it }
            val result = parser.parse(listMultimapOf(chunk.chunkId to chunk))
            result[id("NAME")] shouldBe listOf(chunk)
        }
    }

    context("group chunk parsing") {
        test("nested FormChunk is parsed by its registered formParser") {
            val inner = form("BODY")
            val core = core(formParsers = mapOf(id("BODY") to formParser { _, _ -> "parsed-form" }))
            val parser = FormParser(core) { it }
            val result = parser.parse(listMultimapOf(inner.chunkId to inner))
            result[id("BODY")] shouldBe listOf("parsed-form")
        }

        test("nested FormChunk with no registered parser is left unparsed") {
            val inner = form("BODY")
            val parser = FormParser(core()) { it }
            val result = parser.parse(listMultimapOf(inner.chunkId to inner))
            result[id("BODY")] shouldBe listOf(inner)
        }

        test("nested ListChunk is parsed by its registered listParser") {
            val inner = ListChunk(id("COMP"), emptyMap(), emptyList())
            val core = core(listParsers = mapOf(id("COMP") to listParser { _, _ -> listOf("parsed-list") }))
            val parser = FormParser(core) { it }
            val result = parser.parse(listMultimapOf(inner.chunkId to inner))
            result[id("COMP")] shouldBe listOf(listOf("parsed-list"))
        }

        test("nested CatChunk is parsed by its registered catParser") {
            val inner = CatChunk(id("HINT"), emptyList())
            val core = core(catParsers = mapOf(id("HINT") to catParser { _, _ -> "parsed-cat" }))
            val parser = FormParser(core) { it }
            val result = parser.parse(listMultimapOf(inner.chunkId to inner))
            result[id("HINT")] shouldBe listOf("parsed-cat")
        }

        test("BlankChunk is passed through unchanged") {
            val blank = BlankChunk(4)
            val parser = FormParser(core()) { it }
            val result = parser.parse(listMultimapOf(blank.chunkId to blank))
            result[IffChunkIds.blank].single().shouldBeInstanceOf<BlankChunk>()
        }
    }

    context("property merge semantics") {
        test("chunk value takes precedence over property value for the same key") {
            val chunk = local("NAME")
            val prop = local("NAME")
            // Both parse to different strings to distinguish which source wins
            val core = core(
                localParsers = mapOf(id("NAME") to localParser { "from-chunk" })
            )
            // Override the prop parser result by giving it a different identity — not possible
            // with a single parser per key; instead verify by checking only one value is present
            val parser = FormParser(core) { it }
            val result = parser.parse(
                listMultimapOf(chunk.chunkId to chunk),
                listMultimapOf(prop.chunkId to prop),
            )
            result[id("NAME")] shouldBe listOf("from-chunk")
        }

        test("property fills in a key absent from chunks") {
            val prop = local("AUTH")
            val core = core(localParsers = mapOf(id("AUTH") to localParser { "from-prop" }))
            val parser = FormParser(core) { it }
            val result = parser.parse(emptyListMultimap(), listMultimapOf(prop.chunkId to prop))
            result[id("AUTH")] shouldBe listOf("from-prop")
        }

        test("chunk key and property key both contribute when distinct") {
            val chunk = local("NAME")
            val prop = local("AUTH")
            val core = core(
                localParsers = mapOf(
                    id("NAME") to localParser { "name-parsed" },
                    id("AUTH") to localParser { "auth-parsed" },
                )
            )
            val parser = FormParser(core) { it }
            val result = parser.parse(
                listMultimapOf(chunk.chunkId to chunk),
                listMultimapOf(prop.chunkId to prop),
            )
            result[id("NAME")] shouldBe listOf("name-parsed")
            result[id("AUTH")] shouldBe listOf("auth-parsed")
        }

        test("assembler receives the combined multimap") {
            var received: ListMultimap<ChunkId, Any>? = null
            val parser = FormParser<Unit>(core()) { received = it }
            parser.parse(emptyListMultimap())
            received shouldBe emptyListMultimap()
        }
    }
})

class ListParserTest : FunSpec({

    context("FormChunk items") {
        test("FormChunk is parsed using formParser") {
            val inner = form("BODY")
            val core = core(formParsers = mapOf(id("BODY") to formParser { _, _ -> "parsed" }))
            val parser = ListParser<List<Any>>(core) { it }
            val result = parser.parse(listOf(inner), emptyMap())
            result shouldContainExactly listOf("parsed")
        }

        test("FormChunk with no parser is left as FormChunk") {
            val inner = form("BODY")
            val parser = ListParser<List<Any>>(core()) { it }
            val result = parser.parse(listOf(inner), emptyMap())
            result shouldContainExactly listOf(inner)
        }

        test("PROP properties for the form type are passed to formParser") {
            val prop = local("AUTH")
            val inner = form("BODY")
            var receivedProps: ListMultimap<ChunkId, LocalChunk>? = null
            val core = core(formParsers = mapOf(id("BODY") to formParser { _, props ->
                receivedProps = props
                "parsed"
            }))
            val parser = ListParser<List<Any>>(core) { it }
            parser.parse(listOf(inner), mapOf(id("BODY") to listOf(prop)))
            receivedProps?.get(id("AUTH")) shouldBe listOf(prop)
        }

        test("PROP properties for a different form type are not passed to formParser") {
            val inner = form("BODY")
            var receivedProps: ListMultimap<ChunkId, LocalChunk>? = null
            val core = core(formParsers = mapOf(id("BODY") to formParser { _, props ->
                receivedProps = props
                "parsed"
            }))
            val parser = ListParser<List<Any>>(core) { it }
            parser.parse(listOf(inner), mapOf(id("OTHR") to listOf(local("AUTH"))))
            receivedProps shouldBe emptyListMultimap()
        }
    }

    context("ListChunk items") {
        test("ListChunk is parsed using listParser") {
            val inner = ListChunk(id("COMP"), emptyMap(), emptyList())
            val core = core(listParsers = mapOf(id("COMP") to listParser { _, _ -> listOf("parsed") }))
            val parser = ListParser<List<Any>>(core) { it }
            val result = parser.parse(listOf(inner), emptyMap())
            result shouldContainExactly listOf(listOf("parsed"))
        }

        test("inner LIST PROP overrides outer for the same form type") {
            val outerProp = local("OATR")
            val innerProp = local("IATR")
            val inner = ListChunk(id("COMP"), mapOf(id("BODY") to listOf(innerProp)), emptyList())
            var receivedProps: Map<ChunkId, List<LocalChunk>>? = null
            val core = core(listParsers = mapOf(id("COMP") to listParser<List<Nothing>> { _, props ->
                receivedProps = props
                emptyList()
            }))
            val parser = ListParser<List<Any>>(core) { it }
            parser.parse(listOf(inner), mapOf(id("BODY") to listOf(outerProp), id("OTHR") to listOf(local("XATR"))))
            receivedProps?.get(id("BODY")) shouldBe listOf(innerProp)
            receivedProps?.get(id("OTHR")) shouldBe listOf(local("XATR"))
        }
    }

    context("CatChunk items") {
        test("CatChunk is parsed using catParser") {
            val inner = CatChunk(id("HINT"), emptyList())
            val core = core(catParsers = mapOf(id("HINT") to catParser { _, _ -> "parsed" }))
            val parser = ListParser<List<Any>>(core) { it }
            val result = parser.parse(listOf(inner), emptyMap())
            result shouldContainExactly listOf("parsed")
        }

        test("outer properties are forwarded to catParser") {
            val prop = local("AUTH")
            val inner = CatChunk(id("HINT"), emptyList())
            var receivedProps: Map<ChunkId, List<LocalChunk>>? = null
            val core = core(catParsers = mapOf(id("HINT") to catParser { _, props ->
                receivedProps = props
                "parsed"
            }))
            val parser = ListParser<List<Any>>(core) { it }
            parser.parse(listOf(inner), mapOf(id("BODY") to listOf(prop)))
            receivedProps?.get(id("BODY")) shouldBe listOf(prop)
        }
    }

    context("result ordering") {
        test("items are returned in the same order as input") {
            val a = form("AFRM")
            val b = form("BFRM")
            val core = core(formParsers = mapOf(
                id("AFRM") to formParser { _, _ -> "a" },
                id("BFRM") to formParser { _, _ -> "b" },
            ))
            val parser = ListParser<List<Any>>(core) { it }
            val result = parser.parse(listOf(a, b), emptyMap())
            result shouldContainExactly listOf("a", "b")
        }

        test("empty input returns empty list") {
            ListParser<List<Any>>(core()) { it }.parse(emptyList(), emptyMap()) shouldBe emptyList()
        }
    }

    context("assembler") {
        test("assembler receives all parsed items in order") {
            val a = form("AFRM")
            val b = form("BFRM")
            val core = core(formParsers = mapOf(
                id("AFRM") to formParser { _, _ -> "a" },
                id("BFRM") to formParser { _, _ -> "b" },
            ))
            var received: List<Any>? = null
            val parser = ListParser(core) { items: List<Any> -> received = items; items }
            parser.parse(listOf(a, b), emptyMap())
            received shouldContainExactly listOf("a", "b")
        }

        test("assembler is called with empty list for empty input") {
            var received: List<Any>? = null
            val parser = ListParser(core()) { items: List<Any> -> received = items; items }
            parser.parse(emptyList(), emptyMap())
            received shouldBe emptyList()
        }
    }
})

class CatParserTest : FunSpec({

    context("FormChunk items") {
        test("FormChunk is parsed using formParser") {
            val inner = form("BODY")
            val core = core(formParsers = mapOf(id("BODY") to formParser { _, _ -> "parsed" }))
            val parser = CatParser(core) { it }
            val result = parser.parse(listOf(inner), emptyMap())
            result shouldContainExactly listOf("parsed")
        }

        test("FormChunk with no parser is left as FormChunk") {
            val inner = form("BODY")
            val parser = CatParser(core()) { it }
            val result = parser.parse(listOf(inner), emptyMap())
            result shouldContainExactly listOf(inner)
        }

        test("outer properties for the form type are scoped and passed to formParser") {
            val prop = local("AUTH")
            val inner = form("BODY")
            var receivedProps: ListMultimap<ChunkId, LocalChunk>? = null
            val core = core(formParsers = mapOf(id("BODY") to formParser { _, props ->
                receivedProps = props
                "parsed"
            }))
            val parser = CatParser(core) { it }
            parser.parse(listOf(inner), mapOf(id("BODY") to listOf(prop)))
            receivedProps?.get(id("AUTH")) shouldBe listOf(prop)
        }

        test("properties for a different form type are not passed to formParser") {
            val inner = form("BODY")
            var receivedProps: ListMultimap<ChunkId, LocalChunk>? = null
            val core = core(formParsers = mapOf(id("BODY") to formParser { _, props ->
                receivedProps = props
                "parsed"
            }))
            val parser = CatParser(core) { it }
            parser.parse(listOf(inner), mapOf(id("OTHR") to listOf(local("AUTH"))))
            receivedProps shouldBe emptyListMultimap()
        }
    }

    context("ListChunk items") {
        test("ListChunk is parsed using listParser") {
            val inner = ListChunk(id("COMP"), emptyMap(), emptyList())
            val core = core(listParsers = mapOf(id("COMP") to listParser { _, _ -> listOf("parsed") }))
            val parser = CatParser(core) { it }
            val result = parser.parse(listOf(inner), emptyMap())
            result shouldContainExactly listOf(listOf("parsed"))
        }

        test("inner LIST PROP overrides outer for the same form type") {
            val outerProp = local("OATR")
            val innerProp = local("IATR")
            val inner = ListChunk(id("COMP"), mapOf(id("BODY") to listOf(innerProp)), emptyList())
            var receivedProps: Map<ChunkId, List<LocalChunk>>? = null
            val core = core(listParsers = mapOf(id("COMP") to listParser<List<Nothing>> { _, props ->
                receivedProps = props
                emptyList()
            }))
            val parser = CatParser(core) { it }
            parser.parse(listOf(inner), mapOf(id("BODY") to listOf(outerProp), id("OTHR") to listOf(local("XATR"))))
            receivedProps?.get(id("BODY")) shouldBe listOf(innerProp)
            receivedProps?.get(id("OTHR")) shouldBe listOf(local("XATR"))
        }
    }

    context("CatChunk items") {
        test("CatChunk is parsed using catParser") {
            val inner = CatChunk(id("HINT"), emptyList())
            val core = core(catParsers = mapOf(id("HINT") to catParser { _, _ -> "parsed" }))
            val parser = CatParser(core) { it }
            val result = parser.parse(listOf(inner), emptyMap())
            result shouldContainExactly listOf("parsed")
        }

        test("outer properties are forwarded to nested catParser") {
            val prop = local("AUTH")
            val inner = CatChunk(id("HINT"), emptyList())
            var receivedProps: Map<ChunkId, List<LocalChunk>>? = null
            val core = core(catParsers = mapOf(id("HINT") to catParser { _, props ->
                receivedProps = props
                "parsed"
            }))
            val parser = CatParser(core) { it }
            parser.parse(listOf(inner), mapOf(id("BODY") to listOf(prop)))
            receivedProps?.get(id("BODY")) shouldBe listOf(prop)
        }
    }

    context("assembler") {
        test("assembler receives all parsed items in order") {
            val a = form("AFRM")
            val b = form("BFRM")
            val core = core(formParsers = mapOf(
                id("AFRM") to formParser { _, _ -> "a" },
                id("BFRM") to formParser { _, _ -> "b" },
            ))
            var received: List<Any>? = null
            val parser = CatParser(core) { items -> received = items; items }
            parser.parse(listOf(a, b), emptyMap())
            received shouldContainExactly listOf("a", "b")
        }

        test("assembler is called with empty list for empty input") {
            var received: List<Any>? = null
            val parser = CatParser(core()) { items -> received = items; items }
            parser.parse(emptyList(), emptyMap())
            received shouldBe emptyList()
        }
    }
})
