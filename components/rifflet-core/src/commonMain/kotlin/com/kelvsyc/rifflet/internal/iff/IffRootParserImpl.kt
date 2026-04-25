package com.kelvsyc.rifflet.internal.iff

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.iff.FormChunk
import com.kelvsyc.rifflet.iff.IffRawChunkParser
import com.kelvsyc.rifflet.iff.FormChunkParser
import com.kelvsyc.rifflet.iff.IffRootParser
import okio.Source
import okio.buffer

class IffRootParserImpl<T>(private val parsers: Map<ChunkId, FormChunkParser<out T>>) : IffRootParser<T> {
    class Builder<T> : IffRootParser.Builder<T> {
        private val parsersInternal = mutableMapOf<ChunkId, FormChunkParser<out T>>()
        val parsers: Map<ChunkId, FormChunkParser<out T>> by this::parsersInternal

        override fun addFormParser(type: ChunkId, parser: FormChunkParser<out T>) {
            parsersInternal[type] = parser
        }
    }

    override fun parse(source: Source): T {
        return source.buffer().use { buffered ->
            val raw = IffRawChunkParser.parse(buffered)
            when (val rootChunk = RawIffChunkParser.parse(raw)) {
                is FormChunk -> {
                    val parser = parsers[rootChunk.type]
                        ?: throw IllegalStateException("No registered parser for FORM type '${rootChunk.type.name}'")
                    parser.parse(rootChunk.chunks)
                }
                else -> throw IllegalStateException("Illegal root type ID '${raw.type.name}'")
            }
        }
    }
}
