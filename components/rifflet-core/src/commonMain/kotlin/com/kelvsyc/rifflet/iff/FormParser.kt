package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId

/**
 * `FormParser` is a simple implementation of [FormChunkParser] that parses its nested chunks using its supplied [core]
 * in a context-free manner, before assembling the parsed chunks using an [assembler].
 *
 * @param core A parser core that can be used to parse nested chunks. Nested chunks that cannot be parsed by the core
 *             will be left unparsed.
 * @param assembler A function that assembles parsed chunk data to the final object.
 */
class FormParser<T>(private val core: IffParserCore, private val assembler: (Map<ChunkId, Any>) -> T) : FormChunkParser<T> {
    override fun parse(chunks: List<IffChunk>, properties: List<LocalChunk>): T {
        // Combine the properties with the chunks
        // TODO Multimap?
        val combined = buildMap {
            properties.forEach {
                put(it.data.type, it)
            }
            chunks.forEach {
                when (it) {
                    is FormChunk -> put(it.type, it)
                    is ListChunk -> put(it.type, it)
                    is CatChunk -> put(it.hint, it)
                    is LocalChunk -> put(it.data.type, it)
                    else -> Unit // Ignore blank Chunks
                }
            }
        }

        // Now, we parse the inner chunks, if we have a parser for it
        val parsed = combined.mapValues { (type, chunk) ->
            when (chunk) {
                is FormChunk -> {
                    val parser = core.formParsers[type]
                    parser?.parse(chunk.chunks) ?: chunk
                }
                is ListChunk -> {
                    val parser = core.listParsers[type]
                    parser?.parse(chunk.items, chunk.properties) ?: chunk
                }
                is CatChunk -> {
                    val parser = core.catParsers[type]
                    parser?.parse(chunk.chunks) ?: chunk
                }
                is LocalChunk -> {
                    val parser = core.localParsers[type]
                    parser?.parse(chunk.data.data) ?: chunk
                }
                else -> {
                    // Unreachable
                    throw IllegalStateException("Blank chunk encountered in combined map")
                }
            }
        }
        return assembler(parsed)
    }
}
