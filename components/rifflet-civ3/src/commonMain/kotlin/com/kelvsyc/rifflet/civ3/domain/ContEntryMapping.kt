package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.ContEntry

/**
 * Converts a parsed `CONT` section to its domain-layer form. No `require()` guards, no
 * cross-references: `CONT` is pure scalar data with no referential-integrity failure mode.
 */
fun List<ContEntry>.toDomain(): List<Continent> = map {
    Continent(type = it.type, numberOfTiles = it.numberOfTiles)
}

/**
 * Converts a `CONT` section's domain-layer form back to wire entries.
 */
fun List<Continent>.toWire(): List<ContEntry> = map {
    ContEntry(type = it.type, numberOfTiles = it.numberOfTiles)
}
