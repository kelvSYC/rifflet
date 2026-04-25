package com.kelvsyc.rifflet.iff

import com.kelvsyc.collections.toListMultimap
import com.kelvsyc.rifflet.core.ChunkId

/**
 * `CatParser` is a simple implementation of [CatChunkParser] that parses its nested chunks using its supplied [core]
 * in a context-free manner, before assembling the parsed chunks using an [assembler].
 *
 * @param core A parser core that can be used to parse nested chunks. Nested chunks that cannot be parsed by the core
 *             will be left unparsed.
 * @param assembler A function that assembles parsed chunk data to the final object.
 */
class CatParser<T>(private val core: IffParserCore, private val assembler: (List<Any>) -> T) : CatChunkParser<T> {
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
                        val innerProperties = buildMap {
                            putAll(properties)
                            putAll(it.properties)
                        }
                        add(parser?.parse(it.items, innerProperties) ?: it)
                    }
                    is CatChunk -> {
                        val parser = core.catParsers[it.hint]
                        add(parser?.parse(it.chunks, properties) ?: it)
                    }
                }
            }
        }
        return assembler(parsed)
    }
}
