package com.kelvsyc.rifflet.internal.riff

import com.kelvsyc.kotlin.core.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkEncoder
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.riff.RiffEncoderCore
import com.kelvsyc.rifflet.riff.RiffFormBodyEncoder
import com.kelvsyc.rifflet.riff.RiffListBodyEncoder
import okio.Buffer

internal class RiffEncoderCoreImpl(
    override val formEncoders: MutableMap<ChunkId, RiffFormBodyEncoder<*>>,
    override val listEncoders: MutableMap<ChunkId, RiffListBodyEncoder<*>>,
    override val localEncoders: MutableMap<ChunkId, ChunkEncoder<*>>,
) : RiffEncoderCore {

    class Builder : RiffEncoderCore.Builder {
        private val core = RiffEncoderCoreImpl(mutableMapOf(), mutableMapOf(), mutableMapOf())

        override fun addLocalEncoder(type: ChunkId, encoder: ChunkEncoder<*>) {
            core.localEncoders[type] = encoder
        }

        override fun <T> addLocalEncoder(type: ChunkId, encoder: (T, Buffer) -> Unit) {
            core.localEncoders[type] = object : ChunkEncoder<T> {
                override val chunkId = type
                override fun encode(value: T, destination: Buffer) = encoder(value, destination)
            }
        }

        override fun addFormEncoder(type: ChunkId, encoder: RiffFormBodyEncoder<*>) {
            core.formEncoders[type] = encoder
        }

        override fun <T> addFormEncoder(type: ChunkId, disassembler: (T) -> ListMultimap<ChunkId, Any>) {
            core.formEncoders[type] = RiffFormBodyEncoder(core, disassembler)
        }

        override fun addListEncoder(type: ChunkId, encoder: RiffListBodyEncoder<*>) {
            core.listEncoders[type] = encoder
        }

        override fun <T> addListEncoder(type: ChunkId, disassembler: (T) -> ListMultimap<ChunkId, Any>) {
            core.listEncoders[type] = RiffListBodyEncoder(core, disassembler)
        }

        fun build(): RiffEncoderCore = core
    }
}
