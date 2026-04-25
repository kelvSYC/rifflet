package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId
import okio.BufferedSource

/**
 * Reads a [ChunkId] from this source using IFF byte order: four bytes packed as a big-endian [Int].
 */
fun BufferedSource.readChunkId(): ChunkId = ChunkId(readInt())
