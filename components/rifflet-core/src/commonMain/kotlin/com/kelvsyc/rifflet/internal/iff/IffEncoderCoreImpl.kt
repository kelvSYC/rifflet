package com.kelvsyc.rifflet.internal.iff

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkEncoder
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.iff.CatBodyEncoder
import com.kelvsyc.rifflet.iff.FormBodyEncoder
import com.kelvsyc.rifflet.iff.IffEncoderCore
import com.kelvsyc.rifflet.iff.ListBodyEncoder
import okio.Buffer

internal class IffEncoderCoreImpl(
    override val formEncoders: MutableMap<ChunkId, FormBodyEncoder<*>>,
    override val listEncoders: MutableMap<ChunkId, ListBodyEncoder<*>>,
    override val catEncoders: MutableMap<ChunkId, CatBodyEncoder<*>>,
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

        override fun addFormEncoder(type: ChunkId, encoder: FormBodyEncoder<*>) {
            core.formEncoders[type] = encoder
        }

        override fun <T> addFormEncoder(type: ChunkId, disassembler: (T) -> ListMultimap<ChunkId, Any>) {
            core.formEncoders[type] = FormBodyEncoder(core, disassembler)
        }

        override fun addListEncoder(type: ChunkId, encoder: ListBodyEncoder<*>) {
            core.listEncoders[type] = encoder
        }

        override fun <T> addListEncoder(type: ChunkId, disassembler: (T) -> List<Pair<ChunkId, Any>>) {
            core.listEncoders[type] = ListBodyEncoder(core, disassembler)
        }

        override fun addCatEncoder(type: ChunkId, encoder: CatBodyEncoder<*>) {
            core.catEncoders[type] = encoder
        }

        override fun <T> addCatEncoder(type: ChunkId, disassembler: (T) -> List<Pair<ChunkId, Any>>) {
            core.catEncoders[type] = CatBodyEncoder(core, disassembler)
        }

        fun build(): IffEncoderCore = core
    }
}
