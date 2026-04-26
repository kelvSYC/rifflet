package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.internal.iff.IffRootEncoderImpl
import okio.Sink

/**
 * Encodes a domain object of type [T] into a binary IFF stream.
 *
 * Use [newEncoder] to construct an instance. The [Builder] requires two things:
 * - A [Root] declaration identifying which group chunk kind and type ID to write at the top level.
 * - The body encoder for that root chunk, supplied via [Builder.encoder].
 *
 * The encoder kind must match the declared [Root] kind — supplying a [ListBodyEncoder] for a
 * [Root.FormRoot], for example, is caught at build time.
 *
 * **Variant outer IDs are not supported.** The root chunk is always written with the standard outer
 * ID (`FORM`, `LIST`, or `CAT `). There is no mechanism to write a variant outer ID (`FOR1`–`FOR9`,
 * `LIS1`–`LIS9`, `CAT1`–`CAT9`). This is intentional until a concrete format requiring variant
 * outer IDs is identified.
 */
interface IffRootEncoder<in T> {

    /**
     * Identifies which group chunk kind and type ID to write at the top level of the IFF stream.
     */
    sealed interface Root {
        /** The root is a `FORM` chunk whose form-type field is [type]. */
        data class FormRoot(val type: ChunkId) : Root

        /** The root is a `LIST` chunk whose list-type field is [type]. */
        data class ListRoot(val type: ChunkId) : Root

        /** The root is a `CAT ` chunk whose hint field is [hint]. */
        data class CatRoot(val hint: ChunkId) : Root
    }

    companion object {
        /**
         * Creates a new [IffRootEncoder] configured by [fn].
         */
        fun <T> newEncoder(fn: Builder<T>.() -> Unit): IffRootEncoder<T> =
            IffRootEncoderImpl.Builder<T>().apply(fn).build()
    }

    /**
     * Builder for [IffRootEncoder].
     *
     * Set [root] to declare the root chunk to write, then supply the matching body encoder via
     * one of the [encoder] overloads. The encoder kind must match the [root] kind or [build][newEncoder]
     * throws [IllegalArgumentException].
     */
    interface Builder<T> {
        /**
         * Declares the root chunk identity. Must be set before the builder completes.
         */
        var root: Root

        /** Supplies a [FormBodyEncoder] as the root encoder. [root] must be a [Root.FormRoot]. */
        fun encoder(encoder: FormBodyEncoder<T>)

        /** Supplies a [ListBodyEncoder] as the root encoder. [root] must be a [Root.ListRoot]. */
        fun encoder(encoder: ListBodyEncoder<T>)

        /** Supplies a [CatBodyEncoder] as the root encoder. [root] must be a [Root.CatRoot]. */
        fun encoder(encoder: CatBodyEncoder<T>)
    }

    /**
     * Encodes [value] as a single IFF chunk tree and writes it to [destination].
     */
    fun encode(value: T, destination: Sink)
}
