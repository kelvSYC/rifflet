package com.kelvsyc.rifflet.internal.iff

import com.kelvsyc.collections.toListMultimap
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RawChunk
import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.iff.readChunkId
import com.kelvsyc.rifflet.iff.BlankChunk
import com.kelvsyc.rifflet.iff.CatChunk
import com.kelvsyc.rifflet.iff.GroupChunk
import com.kelvsyc.rifflet.iff.IffChunk
import com.kelvsyc.rifflet.iff.IffChunkIds
import com.kelvsyc.rifflet.iff.ListChunk
import com.kelvsyc.rifflet.iff.LocalChunk
import com.kelvsyc.rifflet.iff.FormChunk
import com.kelvsyc.rifflet.internal.core.BufferedRawChunk

object RawIffChunkParser {
    fun parse(raw: BufferedRawChunk): IffChunk {
        return when (raw.type) {
            IffChunkIds.FORM -> {
                if (raw.declaredSize < 4u) throw RiffletParseException("FORM chunk declared size ${raw.declaredSize} is too small to contain a content type ID")
                val type = raw.data.readChunkId()
                val chunks = buildList<IffChunk> {
                    while (!raw.data.exhausted()) {
                        add(parse(IffBufferedChunkParser.parse(raw.data)))
                    }
                }.map { it.chunkId to it }.toListMultimap()
                FormChunk(type, chunks)
            }
            IffChunkIds.LIST -> {
                if (raw.declaredSize < 4u) throw RiffletParseException("LIST chunk declared size ${raw.declaredSize} is too small to contain a content type ID")
                var propsFinished = false
                val type = raw.data.readChunkId()
                val properties = mutableMapOf<ChunkId, List<LocalChunk>>()
                val items = mutableListOf<GroupChunk>()
                while (!raw.data.exhausted()) {
                    val chunk = IffBufferedChunkParser.parse(raw.data)
                    if (chunk.type == IffChunkIds.PROP) {
                        if (propsFinished) throw RiffletParseException("PROP chunk found after group chunk in LIST chunk")
                        if (chunk.declaredSize < 4u) throw RiffletParseException("PROP chunk declared size ${chunk.declaredSize} is too small to contain a content type ID")
                        val formType = chunk.data.readChunkId()
                        val propertyChunks = buildList {
                            while (!chunk.data.exhausted()) {
                                val inner = IffBufferedChunkParser.parse(chunk.data)
                                if (IffChunkIds.reservedIds.contains(inner.type)) {
                                    throw RiffletParseException("group chunk found in PROP chunk")
                                } else {
                                    add(LocalChunk(RawChunk(inner.type, inner.data.readByteString())))
                                }
                            }
                        }
                        properties[formType] = propertyChunks
                    } else {
                        propsFinished = true
                        val parsed = parse(chunk)
                        if (parsed is GroupChunk) {
                            items.add(parsed)
                        } else {
                            throw RiffletParseException("non-property local chunk found in LIST chunk")
                        }
                    }
                }
                ListChunk(type, properties, items)
            }
            IffChunkIds.CAT -> {
                if (raw.declaredSize < 4u) throw RiffletParseException("CAT chunk declared size ${raw.declaredSize} is too small to contain a hint ID")
                var propsFinished = false
                val type = raw.data.readChunkId()
                val properties = mutableMapOf<ChunkId, List<LocalChunk>>()
                val chunks = buildList {
                    while (!raw.data.exhausted()) {
                        val chunk = IffBufferedChunkParser.parse(raw.data)
                        if (chunk.type == IffChunkIds.PROP) {
                            if (propsFinished) throw RiffletParseException("PROP chunk found after group chunk in CAT chunk")
                            if (chunk.declaredSize < 4u) throw RiffletParseException("PROP chunk declared size ${chunk.declaredSize} is too small to contain a content type ID")
                            val formType = chunk.data.readChunkId()
                            val propertyChunks = buildList {
                                while (!chunk.data.exhausted()) {
                                    val inner = IffBufferedChunkParser.parse(chunk.data)
                                    if (IffChunkIds.reservedIds.contains(inner.type)) {
                                        throw RiffletParseException("group chunk found in PROP chunk")
                                    } else {
                                        add(LocalChunk(RawChunk(inner.type, inner.data.readByteString())))
                                    }
                                }
                            }
                            properties[formType] = propertyChunks
                        } else {
                            propsFinished = true
                            val parsed = parse(chunk)
                            if (parsed is GroupChunk) {
                                add(parsed)
                            } else {
                                throw RiffletParseException("Non-Group child chunk found in CAT chunk")
                            }
                        }
                    }
                }
                CatChunk(type, properties, chunks)
            }
            IffChunkIds.blank -> {
                BlankChunk(raw.declaredSize)
            }
            else -> {
                LocalChunk(RawChunk(raw.type, raw.data.readByteString()))
            }
        }
    }
}
