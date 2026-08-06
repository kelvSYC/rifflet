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
