package com.kelvsyc.rifflet.riff

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.internal.riff.RiffRootEncoderImpl
import okio.Sink

/**
 * Encodes a domain object of type [T] into a binary RIFF stream.
 *
 * Use [newEncoder] to construct an instance. The [Builder] requires:
 * - A [Root] declaration identifying the form-type to write at the top level.
 * - A [RiffFormBodyEncoder] for the root chunk body.
 */
interface RiffRootEncoder<in T> {

    /**
     * Identifies the form-type to write in the top-level `RIFF` chunk.
     */
    data class Root(val type: ChunkId)

    companion object {
        fun <T> newEncoder(fn: Builder<T>.() -> Unit): RiffRootEncoder<T> =
            RiffRootEncoderImpl.Builder<T>().apply(fn).build()
    }

    interface Builder<T> {
        var root: Root

        fun encoder(encoder: RiffFormBodyEncoder<T>)
    }

    /**
     * Encodes [value] as a single RIFF chunk tree and writes it to [destination].
     */
    fun encode(value: T, destination: Sink)
}
