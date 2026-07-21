package com.kelvsyc.rifflet.civ3

/**
 * One entry of `GOVT`'s embedded government-relationship array: how one government type
 * relates to another (propaganda and resistance modifiers).
 *
 * Existing reverse-engineering documentation names this array's second field
 * `briberyModifier`; that name is incorrect — it holds the Conquests Rules Editor's "Propaganda
 * Modifier" values, not bribery data. The byte layout itself
 * (`canBribe`/[propagandaModifier]/[resistanceModifier], 12 bytes total) is otherwise correct
 * as documented.
 *
 * @param canBribe Int-shaped boolean: 0 = no, 1 = yes. Uniformly reads as the classic MSVC
 *   debug-CRT heap-poison pattern (`0xCDCDCDCD`) in real files; the Conquests Rules Editor's
 *   "Governments" tab has no corresponding checkbox, so this field may not be exposed by the
 *   editor UI at all.
 */
data class GovtRelationship(
    val canBribe: Int,
    val propagandaModifier: Int,
    val resistanceModifier: Int,
)
