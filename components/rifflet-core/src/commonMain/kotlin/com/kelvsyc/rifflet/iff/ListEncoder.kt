package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId

/**
 * A simple implementation of [ListChunkEncoder] that disassembles a domain object into its child group chunks
 * using a supplied [disassembler].
 *
 * @param disassembler A function that breaks a domain object of type [T] into an ordered list of
 *   `(chunkId, value)` pairs for child group chunks.
 */
class ListEncoder<T>(private val disassembler: (T) -> List<Pair<ChunkId, Any>>) : ListChunkEncoder<T> {
    companion object {
        /**
         * Creates a [ListEncoder] for a `LIST` chunk containing exclusively items of a single group chunk type.
         *
         * Each element of the input [List] is tagged with [itemTypeId] and dispatched to the corresponding
         * registered encoder in the core. Use this when all items in the list are the same type — for example,
         * a sequence of `FORM TRAK` chunks inside a `LIST ALBM`:
         *
         * ```kotlin
         * addListEncoder(ChunkId("ALBM"), ListEncoder.uniform<Track>(ChunkId("TRAK")))
         * ```
         */
        fun <T> uniform(itemTypeId: ChunkId): ListEncoder<List<T>> =
            ListEncoder { items -> items.map { itemTypeId to it as Any } }
    }

    override fun encode(value: T): List<Pair<ChunkId, Any>> = disassembler(value)
}
