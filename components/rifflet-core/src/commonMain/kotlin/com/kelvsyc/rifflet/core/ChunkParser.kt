package com.kelvsyc.rifflet.core

import okio.Source

/**
 * Parser that reads a single chunk from a [Source] and returns a domain object of type [T].
 *
 * Shared by all format-specific chunk parsing pipelines (IFF, RIFF, etc.).
 */
interface ChunkParser<T> {
    fun parse(source: Source): T
}
