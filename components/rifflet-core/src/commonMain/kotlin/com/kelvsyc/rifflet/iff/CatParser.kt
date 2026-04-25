package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId

/**
 * `CatParser` is a simple implementation of [CatChunkParser] that parses its nested using its supplied [core] in a
 * context-free manner, before assembling the parsed chunks using an [assembler].
 *
 * @param core A parser core that can be used to parse nested chunks. Nested chunks that cannot be parsed by the core
 *             will be left unparsed.
 * @param assembler A function that assembles parsed chunk data to the final object.
 */
class CatParser<T>(private val core: IffParserCore, private val assembler: (List<Any>) -> T) : CatChunkParser<T> {
    // TODO is it possible to integrate a "flattening CAT" feature?
    override fun parse(chunks: List<GroupChunk>, properties: Map<ChunkId, List<LocalChunk>>): T {
        val chunks = buildList {
            chunks.forEach {
                when (it) {
                    is FormChunk -> {
                        val parser = core.formParsers[it.type]
                        val innerProperties = properties[it.type].orEmpty()

                        val parsed = parser?.parse(it.chunks, innerProperties) ?: it
                        add(parsed)
                    }
                    is ListChunk -> {
                        val parser = core.listParsers[it.type]
                        val innerProperties = buildMap {
                            putAll(properties)
                            putAll(it.properties)
                        }

                        val parsed = parser?.parse(it.items, innerProperties) ?: it
                        add(parsed)
                    }
                    is CatChunk -> {
                        val parser = core.catParsers[it.hint]

                        val parsed = parser?.parse(it.chunks, properties) ?: it
                        add(parsed)
                    }
                }
            }
        }
        return assembler(chunks)
    }
}
