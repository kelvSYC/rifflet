package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkWriter
import com.kelvsyc.rifflet.core.RawChunk
import okio.Sink
import okio.buffer
import kotlin.math.max

/**
 * [ChunkWriter] implementation that writes chunks in the original Interchange File Format.
 *
 * IFF uses big-endian chunk sizes. Every chunk must start on an even byte offset, so a pad byte (not counted in
 * [ckSize][RawChunk]) is appended after the data whenever [ckSize][RawChunk] is odd.
 *
 * This writer does not close [destination]; callers are responsible for the sink's lifecycle.
 *
 * For RIFF (little-endian sizes) a separate writer implementation is needed.
 */
object IffChunkWriter : ChunkWriter {
    override fun writeChunk(chunk: RawChunk, destination: Sink) = writeChunk(chunk, chunk.data.size, destination)

    override fun writeChunk(chunk: RawChunk, size: Int, destination: Sink) {
        val out = destination.buffer()
        out.write(chunk.type.data)
        val baseSize = chunk.data.size
        val ckSize = max(size, baseSize)
        out.writeInt(ckSize)
        out.write(chunk.data)
        // Zero-fill any declared size beyond the actual data
        repeat(ckSize - baseSize) { out.writeByte(0) }
        // IFF: append one pad byte when ckSize is odd so the next chunk starts on an even offset
        if (ckSize and 1 != 0) out.writeByte(0)
        out.flush()
    }
}
