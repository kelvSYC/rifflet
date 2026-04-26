package com.kelvsyc.rifflet.iff

import com.kelvsyc.collections.ListMultimap
import com.kelvsyc.rifflet.core.ChunkId

/**
 * A simple implementation of [FormChunkEncoder] that disassembles a domain object into its child chunks
 * using a supplied [disassembler].
 *
 * @param disassembler A function that breaks a domain object of type [T] into a multimap of typed child chunks.
 */
class FormEncoder<T>(private val disassembler: (T) -> ListMultimap<ChunkId, Any>) : FormChunkEncoder<T> {
    override fun encode(value: T): ListMultimap<ChunkId, Any> = disassembler(value)
}
