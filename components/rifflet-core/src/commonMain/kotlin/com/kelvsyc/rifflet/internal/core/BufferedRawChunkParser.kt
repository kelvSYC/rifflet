package com.kelvsyc.rifflet.internal.core

import okio.BufferedSource

/**
 * Reads a single [BufferedRawChunk] from a [BufferedSource], transferring the chunk body via okio
 * segment moves without materialising an intermediate [ByteString][okio.ByteString].
 *
 * Implementations are responsible for format-specific framing (endianness of the size field,
 * padding rules, etc.). The caller must not close [source] between calls.
 */
interface BufferedRawChunkParser {
    fun parse(source: BufferedSource): BufferedRawChunk
}
