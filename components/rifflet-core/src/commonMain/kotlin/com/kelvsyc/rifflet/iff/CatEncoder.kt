package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId

/**
 * A simple implementation of [CatChunkEncoder] that disassembles a domain object into its child group chunks
 * using a supplied [disassembler].
 *
 * @param disassembler A function that breaks a domain object of type [T] into an ordered list of
 *   `(chunkId, value)` pairs for child group chunks.
 */
class CatEncoder<T>(private val disassembler: (T) -> List<Pair<ChunkId, Any>>) : CatChunkEncoder<T> {
    companion object {
        /**
         * Creates a [CatEncoder] for a `CAT ` chunk containing exclusively items of a single group chunk type.
         *
         * Each element of the input [List] is tagged with [itemTypeId] and dispatched to the corresponding
         * registered encoder in the core. Use this when all items in the cat are the same type — for example,
         * a sequence of `FORM AIFF` chunks inside a `CAT AIFF`:
         *
         * ```kotlin
         * addCatEncoder(ChunkId("AIFF"), CatEncoder.uniform<AiffFile>(ChunkId("AIFF")))
         * ```
         */
        fun <T> uniform(itemTypeId: ChunkId): CatEncoder<List<T>> =
            CatEncoder { items -> items.map { itemTypeId to it as Any } }
    }

    override fun encode(value: T): List<Pair<ChunkId, Any>> = disassembler(value)
}
