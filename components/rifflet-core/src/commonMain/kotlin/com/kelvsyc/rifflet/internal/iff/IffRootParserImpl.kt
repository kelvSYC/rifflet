package com.kelvsyc.rifflet.internal.iff

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.iff.GroupChunk
import com.kelvsyc.rifflet.iff.IffRawChunkParser
import com.kelvsyc.rifflet.iff.FormChunkParser
import com.kelvsyc.rifflet.iff.IffRootParser
import okio.Source
import okio.buffer

class IffRootParserImpl(private val parsers: Map<ChunkId, FormChunkParser<*>>) : IffRootParser {
    class Builder : IffRootParser.Builder {
        private val parsersInternal = mutableMapOf<ChunkId, FormChunkParser<*>>()
        val parsers: Map<ChunkId, FormChunkParser<*>> by this::parsersInternal

        override fun addFormParser(type: ChunkId, parser: FormChunkParser<*>) {
            parsersInternal[type] = parser
        }
    }

    override fun parse(source: Source): GroupChunk {
        return source.buffer().use {
            val raw = IffRawChunkParser.parse(it)
            val rootChunk = RawIffChunkParser.parse(raw)
            if (rootChunk !is GroupChunk) {
                throw IllegalStateException("Illegal root type ID '${raw.type.name}'")
            } else {
                return rootChunk
            }
        }
    }
}
