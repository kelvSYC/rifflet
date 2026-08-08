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

/**
 * Converts a `CLNY` section's domain-layer form back to wire entries, resolving each [Colony]'s
 * object references back into indices.
 *
 * Throws [IllegalArgumentException] if [Colony.owner] resolves to an [Owner.Civilization] or
 * [Owner.Player] referencing an object not present in the corresponding list argument —
 * `indexOf`-based, the same accepted structural-equality limitation as
 * GOVT/TECH/BLDG/PRTO/CITY/SLOC/UNIT's `toWire()`.
 *
 * [Owner.None] writes back `-1` for the wire `owner` int — stateless, no raw value to
 * reconstruct. [Owner.Barbarian]/[Owner.Player]/[Owner.Civilization] write back their preserved
 * `tribeIndex`/`unresolvedIndex` whenever the resolved reference is absent or `null`, rather than
 * a hardcoded `-1`. [Owner.Barbarian] is handled for exhaustiveness only: a domain-constructed
 * [Colony] can never legitimately hold one — see `toDomain()`'s guard.
 */
fun List<Colony>.toWire(races: List<Race>, leads: List<LeadEntry>): List<ClnyEntry> = map { colony ->
    val (ownerType, owner) = when (val o = colony.owner) {
        is Owner.None -> 0 to -1
        is Owner.Barbarian -> 1 to o.tribeIndex
        is Owner.Civilization -> 2 to (
            o.race?.let {
                val index = races.indexOf(it)
                require(index >= 0) { "Owner.Civilization references a Race not present in races" }
                index
            } ?: o.unresolvedIndex
            )
        is Owner.Player -> 3 to (
            o.lead?.let {
                val index = leads.indexOf(it)
                require(index >= 0) { "Owner.Player references a LeadEntry not present in leads" }
                index
            } ?: o.unresolvedIndex
            )
    }
    ClnyEntry(
        ownerType = ownerType,
        owner = owner,
        x = colony.x,
        y = colony.y,
        improvementType = colony.improvementType,
    )
}
