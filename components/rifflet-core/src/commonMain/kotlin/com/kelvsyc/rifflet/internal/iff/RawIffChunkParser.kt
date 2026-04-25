package com.kelvsyc.rifflet.internal.iff

import com.kelvsyc.collections.toListMultimap
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RawChunk
import com.kelvsyc.rifflet.core.readChunkId
import com.kelvsyc.rifflet.iff.BlankChunk
import com.kelvsyc.rifflet.iff.CatChunk
import com.kelvsyc.rifflet.iff.GroupChunk
import com.kelvsyc.rifflet.iff.IffChunk
import com.kelvsyc.rifflet.iff.IffChunkIds
import com.kelvsyc.rifflet.iff.IffRawChunkParser
import com.kelvsyc.rifflet.iff.ListChunk
import com.kelvsyc.rifflet.iff.LocalChunk
import com.kelvsyc.rifflet.iff.FormChunk
import okio.Buffer

object RawIffChunkParser {
    fun parse(raw: RawChunk): IffChunk {
        return when (raw.type) {
            IffChunkIds.FORM -> {
                val source = Buffer().apply {
                    write(raw.data)
                }
                val type = source.readChunkId()
                val chunks = buildList<IffChunk> {
                    while (!source.exhausted()) {
                        add(parse(IffRawChunkParser.parse(source)))
                    }
                }.map { it.chunkId to it }.toListMultimap()
                FormChunk(type, chunks)
            }
            IffChunkIds.LIST -> {
                val source = Buffer().apply {
                    write(raw.data)
                }
                var propsFinished = false
                val type = source.readChunkId()
                val properties = mutableMapOf<ChunkId, List<LocalChunk>>()
                val items = mutableListOf<GroupChunk>()
                while (!source.exhausted()) {
                    val chunk = IffRawChunkParser.parse(source)
                    if (chunk.type == IffChunkIds.PROP) {
                        if (propsFinished) throw IllegalStateException("PROP chunk found after group chunk in LIST chunk")
                        val innerData = Buffer().apply { write(chunk.data) }
                        val formType = innerData.readChunkId()
                        val propertyChunks = buildList {
                            while (!innerData.exhausted()) {
                                val inner = IffRawChunkParser.parse(innerData)
                                if (IffChunkIds.reservedIds.contains(inner.type)) {
                                    throw IllegalStateException("group chunk found in PROP chunk")
                                } else {
                                    add(LocalChunk(inner))
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
                            throw IllegalStateException("non-property local chunk found in LIST chunk")
                        }
                    }
                }
                ListChunk(type, properties, items)
            }
            IffChunkIds.CAT -> {
                val source = Buffer().apply {
                    write(raw.data)
                }
                val type = source.readChunkId()
                val chunks = buildList {
                    while (!source.exhausted()) {
                        val chunk = parse(IffRawChunkParser.parse(source))
                        if (chunk is GroupChunk) {
                            add(chunk)
                        } else {
                            throw IllegalStateException("Non-Group child chunk found in CAT chunk")
                        }
                    }
                }
                CatChunk(type, chunks)
            }
            IffChunkIds.blank -> {
                BlankChunk(raw.data.size)
            }
            else -> {
                LocalChunk(raw)
            }
        }
    }
}
