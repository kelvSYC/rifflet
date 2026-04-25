package com.kelvsyc.rifflet.iff

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.collections.contains
import com.kelvsyc.collections.filterKeys
import com.kelvsyc.collections.mapValues
import com.kelvsyc.collections.plus
import com.kelvsyc.rifflet.core.ChunkId

/**
 * `FormParser` is a simple implementation of [FormChunkParser] that parses its nested chunks using its supplied [core]
 * in a context-free manner, before assembling the parsed chunks using an [assembler].
 *
 * @param core A parser core that can be used to parse nested chunks. Nested chunks that cannot be parsed by the core
 *             will be left unparsed.
 * @param assembler A function that assembles parsed chunk data to the final object.
 */
class FormParser<T>(private val core: IffParserCore, private val assembler: (ListMultimap<ChunkId, Any>) -> T) : FormChunkParser<T> {
    override fun parse(chunks: ListMultimap<ChunkId, IffChunk>, properties: ListMultimap<ChunkId, LocalChunk>): T {
        val parsedChunks: ListMultimap<ChunkId, Any> = chunks.mapValues { chunk ->
            when (chunk) {
                // chunkId is the inner content-type field, not the outer FORM/FOR1/… wrapper —
                // one registration covers all variant IDs for the same content type.
                is FormChunk -> core.formParsers[chunk.chunkId]?.parse(chunk.chunks) ?: chunk
                is ListChunk -> core.listParsers[chunk.chunkId]?.parse(chunk.items, chunk.properties) ?: chunk
                is CatChunk -> core.catParsers[chunk.chunkId]?.parse(chunk.chunks) ?: chunk
                is LocalChunk -> core.localParsers[chunk.chunkId]?.parse(chunk.data.data) ?: chunk
                // RawIffChunkParser drops blank chunks before FormChunk.chunks is populated,
                // so this branch is unreachable in practice; retained for sealed-type exhaustiveness.
                is BlankChunk -> chunk
            }
        }

        val parsedProperties: ListMultimap<ChunkId, Any> = properties.mapValues { prop ->
            core.localParsers[prop.chunkId]?.parse(prop.data.data) ?: prop
        }

        // Chunks take precedence; properties fill in for keys absent from chunks.
        val combined = parsedChunks + parsedProperties.filterKeys { it !in parsedChunks }

        return assembler(combined)
    }
}
