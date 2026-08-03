package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.EspnEntry
import com.kelvsyc.rifflet.civ3.ExprEntry
import com.kelvsyc.rifflet.civ3.GovtEntry
import com.kelvsyc.rifflet.civ3.GovtRelationship
import com.kelvsyc.rifflet.civ3.TechEntry

/**
 * Converts a parsed `GOVT` section to its domain-layer form, resolving each entry's raw
 * cross-section indices into real object references against [techs]/[espionageMissions]/
 * [experienceLevels], and its `relationships` array into a map keyed by sibling [Government]s.
 */
fun List<GovtEntry>.toDomain(
    techs: List<TechEntry>,
    espionageMissions: List<EspnEntry>,
    experienceLevels: List<ExprEntry>,
): List<Government> {
    val governments = map { entry ->
        Government(
            name = entry.name,
            civilopediaEntry = entry.civilopediaEntry,
            rulerTitles = entry.rulerTitles,
            corruption = entry.corruption,
            hurrying = entry.hurrying,
            unitSupportCosts = entry.unitSupportCosts,
            warWeariness = entry.warWeariness,
            defaultType = entry.defaultType != 0,
            transitionType = entry.transitionType != 0,
            requiresMaintenance = entry.requiresMaintenance != 0,
            toggle1 = entry.toggle1,
            tilePenalty = entry.tilePenalty,
            tradeBonus = entry.tradeBonus,
            assimilationChance = entry.assimilationChance,
            draftLimit = entry.draftLimit,
            militaryPoliceLimit = entry.militaryPoliceLimit,
            rulerTitlePairsUsed = entry.rulerTitlePairsUsed,
            scienceRateCap = entry.scienceRateCap,
            workerRate = entry.workerRate,
            toggle2 = entry.toggle2,
            toggle3 = entry.toggle3,
            unknown = entry.unknown,
            xenophobic = entry.xenophobic != 0,
            forceResettle = entry.forceResettle != 0,
        )
    }

    forEachIndexed { index, entry ->
        val government = governments[index]
        government.prerequisiteTechnology = techs.getOrNull(entry.prerequisiteTechnology)
        government.immuneTo = espionageMissions.getOrNull(entry.immuneTo)
        government.diplomatsAre = experienceLevels.getOrNull(entry.diplomatsAre)
        government.spiesAre = experienceLevels.getOrNull(entry.spiesAre)
        entry.relationships.forEachIndexed { otherIndex, relationship ->
            governments.getOrNull(otherIndex)?.let { other -> government.relationships[other] = relationship }
        }
    }

    return governments
}
