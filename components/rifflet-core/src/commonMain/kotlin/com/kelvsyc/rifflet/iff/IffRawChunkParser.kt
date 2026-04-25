package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.RawChunk
import com.kelvsyc.rifflet.core.RawChunkParser
import com.kelvsyc.rifflet.core.readChunkId
import okio.Buffer
import okio.Source
import okio.buffer

/**
 * Canonical implementation of a [RawChunkParser] for the Interchange File Format.
 */
object IffRawChunkParser : RawChunkParser {
    override fun parse(source: Source): RawChunk {
        val input = source.buffer()
        val type = input.readChunkId()
        val size = input.readInt()
        val data = Buffer()
        val bytesRead = input.read(data, size.toLong())
        if (bytesRead != size.toLong()) {
            if (bytesRead == -1L) {
                throw IllegalStateException("Source is exhausted: expected $size bytes")
            } else {
                throw IllegalStateException("Unexpected end of source: could only read $bytesRead of $size bytes")
            }
        }

        // IFF requires that chunks with odd lengths be padded with a pad byte, so we consume it
        if (size and 1 != 0) input.skip(1)

        return RawChunk(type, data.readByteString())
    }
}
