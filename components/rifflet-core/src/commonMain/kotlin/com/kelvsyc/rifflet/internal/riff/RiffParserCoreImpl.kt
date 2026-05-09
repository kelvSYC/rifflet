package com.kelvsyc.rifflet.internal.riff

import com.kelvsyc.kotlin.core.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.LocalChunkParser
import com.kelvsyc.rifflet.riff.RiffFormChunkParser
import com.kelvsyc.rifflet.riff.RiffFormParser
import com.kelvsyc.rifflet.riff.RiffListChunkParser
import com.kelvsyc.rifflet.riff.RiffListParser
import com.kelvsyc.rifflet.riff.RiffParserCore
import okio.ByteString

internal class RiffParserCoreImpl(
    override val formParsers: MutableMap<ChunkId, RiffFormChunkParser<*>>,
    override val listParsers: MutableMap<ChunkId, RiffListChunkParser<*>>,
    override val localParsers: MutableMap<ChunkId, LocalChunkParser<*>>,
) : RiffParserCore {

    class Builder : RiffParserCore.Builder {
        private val core = RiffParserCoreImpl(mutableMapOf(), mutableMapOf(), mutableMapOf())

        override fun addLocalParser(type: ChunkId, parser: LocalChunkParser<*>) {
            core.localParsers[type] = parser
        }

        override fun <T> addLocalParser(type: ChunkId, parser: (ByteString) -> T) {
            core.localParsers[type] = object : LocalChunkParser<T> {
                override fun parse(data: ByteString): T = parser(data)
            }
        }

        override fun addFormParser(type: ChunkId, parser: RiffFormChunkParser<*>) {
            core.formParsers[type] = parser
        }

        override fun <T> addFormParser(type: ChunkId, assembler: (ListMultimap<ChunkId, Any>) -> T) {
            core.formParsers[type] = RiffFormParser(core, assembler)
        }

        override fun addListParser(type: ChunkId, parser: RiffListChunkParser<*>) {
            core.listParsers[type] = parser
        }

        override fun <T> addListParser(type: ChunkId, assembler: (ListMultimap<ChunkId, Any>) -> T) {
            core.listParsers[type] = RiffListParser(core, assembler)
        }

        fun build(): RiffParserCore = core
    }
}
