package com.kelvsyc.rifflet.iff

import com.kelvsyc.collections.toListMultimap
import com.kelvsyc.rifflet.core.ChunkId

class ListParser<T>(private val core: IffParserCore) : ListChunkParser<T> {
    @Suppress("UNCHECKED_CAST")
    override fun parse(chunks: List<GroupChunk>, properties: Map<ChunkId, List<LocalChunk>>): List<T> {
        return buildList {
            chunks.forEach {
                when (it) {
                    is FormChunk -> {
                        val parser = core.formParsers[it.type]
                        // Per IFF spec, PROP properties for this form type from the outer LIST are passed down.
                        val innerProperties = properties[it.type].orEmpty().map { prop -> prop.chunkId to prop }.toListMultimap()
                        add((parser?.parse(it.chunks, innerProperties) ?: it) as T)
                    }
                    is ListChunk -> {
                        val parser = core.listParsers[it.type]
                        // Inner LIST PROP properties override outer ones for matching form types.
                        val innerProperties = buildMap {
                            putAll(properties)
                            putAll(it.properties)
                        }
                        add((parser?.parse(it.items, innerProperties) ?: it) as T)
                    }
                    is CatChunk -> {
                        val parser = core.catParsers[it.hint]
                        // Outer properties are forwarded so nested FORMs inside the CAT can inherit them.
                        add((parser?.parse(it.chunks, properties) ?: it) as T)
                    }
                }
            }
        }
    }
}
