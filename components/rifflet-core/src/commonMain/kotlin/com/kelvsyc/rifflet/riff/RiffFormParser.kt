package com.kelvsyc.rifflet.riff

import com.kelvsyc.kotlin.core.collections.ListMultimap
import com.kelvsyc.kotlin.core.collections.mapValues
import com.kelvsyc.rifflet.core.ChunkId

/**
 * A context-free implementation of [RiffFormChunkParser] that dispatches each sub-chunk through
 * [core] and assembles the results with [assembler].
 *
 * Sub-chunks with no registered parser are left as their raw [RiffChunk] representation.
 */
class RiffFormParser<T>(
    private val core: RiffParserCore,
    private val assembler: (ListMultimap<ChunkId, Any>) -> T,
) : RiffFormChunkParser<T> {
    @Suppress("UNCHECKED_CAST")
    override fun parse(chunks: ListMultimap<ChunkId, RiffChunk>): T {
        val parsed = chunks.mapValues { chunk ->
            when (chunk) {
                is RiffFormChunk -> core.formParsers[chunk.chunkId]?.parse(chunk.chunks) ?: chunk
                is RiffListChunk -> core.listParsers[chunk.chunkId]?.parse(chunk.chunks) ?: chunk
                is RiffLocalChunk -> core.localParsers[chunk.chunkId]?.parse(chunk.data.data) ?: chunk
            }
        }
        return assembler(parsed)
    }
}
