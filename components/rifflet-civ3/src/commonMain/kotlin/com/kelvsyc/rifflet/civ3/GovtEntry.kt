package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `GOVT` section: a government type's rules, ruler titles, and relationships
 * to every other government type in the file.
 *
 * @param defaultType Int-shaped boolean marking this entry as the Default government — the one a
 *   civ starts with before researching any government tech (Despotism, in every real file). Every
 *   real file has at most one such entry; a small number of real multiplayer scenarios have none
 *   at all.
 * @param transitionType Int-shaped boolean marking this entry as the Transition government — the
 *   one a civ falls into after a revolution (Anarchy, in every real file). Every real file has
 *   exactly one such entry. [defaultType] and [transitionType] can both be set on the same entry.
 * @param requiresMaintenance Int-shaped boolean.
 * @param toggle1 `???` — observed: 0 = Republic/Democracy, 1 = other.
 * @param rulerTitles This government's ruler titles. See [GovtRulerTitles].
 * @param relationships The embedded dynamic array; its on-disk count (`numberOfGovernments`) is
 *   not stored separately — `relationships.size` is already that count.
 * @param toggle2 `???` — observed: -1 = Despotism/Communism, 0 = Anarchy/Monarchy,
 *   1 = Republic/Democracy.
 * @param toggle3 `???` — observed: 1 = Republic/Democracy, 0 = other.
 * @param corruption This government's Corruption and Waste severity — see [GovtCorruption] for
 *   what each value means, per the Governments editor's own radio group.
 * @param immuneTo An `ESPN` section index, or `-1` for "None" — see [immuneToEspn]. Per the
 *   Governments editor's Espionage groupbox "Immune" dropdown.
 * @param diplomatsAre An `EXPR` section index — see [diplomatsAreExpr]. Per the Governments
 *   editor's Espionage groupbox "Diplomats Are" dropdown.
 * @param spiesAre An `EXPR` section index — see [spiesAreExpr]. Per the Governments editor's
 *   Espionage groupbox "Spies Are" dropdown.
 * @param hurrying See [GovtHurrying] for what each value means, per the Governments editor's own
 *   "Hurrying" dropdown.
 * @param prerequisiteTechnology A `TECH` section index, per the Governments editor's own
 *   "Prerequisite" dropdown.
 * @param unknown 4 bytes with zero documented behavior from either reverse-engineering source;
 *   preserved raw, not validated.
 * @param unitSupportCosts This government's unit support costs. See [GovtUnitSupportCosts].
 * @param warWeariness See [GovtWarWeariness] for what each value means, per the Governments
 *   editor's own "War Weariness" dropdown.
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
    val rulerTitles: GovtRulerTitles,
    val corruption: GovtCorruption,
    val immuneTo: Int,
    val diplomatsAre: Int,
    val spiesAre: Int,
    val relationships: List<GovtRelationship>,
    val hurrying: GovtHurrying,
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
    val unitSupportCosts: GovtUnitSupportCosts,
    val warWeariness: GovtWarWeariness,
    val xenophobic: Int,
    val forceResettle: Int,
) {
    init {
        require(unknown.size == 4) { "GovtEntry.unknown must be exactly 4 bytes, was ${unknown.size}" }
    }
}
