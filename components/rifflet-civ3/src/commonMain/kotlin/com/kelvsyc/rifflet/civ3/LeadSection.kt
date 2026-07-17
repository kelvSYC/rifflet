package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `LEAD` section: the file's set of player/leader slot definitions.
 */
data class LeadSection(val entries: List<LeadEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.LEAD
}
