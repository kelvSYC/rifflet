package com.kelvsyc.rifflet.civ3.domain

/**
 * A placed unit's currently-selected AI Strategy — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.UnitEntry.aiStrategy], mirroring [Prto]'s 20 named `aiStrategies` bit
 * accessors (see `PrtoFlags.kt`) by ordinal position. Ordinal position deliberately matches the
 * documented bit layout — do not reorder these constants.
 */
enum class AiStrategy {
    OFFENSE, DEFENSE, ARTILLERY, EXPLORE, ARMY, CRUISE_MISSILE, AIR_BOMBARD, AIR_DEFENSE,
    NAVAL_POWER, AIR_TRANSPORT, NAVAL_TRANSPORT, NAVAL_CARRIER, TERRAFORM, SETTLE, LEADER,
    TACTICAL_NUKE, ICBM, NAVAL_MISSILE_TRANSPORT, FLAG_UNIT, KING,
}
