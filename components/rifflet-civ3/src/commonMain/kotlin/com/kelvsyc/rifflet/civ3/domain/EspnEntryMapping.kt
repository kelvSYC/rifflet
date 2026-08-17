package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.EspnEntry

/**
 * Converts a parsed `ESPN` section to its domain-layer form. No `require()` guards, no
 * cross-references.
 */
fun List<EspnEntry>.toDomain(): List<EspionageMission> = map {
    EspionageMission(
        name = it.name,
        description = it.description,
        civilopediaEntry = it.civilopediaEntry,
        missionFlags = it.missionFlags,
        baseCost = it.baseCost,
    )
}

/**
 * Converts an `ESPN` section's domain-layer form back to wire entries.
 */
fun List<EspionageMission>.toWire(): List<EspnEntry> = map {
    EspnEntry(
        description = it.description,
        name = it.name,
        civilopediaEntry = it.civilopediaEntry,
        missionFlags = it.missionFlags,
        baseCost = it.baseCost,
    )
}
