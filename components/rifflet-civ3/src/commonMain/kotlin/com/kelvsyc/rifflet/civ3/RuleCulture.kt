package com.kelvsyc.rifflet.civ3

/**
 * The scenario's culture level names and border expansion settings.
 *
 * Corresponds to the Conquests Rules Editor's `General Settings` tab's "Culture" groupbox, in its
 * entirety.
 *
 * @param cultureLevelNames The "Cultural Levels" listbox, in display order (e.g. "Fledgling",
 *   "Weak", "Fragile", "Solid", "Strong", "Glorious"). Genuinely dynamic-length, sized by the
 *   file's own declared count — not a fixed-size list.
 * @param borderExpansionMultiplier The "Lvl." field.
 * @param borderFactor The "Border" field.
 */
data class RuleCulture(
    val cultureLevelNames: List<String>,
    val borderExpansionMultiplier: Int,
    val borderFactor: Int,
)
