package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `RACE` section: the file's set of playable/available civilizations.
 */
data class RaceSection(val entries: List<RaceEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.RACE
}
