package com.kelvsyc.rifflet.iff

import okio.Source

/**
 * Parser of a particular type of IFF chunk.
 */
interface IffChunkParser<T> {
    /**
     * Parses the contents of this chunk from the specified data.
     */
    fun parse(source: Source): T
}
