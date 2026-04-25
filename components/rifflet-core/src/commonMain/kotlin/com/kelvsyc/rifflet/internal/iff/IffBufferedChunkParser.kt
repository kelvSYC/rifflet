package com.kelvsyc.rifflet.internal.iff

import com.kelvsyc.rifflet.iff.readChunkId
import com.kelvsyc.rifflet.iff.IffChunkIds
import com.kelvsyc.rifflet.internal.core.BufferedRawChunk
import com.kelvsyc.rifflet.internal.core.BufferedRawChunkParser
import okio.Buffer
import okio.BufferedSource

/**
 * [BufferedRawChunkParser] for the Interchange File Format.
 */
object IffBufferedChunkParser : BufferedRawChunkParser {
    /**
     * Reads the 4-byte type ID and 4-byte big-endian size, then transfers exactly that many bytes from
     * [source] into a fresh [Buffer] via okio segment transfer (zero byte copy). Consumes the IFF pad
     * byte when the declared size is odd.
     */
    override fun parse(source: BufferedSource): BufferedRawChunk {
        val type = source.readChunkId()
        val size = source.readInt()
        val data = Buffer()
        if (type != IffChunkIds.blank) {
            source.readFully(data, size.toLong())
        } else {
            source.skip(size.toLong())
        }
        if (size and 1 != 0) source.skip(1)
        return BufferedRawChunk(type, data, size)
    }
}
