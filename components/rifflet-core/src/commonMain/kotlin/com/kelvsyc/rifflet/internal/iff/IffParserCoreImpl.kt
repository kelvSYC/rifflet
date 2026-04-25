package com.kelvsyc.rifflet.internal.iff

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.iff.CatChunkParser
import com.kelvsyc.rifflet.iff.CatParser
import com.kelvsyc.rifflet.iff.FormChunkParser
import com.kelvsyc.rifflet.iff.FormParser
import com.kelvsyc.rifflet.iff.IffParserCore
import com.kelvsyc.rifflet.iff.ListChunkParser
import com.kelvsyc.rifflet.iff.ListParser
import com.kelvsyc.rifflet.iff.LocalChunkParser
import okio.ByteString

internal class IffParserCoreImpl(
    override val formParsers: MutableMap<ChunkId, FormChunkParser<*>>,
    override val listParsers: MutableMap<ChunkId, ListChunkParser<*>>,
    override val catParsers: MutableMap<ChunkId, CatChunkParser<*>>,
    override val localParsers: MutableMap<ChunkId, LocalChunkParser<*>>,
) : IffParserCore {

    class Builder : IffParserCore.Builder {
        private val core = IffParserCoreImpl(mutableMapOf(), mutableMapOf(), mutableMapOf(), mutableMapOf())

        override fun addLocalParser(type: ChunkId, parser: LocalChunkParser<*>) {
            core.localParsers[type] = parser
        }

        override fun <T> addLocalParser(type: ChunkId, parser: (ByteString) -> T) {
            core.localParsers[type] = object : LocalChunkParser<T> {
                override fun parse(data: ByteString): T = parser(data)
            }
        }

        override fun addFormParser(type: ChunkId, parser: FormChunkParser<*>) {
            core.formParsers[type] = parser
        }

        override fun <T> addFormParser(type: ChunkId, assembler: (ListMultimap<ChunkId, Any>) -> T) {
            core.formParsers[type] = FormParser(core, assembler)
        }

        override fun addListParser(type: ChunkId, parser: ListChunkParser<*>) {
            core.listParsers[type] = parser
        }

        override fun <T> addListParser(type: ChunkId, assembler: (List<Any>) -> T) {
            core.listParsers[type] = ListParser(core, assembler)
        }

        override fun addCatParser(type: ChunkId, parser: CatChunkParser<*>) {
            core.catParsers[type] = parser
        }

        override fun <T> addCatParser(type: ChunkId, assembler: (List<Any>) -> T) {
            core.catParsers[type] = CatParser(core, assembler)
        }

        fun build(): IffParserCore = core
    }
}
