package com.kelvsyc.rifflet.internal.rifx

import com.kelvsyc.rifflet.rifx.RifxChunkIds
import com.kelvsyc.rifflet.rifx.RifxFormBodyEncoder
import com.kelvsyc.rifflet.rifx.RifxRootEncoder
import okio.Buffer
import okio.Sink

internal class RifxRootEncoderImpl<T>(
    private val root: RifxRootEncoder.Root,
    private val encoder: RifxFormBodyEncoder<T>,
) : RifxRootEncoder<T> {

    class Builder<T> : RifxRootEncoder.Builder<T> {
        override lateinit var root: RifxRootEncoder.Root
        private var encoderInternal: RifxFormBodyEncoder<T>? = null

        override fun encoder(encoder: RifxFormBodyEncoder<T>) { encoderInternal = encoder }

        fun build(): RifxRootEncoderImpl<T> {
            check(::root.isInitialized) { "root must be set" }
            val encoder = checkNotNull(encoderInternal) { "encoder must be set" }
            return RifxRootEncoderImpl(root, encoder)
        }
    }

    override fun encode(value: T, destination: Sink) {
        val out = Buffer()
        val innerBody = Buffer()
        encoder.encode(value, innerBody)
        writeRifxGroupChunk(RifxChunkIds.RIFX, root.type, innerBody, out)
        destination.write(out, out.size)
        destination.flush()
    }
}
