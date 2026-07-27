package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `RACE` section: the file's set of playable/available civilizations.
 *
 * Entry 0 is always the barbarian placeholder. Its display name can be changed by a scenario (a
 * real PTW scenario renames it "Ronin" to fit a Sengoku Japan setting), but the Rules Editor
 * otherwise locks the entry to a fixed shape — see [validateRaceBarbarianPlaceholder].
 */
data class RaceSection(val entries: List<RaceEntry>) : Civ3Section {
    override val chunkId: ChunkId get() = Civ3SectionIds.RACE
}
