package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.LeadEntry
import com.kelvsyc.rifflet.civ3.SlocEntry

/**
 * Converts a parsed `SLOC` section to its domain-layer form.
 *
 * [races]/[leads] may legitimately be empty even for entries with `ownerType` 2/3 respectively:
 * `RACE` is gated by Custom Rules and `LEAD` by Custom Player Data, both independent of the Custom
 * Map toggle that governs `SLOC`'s own presence. The caller is responsible for supplying the right
 * lists for this file — this file's own sections converted via their own `toDomain()`, or
 * externally-sourced standard lists, as appropriate.
 *
 * Throws [IllegalArgumentException] if any entry's `ownerType` is outside the documented `0..3`
 * range (see `resolveOwner`'s own KDoc in `Owner.kt`), or if it is `1` (Barbarian) — the real
 * Rules/Scenario editor never allows a Barbarian-owned starting location.
 */
fun List<SlocEntry>.toDomain(races: List<Race>, leads: List<LeadEntry>): List<StartingLocation> = map { entry ->
    val owner = resolveOwner(entry.ownerType, entry.owner, races, leads)
    require(owner !is Owner.Barbarian) {
        "SLOC entries cannot be owned by Barbarian (ownerType=1) — the Rules/Scenario editor does not allow it"
    }
    StartingLocation(x = entry.x, y = entry.y, owner = owner)
}
