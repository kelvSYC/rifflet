package com.kelvsyc.rifflet.iff

/**
 * A simple implementation of [ListChunkEncoder] that disassembles a domain object into its child group chunks
 * using a supplied [disassembler].
 *
 * @param disassembler A function that breaks a domain object of type [T] into an ordered list of typed child chunks.
 */
class ListEncoder<T>(private val disassembler: (T) -> List<Any>) : ListChunkEncoder<T> {
    override fun encode(value: T): List<Any> = disassembler(value)
}
