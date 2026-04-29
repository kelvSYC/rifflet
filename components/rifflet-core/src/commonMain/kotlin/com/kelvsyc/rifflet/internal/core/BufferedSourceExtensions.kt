package com.kelvsyc.rifflet.internal.core

import com.kelvsyc.rifflet.core.ChunkId
import okio.BufferedSource

/**
 * Reads a [ChunkId] from this source: four bytes packed as a big-endian [Int].
 *
 * Chunk type IDs in IFF-family formats (IFF, RIFF, etc.) are four ASCII bytes read
 * left-to-right, so big-endian packing is correct for all variants regardless of each
 * format's data endianness.
 */
fun BufferedSource.readChunkId(): ChunkId = ChunkId(readInt())
