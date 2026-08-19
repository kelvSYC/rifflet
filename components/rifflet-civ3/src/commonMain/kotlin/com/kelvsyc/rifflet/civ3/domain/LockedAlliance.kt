package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.AllianceSlot

/**
 * This scenario's locked-alliance identities, war relations, and victory type, mutable — the
 * domain-layer counterpart to [com.kelvsyc.rifflet.civ3.GameLockedAlliance].
 */
data class LockedAlliance(
    var alliances: MutableMap<AllianceSlot, Alliance> = mutableMapOf(),
    var relations: AllianceRelations = AllianceRelations(),
    var victoryType: AllianceVictoryType = AllianceVictoryType.INDIVIDUAL,
)
