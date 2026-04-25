package com.kelvsyc.rifflet.iff

import com.kelvsyc.collections.toListMultimap
import com.kelvsyc.rifflet.core.ChunkId

/**
 * `ListParser` is a simple implementation of [ListChunkParser] that parses its nested chunks using its supplied [core]
 * in a context-free manner, before assembling the parsed chunks using an [assembler].
 *
 * @param core A parser core used to parse nested chunks. Nested chunks that cannot be parsed by the core
 *             will be left unparsed as their raw [IffChunk] representation.
 * @param assembler A function that assembles the list of parsed chunk values into the final domain object.
 */
class ListParser<T>(private val core: IffParserCore, private val assembler: (List<Any>) -> T) : ListChunkParser<T> {
    @Suppress("UNCHECKED_CAST")
    override fun parse(chunks: List<GroupChunk>, properties: Map<ChunkId, List<LocalChunk>>): T {
        val parsed = buildList {
            chunks.forEach {
                when (it) {
                    is FormChunk -> {
                        val parser = core.formParsers[it.type]
                        val innerProperties = properties[it.type].orEmpty().map { prop -> prop.chunkId to prop }.toListMultimap()
                        add(parser?.parse(it.chunks, innerProperties) ?: it)
                    }
                    is ListChunk -> {
                        val parser = core.listParsers[it.type]
                        // Inner LIST PROP properties override outer ones for matching form types.
                        val innerProperties = buildMap {
                            putAll(properties)
                            putAll(it.properties)
                        }
                        add(parser?.parse(it.items, innerProperties) ?: it)
                    }
                    is CatChunk -> {
                        val parser = core.catParsers[it.hint]
                        // Outer properties are forwarded so nested FORMs inside the CAT can inherit them.
                        add(parser?.parse(it.chunks, properties) ?: it)
                    }
                }
            }
        }
        return assembler(parsed)
    }
}
