package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.ClnyEntry
import com.kelvsyc.rifflet.civ3.LeadEntry

/**
 * Converts a parsed `CLNY` section to its domain-layer form.
 *
 * [races] is the already domain-converted `RACE` list; [leads] stays a wire type (`LEAD` doesn't
 * have a domain type yet). The caller is responsible for supplying the right lists for this file —
 * this file's own sections converted via their own `toDomain()`, or externally-sourced standard
 * lists, as appropriate.
 *
 * Throws [IllegalArgumentException] if any entry's `ownerType` is outside the documented `0..3`
 * range (see `resolveOwner`'s own KDoc in `Owner.kt`), if it is `0` (None) or `1` (Barbarian), or
 * if it is `2` (Civilization) pointing at RACE index `0` (the barbarian placeholder civilization)
 * — the real Rules/Scenario editor requires every colony/airfield/radar tower/outpost to belong to
 * a real civilization or player, and never the barbarian placeholder, uniformly across all 4
 * `ClnyImprovementType` values.
 */
fun List<ClnyEntry>.toDomain(races: List<Race>, leads: List<LeadEntry>): List<Colony> = map { entry ->
    require(entry.ownerType != 0 && entry.ownerType != 1) {
        "CLNY entries cannot be owned by None or Barbarian (ownerType=${entry.ownerType}) — the " +
            "Rules/Scenario editor requires every colony/airfield/radar tower/outpost to belong " +
            "to a real civilization or player"
    }
    require(!(entry.ownerType == 2 && entry.owner == 0)) {
        "CLNY entries cannot be owned by the barbarian placeholder civilization (ownerType=2, " +
            "owner=0) — the Rules/Scenario editor does not allow it"
    }
    Colony(
        x = entry.x,
        y = entry.y,
        owner = resolveOwner(entry.ownerType, entry.owner, races, leads),
        improvementType = entry.improvementType,
    )
}
