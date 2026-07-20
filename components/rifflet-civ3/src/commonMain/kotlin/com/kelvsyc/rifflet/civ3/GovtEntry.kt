package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `GOVT` section: a government type's rules, ruler titles, and relationships
 * to every other government type in the file.
 *
 * @param requiresMaintenance Int-shaped boolean.
 * @param toggle1 `???` — observed: 0 = Republic/Democracy, 1 = other.
 * @param relationships The embedded dynamic array; its on-disk count (`numberOfGovernments`) is
 *   not stored separately — `relationships.size` is already that count.
 * @param toggle2 `???` — observed: -1 = Despotism/Communism, 0 = Anarchy/Monarchy,
 *   1 = Republic/Democracy.
 * @param toggle3 `???` — observed: 1 = Republic/Democracy, 0 = other.
 * @param unknown 4 bytes with zero documented behavior from either primary source;
 *   preserved raw, not validated.
 */
data class GovtEntry(
    val defaultType: Int,
    val transitionType: Int,
    val requiresMaintenance: Int,
    val toggle1: Int,
    val tilePenalty: Int,
    val tradeBonus: Int,
    val name: String,
    val civilopediaEntry: String,
    val maleRulerTitle1: String,
    val femaleRulerTitle1: String,
    val maleRulerTitle2: String,
    val femaleRulerTitle2: String,
    val maleRulerTitle3: String,
    val femaleRulerTitle3: String,
    val maleRulerTitle4: String,
    val femaleRulerTitle4: String,
    val corruption: Int,
    val immuneTo: Int,
    val diplomatsAre: Int,
    val spiesAre: Int,
    val relationships: List<GovtRelationship>,
    val hurrying: Int,
    val assimilationChance: Int,
    val draftLimit: Int,
    val militaryPoliceLimit: Int,
    val rulerTitlePairsUsed: Int,
    val prerequisiteTechnology: Int,
    val scienceRateCap: Int,
    val workerRate: Int,
    val toggle2: Int,
    val toggle3: Int,
    val unknown: ByteString,
    val freeUnits: Int,
    val freeUnitsPerTown: Int,
    val freeUnitsPerCity: Int,
    val freeUnitsPerMetropolis: Int,
    val unitCost: Int,
    val warWeariness: Int,
    val xenophobic: Int,
    val forceResettle: Int,
) {
    init {
        require(unknown.size == 4) { "GovtEntry.unknown must be exactly 4 bytes, was ${unknown.size}" }
    }
}
