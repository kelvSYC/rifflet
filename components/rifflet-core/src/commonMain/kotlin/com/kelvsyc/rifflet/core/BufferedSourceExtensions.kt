package com.kelvsyc.rifflet.core

import okio.BufferedSource

/**
 * Reads a [ChunkId] from this source.
 */
fun BufferedSource.readChunkId(): ChunkId = ChunkId(readByteString(4))
