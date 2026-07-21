package com.kelvsyc.rifflet.civ3

/**
 * One entry of `GOVT`'s embedded government-relationship array: how one government type
 * relates to another (bribery and resistance modifiers).
 *
 * Open question: the Conquests Rules Editor also shows a "Propaganda Modifier" control,
 * structurally identical to [resistanceModifier]'s "Resistance Modifier" control, with no
 * corresponding field here — existing reverse-engineering documentation is explicit that this
 * array's entries are exactly 12 bytes (`canBribe`/`briberyModifier`/`resistanceModifier`).
 * Possibilities include the
 * UI computing "Propaganda Modifier" from `CultEntry` data rather than storing it
 * per-government-relationship, or a field this codebase doesn't yet parse; misjudging which
 * would shift every subsequent field's offset, so this is left unresolved pending byte-level
 * confirmation.
 *
 * @param canBribe Int-shaped boolean: 0 = no, 1 = yes.
 */
data class GovtRelationship(
    val canBribe: Int,
    val briberyModifier: Int,
    val resistanceModifier: Int,
)
