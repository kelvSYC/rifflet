package com.kelvsyc.rifflet.internal.iff

import com.kelvsyc.rifflet.iff.CatBodyEncoder
import com.kelvsyc.rifflet.iff.FormBodyEncoder
import com.kelvsyc.rifflet.iff.IffChunkIds
import com.kelvsyc.rifflet.iff.IffRootEncoder
import com.kelvsyc.rifflet.iff.ListBodyEncoder
import okio.Buffer
import okio.Sink

internal class IffRootEncoderImpl<T>(
    private val root: IffRootEncoder.Root,
    private val encoder: Any,
) : IffRootEncoder<T> {

    class Builder<T> : IffRootEncoder.Builder<T> {
        override lateinit var root: IffRootEncoder.Root
        private var encoderInternal: Any? = null

        override fun encoder(encoder: FormBodyEncoder<T>) { encoderInternal = encoder }
        override fun encoder(encoder: ListBodyEncoder<T>) { encoderInternal = encoder }
        override fun encoder(encoder: CatBodyEncoder<T>) { encoderInternal = encoder }

        fun build(): IffRootEncoderImpl<T> {
            check(::root.isInitialized) { "root must be set" }
            val encoder = checkNotNull(encoderInternal) { "encoder must be set" }
            when (val r = root) {
                is IffRootEncoder.Root.FormRoot ->
                    require(encoder is FormBodyEncoder<*>) {
                        "encoder for FORM root '${r.type.name}' must be a FormBodyEncoder"
                    }
                is IffRootEncoder.Root.ListRoot ->
                    require(encoder is ListBodyEncoder<*>) {
                        "encoder for LIST root '${r.type.name}' must be a ListBodyEncoder"
                    }
                is IffRootEncoder.Root.CatRoot ->
                    require(encoder is CatBodyEncoder<*>) {
                        "encoder for CAT root '${r.hint.name}' must be a CatBodyEncoder"
                    }
            }
            return IffRootEncoderImpl(root, encoder)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun encode(value: T, destination: Sink) {
        val out = Buffer()
        val innerBody = Buffer()
        when (val r = root) {
            is IffRootEncoder.Root.FormRoot -> {
                (encoder as FormBodyEncoder<T>).encode(value, innerBody)
                writeGroupChunk(IffChunkIds.FORM, r.type, innerBody, out)
            }
            is IffRootEncoder.Root.ListRoot -> {
                (encoder as ListBodyEncoder<T>).encode(value, innerBody)
                writeGroupChunk(IffChunkIds.LIST, r.type, innerBody, out)
            }
            is IffRootEncoder.Root.CatRoot -> {
                (encoder as CatBodyEncoder<T>).encode(value, innerBody)
                writeGroupChunk(IffChunkIds.CAT, r.hint, innerBody, out)
            }
        }
        destination.write(out, out.size)
        destination.flush()
    }
}
