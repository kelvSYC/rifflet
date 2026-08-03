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

/**
 * Converts a `GOVT` section's domain-layer form back to wire entries, resolving each
 * [Government]'s object references back into indices against [techs]/[espionageMissions]/
 * [experienceLevels] and this list's own roster.
 *
 * Throws [IllegalArgumentException] if [Government.prerequisiteTechnology], [Government.immuneTo],
 * [Government.diplomatsAre], or [Government.spiesAre] references an object not present in the
 * corresponding list argument — a dangling reference at encode time is a real bug, not something
 * to default silently. [Government.relationships] entries for a [Government] outside this list are
 * ignored (irrelevant to this encode); entries missing for a [Government] inside this list default
 * to `GovtRelationship(canBribe = 0, propagandaModifier = 0, resistanceModifier = 0)`.
 *
 * [techs]/[espionageMissions]/[experienceLevels] are still `data class`-based wire types, so these
 * particular lookups are structural-equality matches, not true reference-identity matches (unlike
 * the [Government]-to-[Government] [Government.relationships] lookups below, which are identity-based
 * since [Government] is a plain class) — a narrow, pre-existing limitation that resolves once those
 * sections get their own domain types.
 */
fun List<Government>.toWire(
    techs: List<TechEntry>,
    espionageMissions: List<EspnEntry>,
    experienceLevels: List<ExprEntry>,
): List<GovtEntry> {
    val roster = this
    return map { government ->
        val prerequisiteTechnologyIndex = government.prerequisiteTechnology?.let { tech ->
            val index = techs.indexOf(tech)
            require(index >= 0) { "Government.prerequisiteTechnology references a TechEntry not present in techs" }
            index
        } ?: -1
        val immuneToIndex = government.immuneTo?.let { espn ->
            val index = espionageMissions.indexOf(espn)
            require(index >= 0) { "Government.immuneTo references an EspnEntry not present in espionageMissions" }
            index
        } ?: -1
        val diplomatsAreIndex = government.diplomatsAre?.let { expr ->
            val index = experienceLevels.indexOf(expr)
            require(index >= 0) { "Government.diplomatsAre references an ExprEntry not present in experienceLevels" }
            index
        } ?: -1
        val spiesAreIndex = government.spiesAre?.let { expr ->
            val index = experienceLevels.indexOf(expr)
            require(index >= 0) { "Government.spiesAre references an ExprEntry not present in experienceLevels" }
            index
        } ?: -1
        val relationships = roster.map { other ->
            government.relationships[other] ?: GovtRelationship(canBribe = 0, propagandaModifier = 0, resistanceModifier = 0)
        }

        GovtEntry(
            defaultType = if (government.defaultType) 1 else 0,
            transitionType = if (government.transitionType) 1 else 0,
            requiresMaintenance = if (government.requiresMaintenance) 1 else 0,
            toggle1 = government.toggle1,
            tilePenalty = government.tilePenalty,
            tradeBonus = government.tradeBonus,
            name = government.name,
            civilopediaEntry = government.civilopediaEntry,
            rulerTitles = government.rulerTitles,
            corruption = government.corruption,
            immuneTo = immuneToIndex,
            diplomatsAre = diplomatsAreIndex,
            spiesAre = spiesAreIndex,
            relationships = relationships,
            hurrying = government.hurrying,
            assimilationChance = government.assimilationChance,
            draftLimit = government.draftLimit,
            militaryPoliceLimit = government.militaryPoliceLimit,
            rulerTitlePairsUsed = government.rulerTitlePairsUsed,
            prerequisiteTechnology = prerequisiteTechnologyIndex,
            scienceRateCap = government.scienceRateCap,
            workerRate = government.workerRate,
            unknown = government.unknown,
            unitSupportCosts = government.unitSupportCosts,
            warWeariness = government.warWeariness,
            xenophobic = if (government.xenophobic) 1 else 0,
            forceResettle = if (government.forceResettle) 1 else 0,
        )
    }
}
