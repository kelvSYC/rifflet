package com.kelvsyc.rifflet.internal.rifx

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkEncoder
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.rifx.RifxEncoderCore
import com.kelvsyc.rifflet.rifx.RifxFormBodyEncoder
import com.kelvsyc.rifflet.rifx.RifxListBodyEncoder
import okio.Buffer

internal class RifxEncoderCoreImpl(
    override val formEncoders: MutableMap<ChunkId, RifxFormBodyEncoder<*>>,
    override val listEncoders: MutableMap<ChunkId, RifxListBodyEncoder<*>>,
    override val localEncoders: MutableMap<ChunkId, ChunkEncoder<*>>,
) : RifxEncoderCore {

    class Builder : RifxEncoderCore.Builder {
        private val core = RifxEncoderCoreImpl(mutableMapOf(), mutableMapOf(), mutableMapOf())

        override fun addLocalEncoder(type: ChunkId, encoder: ChunkEncoder<*>) {
            core.localEncoders[type] = encoder
        }

        override fun <T> addLocalEncoder(type: ChunkId, encoder: (T, Buffer) -> Unit) {
            core.localEncoders[type] = object : ChunkEncoder<T> {
                override val chunkId = type
                override fun encode(value: T, destination: Buffer) = encoder(value, destination)
            }
        }

        override fun addFormEncoder(type: ChunkId, encoder: RifxFormBodyEncoder<*>) {
            core.formEncoders[type] = encoder
        }

        override fun <T> addFormEncoder(type: ChunkId, disassembler: (T) -> ListMultimap<ChunkId, Any>) {
            core.formEncoders[type] = RifxFormBodyEncoder(core, disassembler)
        }

        override fun addListEncoder(type: ChunkId, encoder: RifxListBodyEncoder<*>) {
            core.listEncoders[type] = encoder
        }

        override fun <T> addListEncoder(type: ChunkId, disassembler: (T) -> ListMultimap<ChunkId, Any>) {
            core.listEncoders[type] = RifxListBodyEncoder(core, disassembler)
        }

        fun build(): RifxEncoderCore = core
    }
}
