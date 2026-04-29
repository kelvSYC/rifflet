package com.kelvsyc.rifflet.rifx

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.internal.rifx.RifxRootEncoderImpl
import okio.Sink

/**
 * Encodes a domain object of type [T] into a binary RIFX stream.
 *
 * Use [newEncoder] to construct an instance. The [Builder] requires:
 * - A [Root] declaration identifying the form-type to write at the top level.
 * - A [RifxFormBodyEncoder] for the root chunk body.
 */
interface RifxRootEncoder<in T> {

    /**
     * Identifies the form-type to write in the top-level `RIFX` chunk.
     */
    data class Root(val type: ChunkId)

    companion object {
        fun <T> newEncoder(fn: Builder<T>.() -> Unit): RifxRootEncoder<T> =
            RifxRootEncoderImpl.Builder<T>().apply(fn).build()
    }

    interface Builder<T> {
        var root: Root

        fun encoder(encoder: RifxFormBodyEncoder<T>)
    }

    /**
     * Encodes [value] as a single RIFX chunk tree and writes it to [destination].
     */
    fun encode(value: T, destination: Sink)
}
