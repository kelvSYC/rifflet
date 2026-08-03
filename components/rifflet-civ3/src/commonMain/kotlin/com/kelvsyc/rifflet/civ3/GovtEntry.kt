package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `GOVT` section: a government type's rules, ruler titles, and relationships
 * to every other government type in the file.
 *
 * @param defaultType Int-shaped boolean marking this entry as the Default government — the one a
 *   civ starts with before researching any government tech (Despotism, in every real file). Every
 *   real file has at most one such entry; a small number of real scenarios (Fall of Rome,
 *   Napoleonic Europe, each present in both single-player and multiplayer file form) have none at
 *   all — both give every playable civ a custom starting government via its own player settings
 *   instead, sidestepping the missing default.
 * @param transitionType Int-shaped boolean marking this entry as the Transition government — the
 *   one a civ falls into after a revolution (Anarchy, in every real file). Every real file has
 *   exactly one such entry. [defaultType] and [transitionType] can both be set on the same entry.
 * @param requiresMaintenance Int-shaped boolean.
 * @param toggle1 Vestigial — carries leftover, uninitialized memory content rather than real
 *   government data. No control in the Governments editor's Flags or Espionage groupboxes
 *   corresponds to it. 4 raw bytes, not an `Int` — there's no numeric meaning to preserve.
 * @param rulerTitles This government's ruler titles. See [GovtRulerTitles].
 * @param relationships The embedded dynamic array; its on-disk count (`numberOfGovernments`) is
 *   not stored separately — `relationships.size` is already that count.
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
 * @param unknown 12 contiguous bytes with zero documented behavior, matching [toggle1]'s
 *   vestigial, uninitialized-memory nature. Preserved raw, not validated.
 * @param unitSupportCosts This government's unit support costs. See [GovtUnitSupportCosts].
 * @param warWeariness See [GovtWarWeariness] for what each value means, per the Governments
 *   editor's own "War Weariness" dropdown.
 */
data class GovtEntry(
    val defaultType: Int,
    val transitionType: Int,
    val requiresMaintenance: Int,
    val toggle1: ByteString,
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
    val unknown: ByteString,
    val unitSupportCosts: GovtUnitSupportCosts,
    val warWeariness: GovtWarWeariness,
    val xenophobic: Int,
    val forceResettle: Int,
) {
    init {
        require(toggle1.size == 4) { "GovtEntry.toggle1 must be exactly 4 bytes, was ${toggle1.size}" }
        require(unknown.size == 12) { "GovtEntry.unknown must be exactly 12 bytes, was ${unknown.size}" }
    }
}
