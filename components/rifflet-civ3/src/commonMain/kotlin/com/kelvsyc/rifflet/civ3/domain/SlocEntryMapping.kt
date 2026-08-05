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

/**
 * Converts a `SLOC` section's domain-layer form back to wire entries, resolving each
 * [StartingLocation]'s [Owner] back into an `ownerType`/`owner` index pair.
 *
 * Throws [IllegalArgumentException] if [StartingLocation.owner] resolves to an [Owner.Civilization]
 * or [Owner.Player] referencing an object not present in the corresponding list argument —
 * `indexOf`-based, the same accepted structural-equality limitation as GOVT/TECH/BLDG/PRTO/CITY's
 * `toWire()`.
 *
 * [Owner.None] writes back `-1` for the wire `owner` int — stateless, no raw value to reconstruct.
 * [Owner.Barbarian] is handled for exhaustiveness only: a domain-constructed [StartingLocation] can
 * never legitimately hold one (see `toDomain()`'s guard) — but if hand-constructed anyway, its
 * preserved `tribeIndex` is still written back correctly. [Owner.Player]/[Owner.Civilization] write
 * back their preserved `unresolvedIndex` whenever the resolved reference is `null`, rather than a
 * hardcoded `-1`.
 */
fun List<StartingLocation>.toWire(races: List<Race>, leads: List<LeadEntry>): List<SlocEntry> = map { location ->
    val (ownerType, owner) = when (val o = location.owner) {
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
    SlocEntry(ownerType = ownerType, owner = owner, x = location.x, y = location.y)
}
