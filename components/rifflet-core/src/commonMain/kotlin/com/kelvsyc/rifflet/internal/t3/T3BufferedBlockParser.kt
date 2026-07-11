package com.kelvsyc.rifflet.internal.t3

import com.kelvsyc.rifflet.internal.core.readChunkId
import okio.Buffer
import okio.BufferedSource

/**
 * Reads a single T3 block: 4-byte type ID, 4-byte little-endian size, 2-byte little-endian flags,
 * then transfers exactly `size` bytes of body via okio segment transfer (zero byte copy).
 *
 * T3 has no padding byte after odd-length bodies.
 */
internal object T3BufferedBlockParser {
    fun parse(source: BufferedSource): T3RawBufferedBlock {
        val type = source.readChunkId()
        val size = source.readIntLe().toUInt()
        val flags = source.readShortLe().toInt() and 0xFFFF
        val data = Buffer()
        source.readFully(data, size.toLong())
        return T3RawBufferedBlock(type, flags, data, size)
    }
}
