package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `EXPR` section: the file's set of combat experience levels.
 */
data class ExprSection(val entries: List<ExprEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.EXPR
}
