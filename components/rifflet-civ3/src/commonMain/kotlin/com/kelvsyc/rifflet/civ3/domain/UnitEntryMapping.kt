package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.ExprEntry
import com.kelvsyc.rifflet.civ3.LeadEntry
import com.kelvsyc.rifflet.civ3.UnitEntry

/**
 * Converts a parsed `UNIT` section to its domain-layer form.
 *
 * [races]/[prtos] are the already domain-converted `RACE`/`PRTO` lists; [leads]/[experienceLevels]
 * stay wire types (`LEAD`/`EXPR` don't have domain types yet). The caller is responsible for
 * supplying the right lists for this file — this file's own sections converted via their own
 * `toDomain()`, or externally-sourced standard lists, as appropriate.
 *
 * Throws [IllegalArgumentException] if any entry's `ownerType` is outside the documented `0..3`
 * range (see `resolveOwner`'s own KDoc in `Owner.kt`), if it is `0` (None), if it is `2`
 * (Civilization) pointing at RACE index `0` (the barbarian placeholder civilization), or if
 * `aiStrategy` is not `-1` and not a bit set in the resolved prototype's `aiStrategies` (when
 * `unitType` resolves to a real prototype at all — a dangling `unitType` skips this check). Unlike
 * CITY/CLNY/SLOC, `ownerType=1` (Barbarian) is permitted for UNIT.
 */
fun List<UnitEntry>.toDomain(
    races: List<Race>,
    leads: List<LeadEntry>,
    prtos: List<Prto>,
    experienceLevels: List<ExprEntry>,
): List<PlacedUnit> = map { entry ->
    require(entry.ownerType != 0) {
        "UNIT entries cannot be owned by None (ownerType=0) — the Rules/Scenario editor requires " +
            "every unit to belong to a real civilization, player, or barbarians"
    }
    require(!(entry.ownerType == 2 && entry.owner == 0)) {
        "UNIT entries cannot be owned by the barbarian placeholder civilization (ownerType=2, " +
            "owner=0) — the Rules/Scenario editor does not allow it"
    }
    val proto = prtos.getOrNull(entry.unitType)
    val aiStrategy = when {
        entry.aiStrategy == -1 -> null
        entry.aiStrategy in 0..19 -> {
            if (proto != null) {
                require((proto.aiStrategies shr entry.aiStrategy) and 1 == 1) {
                    "UnitEntry.aiStrategy=${entry.aiStrategy} is not a bit set in its prototype's " +
                        "aiStrategies (${proto.aiStrategies})"
                }
            }
            AiStrategy.entries[entry.aiStrategy]
        }
        else -> throw IllegalArgumentException(
            "UnitEntry.aiStrategy=${entry.aiStrategy} is not -1 or in 0..19",
        )
    }
    PlacedUnit(
        x = entry.x,
        y = entry.y,
        legacyName = entry.legacyName,
        ptwName = entry.ptwName,
        owner = resolveOwner(entry.ownerType, entry.owner, races, leads),
        unitType = proto,
        experienceLevel = experienceLevels.getOrNull(entry.experienceLevel),
        aiStrategy = aiStrategy,
        useCivilizationKing = entry.useCivilizationKing != 0,
    )
}

/**
 * Converts a `UNIT` section's domain-layer form back to wire entries, resolving each
 * [PlacedUnit]'s object references back into indices.
 *
 * Throws [IllegalArgumentException] if [PlacedUnit.owner], [PlacedUnit.unitType], or
 * [PlacedUnit.experienceLevel] resolves to an object not present in the corresponding list
 * argument — `indexOf`-based, the same accepted structural-equality limitation as
 * GOVT/TECH/BLDG/PRTO/CITY/SLOC's `toWire()`.
 *
 * [Owner.None] writes back `-1` for the wire `owner` int — stateless, no raw value to reconstruct.
 * [Owner.Barbarian]/[Owner.Player]/[Owner.Civilization] write back their preserved
 * `tribeIndex`/`unresolvedIndex` whenever the resolved reference is `null`, rather than a
 * hardcoded `-1`. A `null` [PlacedUnit.unitType]/[PlacedUnit.experienceLevel] writes back `-1` —
 * unlike [Owner], neither field preserves a dangling wire index across a `toDomain()`/`toWire()`
 * round-trip: a genuinely dangling `unitType`/`experienceLevel` index is indistinguishable from a
 * legitimately absent one once resolved to `null`.
 */
fun List<PlacedUnit>.toWire(
    races: List<Race>,
    leads: List<LeadEntry>,
    prtos: List<Prto>,
    experienceLevels: List<ExprEntry>,
): List<UnitEntry> = map { unit ->
    val (ownerType, owner) = when (val o = unit.owner) {
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
    val unitType = unit.unitType?.let {
        val index = prtos.indexOf(it)
        require(index >= 0) { "PlacedUnit.unitType references a Prto not present in prtos" }
        index
    } ?: -1
    val experienceLevel = unit.experienceLevel?.let {
        val index = experienceLevels.indexOf(it)
        require(index >= 0) {
            "PlacedUnit.experienceLevel references an ExprEntry not present in experienceLevels"
        }
        index
    } ?: -1
    UnitEntry(
        legacyName = unit.legacyName,
        ownerType = ownerType,
        experienceLevel = experienceLevel,
        owner = owner,
        unitType = unitType,
        aiStrategy = unit.aiStrategy?.ordinal ?: -1,
        x = unit.x,
        y = unit.y,
        ptwName = unit.ptwName,
        useCivilizationKing = if (unit.useCivilizationKing) 1 else 0,
    )
}
