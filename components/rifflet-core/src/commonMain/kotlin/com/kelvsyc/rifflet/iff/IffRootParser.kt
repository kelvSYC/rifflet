package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.internal.iff.IffRootParserImpl
import okio.Source

interface IffRootParser {
    companion object {
        fun newParser(fn: Builder.() -> Unit): IffRootParser {
            val builder = IffRootParserImpl.Builder().apply(fn)
            return IffRootParserImpl(builder.parsers)
        }
    }

    interface Builder {
        fun addFormParser(type: ChunkId, parser: FormChunkParser<*>)
    }

    fun parse(source: Source): GroupChunk
}
