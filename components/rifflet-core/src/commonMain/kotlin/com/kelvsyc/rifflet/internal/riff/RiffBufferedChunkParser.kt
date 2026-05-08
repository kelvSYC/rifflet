package com.kelvsyc.rifflet.internal.riff

import com.kelvsyc.rifflet.internal.core.BufferedRawChunk
import com.kelvsyc.rifflet.internal.core.BufferedRawChunkParser
import com.kelvsyc.rifflet.internal.core.readChunkId
import okio.Buffer
import okio.BufferedSource

/**
 * [BufferedRawChunkParser] for the RIFF format.
 *
 * Reads the 4-byte type ID and 4-byte little-endian size, then transfers exactly that many bytes
 * from [source] into a fresh [Buffer] via okio segment transfer (zero byte copy). Consumes the
 * RIFF pad byte when the declared size is odd.
 */
object RiffBufferedChunkParser : BufferedRawChunkParser {
    override fun parse(source: BufferedSource): BufferedRawChunk {
        val type = source.readChunkId()
        val size = source.readIntLe().toUInt()
        val data = Buffer()
        source.readFully(data, size.toLong())
        if (size and 1u != 0u) source.skip(1)
        return BufferedRawChunk(type, data, size)
    }
}
