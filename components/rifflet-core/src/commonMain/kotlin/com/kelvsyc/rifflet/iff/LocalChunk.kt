package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.RawChunk

/**
 * A `LocalChunk` is type of [IffChunk] to represent any user-defined chunk. It is effectively a wrapper around a
 * [RawChunk] meant to comply with the [IffChunk] interface.
 */
@JvmInline
value class LocalChunk(val data: RawChunk) : IffChunk
