package com.kelvsyc.rifflet.iff

/**
 * A simple implementation of [CatChunkEncoder] that disassembles a domain object into its child group chunks
 * using a supplied [disassembler].
 *
 * @param disassembler A function that breaks a domain object of type [T] into an ordered list of typed child chunks.
 */
class CatEncoder<T>(private val disassembler: (T) -> List<Any>) : CatChunkEncoder<T> {
    override fun encode(value: T): List<Any> = disassembler(value)
}
