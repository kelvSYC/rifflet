package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `GAME` section: the file's global scenario/ruleset settings.
 */
data class GameSection(val entries: List<GameEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.GAME
}
