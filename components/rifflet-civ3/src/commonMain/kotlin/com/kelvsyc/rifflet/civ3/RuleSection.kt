package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `RULE` section: the file's general game-rule settings.
 */
data class RuleSection(val entries: List<RuleEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.RULE
}
