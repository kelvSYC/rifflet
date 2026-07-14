package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * Named [ChunkId] constants for Civ3 section markers with dedicated domain types. Extended
 * incrementally as more section types are modeled; unlisted markers fall back to
 * [Civ3RawSection].
 */
object Civ3SectionIds {
    val WSIZ = ChunkId("WSIZ")
    val DIFF = ChunkId("DIFF")
    val ERAS = ChunkId("ERAS")
    val GOVT = ChunkId("GOVT")
    val RACE = ChunkId("RACE")
    val EXPR = ChunkId("EXPR")
    val CULT = ChunkId("CULT")
    val CTZN = ChunkId("CTZN")
    val GOOD = ChunkId("GOOD")
    val ESPN = ChunkId("ESPN")
    val SLOC = ChunkId("SLOC")
}
