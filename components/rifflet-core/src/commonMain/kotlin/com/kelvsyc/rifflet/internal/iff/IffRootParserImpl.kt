package com.kelvsyc.rifflet.internal.iff

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.iff.CatChunk
import com.kelvsyc.rifflet.iff.CatChunkParser
import com.kelvsyc.rifflet.iff.FormChunk
import com.kelvsyc.rifflet.iff.FormChunkParser
import com.kelvsyc.rifflet.iff.IffRawChunkParser
import com.kelvsyc.rifflet.iff.IffRootParser
import com.kelvsyc.rifflet.iff.ListChunk
import com.kelvsyc.rifflet.iff.ListChunkParser
import okio.Source
import okio.buffer

class IffRootParserImpl<T>(
    private val formParsers: Map<ChunkId, FormChunkParser<out T>>,
    private val listParsers: Map<ChunkId, ListChunkParser<out T>>,
    private val catParsers: Map<ChunkId, CatChunkParser<out T>>,
) : IffRootParser<T> {
    class Builder<T> : IffRootParser.Builder<T> {
        private val formParsersInternal = mutableMapOf<ChunkId, FormChunkParser<out T>>()
        private val listParsersInternal = mutableMapOf<ChunkId, ListChunkParser<out T>>()
        private val catParsersInternal = mutableMapOf<ChunkId, CatChunkParser<out T>>()

        val formParsers: Map<ChunkId, FormChunkParser<out T>> by this::formParsersInternal
        val listParsers: Map<ChunkId, ListChunkParser<out T>> by this::listParsersInternal
        val catParsers: Map<ChunkId, CatChunkParser<out T>> by this::catParsersInternal

        override fun addFormParser(type: ChunkId, parser: FormChunkParser<out T>) {
            formParsersInternal[type] = parser
        }

        override fun addListParser(type: ChunkId, parser: ListChunkParser<out T>) {
            listParsersInternal[type] = parser
        }

        override fun addCatParser(type: ChunkId, parser: CatChunkParser<out T>) {
            catParsersInternal[type] = parser
        }
    }

    override fun parse(source: Source): T {
        return source.buffer().use { buffered ->
            val raw = IffRawChunkParser.parse(buffered)
            when (val rootChunk = RawIffChunkParser.parse(raw)) {
                is FormChunk -> {
                    val parser = formParsers[rootChunk.type]
                        ?: throw IllegalStateException("No registered parser for FORM type '${rootChunk.type.name}'")
                    parser.parse(rootChunk.chunks)
                }
                is ListChunk -> {
                    val parser = listParsers[rootChunk.type]
                        ?: throw IllegalStateException("No registered parser for LIST type '${rootChunk.type.name}'")
                    parser.parse(rootChunk.items, rootChunk.properties)
                }
                is CatChunk -> {
                    val parser = catParsers[rootChunk.hint]
                        ?: throw IllegalStateException("No registered parser for CAT hint '${rootChunk.hint.name}'")
                    parser.parse(rootChunk.chunks)
                }
                else -> throw IllegalStateException("Illegal root type ID '${raw.type.name}'")
            }
        }
    }
}
