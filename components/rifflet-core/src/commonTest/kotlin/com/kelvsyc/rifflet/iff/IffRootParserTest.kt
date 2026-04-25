package com.kelvsyc.rifflet.iff

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletParseException
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

private fun formBinary(formType: String): Buffer {
    val body = Buffer().apply { writeString(formType, Charsets.ISO_8859_1) }
    return Buffer().apply {
        writeString("FORM", Charsets.ISO_8859_1)
        writeInt(body.size.toInt())
        writeAll(body)
    }
}

private fun listBinary(listType: String): Buffer {
    val body = Buffer().apply { writeString(listType, Charsets.ISO_8859_1) }
    return Buffer().apply {
        writeString("LIST", Charsets.ISO_8859_1)
        writeInt(body.size.toInt())
        writeAll(body)
    }
}

private fun catBinary(hint: String): Buffer {
    val body = Buffer().apply { writeString(hint, Charsets.ISO_8859_1) }
    return Buffer().apply {
        writeString("CAT ", Charsets.ISO_8859_1)
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

    context("FORM root") {
        test("is dispatched to its registered parser") {
            val parser = IffRootParser.newParser<String> {
                root = IffRootParser.Root.FormRoot(id("TEST"))
                core { addFormParser(id("TEST"), formParser { _, _ -> "parsed" }) }
            }
            parser.parse(formBinary("TEST")) shouldBe "parsed"
        }

        test("passes sub-chunks to the registered parser") {
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
                root = IffRootParser.Root.FormRoot(id("TEST"))
                core { addFormParser(id("TEST"), formParser { chunks, _ -> receivedChunks = chunks }) }
            }
            parser.parse(source)
            receivedChunks?.keys shouldBe setOf(id("NAME"))
        }

        test("throws when no parser is registered for the form type") {
            val parser = IffRootParser.newParser<String> {
                root = IffRootParser.Root.FormRoot(id("TEST"))
                core {}
            }
            shouldThrow<RiffletParseException> { parser.parse(formBinary("TEST")) }
        }

        test("throws when root chunk kind does not match declared root") {
            val parser = IffRootParser.newParser<String> {
                root = IffRootParser.Root.FormRoot(id("TEST"))
                core { addFormParser(id("TEST"), formParser { _, _ -> "parsed" }) }
            }
            shouldThrow<RiffletParseException> { parser.parse(listBinary("TEST")) }
        }
    }

    context("LIST root") {
        test("is dispatched to its registered parser") {
            val parser = IffRootParser.newParser<String> {
                root = IffRootParser.Root.ListRoot(id("COMP"))
                core { addListParser(id("COMP"), listParser { _, _ -> "parsed" }) }
            }
            parser.parse(listBinary("COMP")) shouldBe "parsed"
        }

        test("passes items and inner properties to the registered parser") {
            var receivedItems: List<GroupChunk>? = null
            var receivedProperties: Map<ChunkId, List<LocalChunk>>? = null
            val parser = IffRootParser.newParser<String> {
                root = IffRootParser.Root.ListRoot(id("COMP"))
                core {
                    addListParser(id("COMP"), listParser { items, props ->
                        receivedItems = items
                        receivedProperties = props
                        "parsed"
                    })
                }
            }
            parser.parse(listBinary("COMP"))
            receivedItems shouldBe emptyList()
            receivedProperties shouldBe emptyMap()
        }

        test("throws when no parser is registered for the list type") {
            val parser = IffRootParser.newParser<String> {
                root = IffRootParser.Root.ListRoot(id("COMP"))
                core {}
            }
            shouldThrow<RiffletParseException> { parser.parse(listBinary("COMP")) }
        }

        test("throws when root chunk kind does not match declared root") {
            val parser = IffRootParser.newParser<String> {
                root = IffRootParser.Root.ListRoot(id("COMP"))
                core { addListParser(id("COMP"), listParser { _, _ -> "parsed" }) }
            }
            shouldThrow<RiffletParseException> { parser.parse(formBinary("COMP")) }
        }
    }

    context("CAT root") {
        test("is dispatched to its registered parser") {
            val parser = IffRootParser.newParser<String> {
                root = IffRootParser.Root.CatRoot(id("HINT"))
                core { addCatParser(id("HINT"), catParser { _, _ -> "parsed" }) }
            }
            parser.parse(catBinary("HINT")) shouldBe "parsed"
        }

        test("passes chunks to the registered parser") {
            var receivedChunks: List<GroupChunk>? = null
            val parser = IffRootParser.newParser<Unit> {
                root = IffRootParser.Root.CatRoot(id("HINT"))
                core { addCatParser(id("HINT"), catParser { chunks, _ -> receivedChunks = chunks }) }
            }
            parser.parse(catBinary("HINT"))
            receivedChunks shouldBe emptyList()
        }

        test("throws when no parser is registered for the cat hint") {
            val parser = IffRootParser.newParser<String> {
                root = IffRootParser.Root.CatRoot(id("HINT"))
                core {}
            }
            shouldThrow<RiffletParseException> { parser.parse(catBinary("HINT")) }
        }

        test("throws when root chunk kind does not match declared root") {
            val parser = IffRootParser.newParser<String> {
                root = IffRootParser.Root.CatRoot(id("HINT"))
                core { addCatParser(id("HINT"), catParser { _, _ -> "parsed" }) }
            }
            shouldThrow<RiffletParseException> { parser.parse(formBinary("HINT")) }
        }
    }

    context("invalid root") {
        test("throws for a non-group root chunk") {
            val parser = IffRootParser.newParser<String> {
                root = IffRootParser.Root.FormRoot(id("NAME"))
                core {}
            }
            shouldThrow<RiffletParseException> { parser.parse(localChunkBinary("NAME")) }
        }
    }
})
