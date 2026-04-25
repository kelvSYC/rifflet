package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.RawChunk
import com.kelvsyc.rifflet.core.RawChunkParser
import com.kelvsyc.rifflet.core.readChunkId
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
        // readByteString throws EOFException if the source is exhausted before size bytes are read,
        // correctly handling both total exhaustion and partial reads.
        val data = input.readByteString(size.toLong())

        // IFF requires that chunks with odd lengths be padded with a pad byte, so we consume it
        if (size and 1 != 0) input.skip(1)

        return RawChunk(type, data)
    }
}
