package com.kelvsyc.rifflet.internal.rifx

import com.kelvsyc.rifflet.internal.core.BufferedRawChunk
import com.kelvsyc.rifflet.internal.core.BufferedRawChunkParser
import com.kelvsyc.rifflet.internal.core.readChunkId
import okio.Buffer
import okio.BufferedSource

/**
 * [BufferedRawChunkParser] for the RIFX format.
 *
 * Identical to the RIFF parser except chunk sizes are read as big-endian (per the RIFX spec).
 * Reads the 4-byte type ID and 4-byte big-endian size, then transfers exactly that many bytes
 * from [source] into a fresh [Buffer] via okio segment transfer (zero byte copy). Consumes the
 * pad byte when the declared size is odd.
 */
internal object RifxBufferedChunkParser : BufferedRawChunkParser {
    override fun parse(source: BufferedSource): BufferedRawChunk {
        val type = source.readChunkId()
        val size = source.readInt().toUInt()
        val data = Buffer()
        source.readFully(data, size.toLong())
        if (size and 1u != 0u) source.skip(1)
        return BufferedRawChunk(type, data, size)
    }
}
