package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `CITY` section: the file's set of placed city instances.
 */
data class CitySection(val entries: List<CityEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.CITY
}
