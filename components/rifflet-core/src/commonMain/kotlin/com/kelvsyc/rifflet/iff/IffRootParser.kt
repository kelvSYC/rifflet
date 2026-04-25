package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.internal.iff.IffRootParserImpl
import okio.Source

interface IffRootParser<out T> {
    companion object {
        fun <T> newParser(fn: Builder<T>.() -> Unit): IffRootParser<T> {
            val builder = IffRootParserImpl.Builder<T>().apply(fn)
            return IffRootParserImpl(builder.parsers)
        }
    }

    interface Builder<T> {
        fun addFormParser(type: ChunkId, parser: FormChunkParser<out T>)
    }

    fun parse(source: Source): T
}
