package com.kelvsyc.rifflet.internal.iff

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkEncoder
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.iff.CatChunkEncoder
import com.kelvsyc.rifflet.iff.CatEncoder
import com.kelvsyc.rifflet.iff.FormChunkEncoder
import com.kelvsyc.rifflet.iff.FormEncoder
import com.kelvsyc.rifflet.iff.IffEncoderCore
import com.kelvsyc.rifflet.iff.ListChunkEncoder
import com.kelvsyc.rifflet.iff.ListEncoder
import okio.Buffer

internal class IffEncoderCoreImpl(
    override val formEncoders: MutableMap<ChunkId, FormChunkEncoder<*>>,
    override val listEncoders: MutableMap<ChunkId, ListChunkEncoder<*>>,
    override val catEncoders: MutableMap<ChunkId, CatChunkEncoder<*>>,
    override val localEncoders: MutableMap<ChunkId, ChunkEncoder<*>>,
) : IffEncoderCore {

    class Builder : IffEncoderCore.Builder {
        private val core = IffEncoderCoreImpl(mutableMapOf(), mutableMapOf(), mutableMapOf(), mutableMapOf())

        override fun addLocalEncoder(type: ChunkId, encoder: ChunkEncoder<*>) {
            core.localEncoders[type] = encoder
        }

        override fun <T> addLocalEncoder(type: ChunkId, encoder: (T, Buffer) -> Unit) {
            core.localEncoders[type] = object : ChunkEncoder<T> {
                override val chunkId = type
                override fun encode(value: T, destination: Buffer) = encoder(value, destination)
            }
        }

        override fun addFormEncoder(type: ChunkId, encoder: FormChunkEncoder<*>) {
            core.formEncoders[type] = encoder
        }

        override fun <T> addFormEncoder(type: ChunkId, disassembler: (T) -> ListMultimap<ChunkId, Any>) {
            core.formEncoders[type] = FormEncoder(disassembler)
        }

        override fun addListEncoder(type: ChunkId, encoder: ListChunkEncoder<*>) {
            core.listEncoders[type] = encoder
        }

        override fun <T> addListEncoder(type: ChunkId, disassembler: (T) -> List<Pair<ChunkId, Any>>) {
            core.listEncoders[type] = ListEncoder(disassembler)
        }

        override fun addCatEncoder(type: ChunkId, encoder: CatChunkEncoder<*>) {
            core.catEncoders[type] = encoder
        }

        override fun <T> addCatEncoder(type: ChunkId, disassembler: (T) -> List<Pair<ChunkId, Any>>) {
            core.catEncoders[type] = CatEncoder(disassembler)
        }

        fun build(): IffEncoderCore = core
    }
}
