package com.kelvsyc.rifflet.internal.riff

import com.kelvsyc.rifflet.riff.RiffChunkIds
import com.kelvsyc.rifflet.riff.RiffFormBodyEncoder
import com.kelvsyc.rifflet.riff.RiffRootEncoder
import okio.Buffer
import okio.Sink

internal class RiffRootEncoderImpl<T>(
    private val root: RiffRootEncoder.Root,
    private val encoder: RiffFormBodyEncoder<T>,
) : RiffRootEncoder<T> {

    class Builder<T> : RiffRootEncoder.Builder<T> {
        override lateinit var root: RiffRootEncoder.Root
        private var encoderInternal: RiffFormBodyEncoder<T>? = null

        override fun encoder(encoder: RiffFormBodyEncoder<T>) { encoderInternal = encoder }

        fun build(): RiffRootEncoderImpl<T> {
            check(::root.isInitialized) { "root must be set" }
            val encoder = checkNotNull(encoderInternal) { "encoder must be set" }
            return RiffRootEncoderImpl(root, encoder)
        }
    }

    override fun encode(value: T, destination: Sink) {
        val out = Buffer()
        val innerBody = Buffer()
        encoder.encode(value, innerBody)
        writeRiffGroupChunk(RiffChunkIds.RIFF, root.type, innerBody, out)
        destination.write(out, out.size)
        destination.flush()
    }
}
