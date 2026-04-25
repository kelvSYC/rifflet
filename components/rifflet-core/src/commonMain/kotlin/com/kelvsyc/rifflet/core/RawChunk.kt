package com.kelvsyc.rifflet.core

import okio.ByteString

/**
 * Class representing a raw chunk of data.
 *
 * A chunk of data contains type information as well as binary data.
 */
data class RawChunk(val type: ChunkId, val data: ByteString)
