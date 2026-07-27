package com.kelvsyc.rifflet.civ3

/**
 * The building's combat modifiers.
 *
 * Corresponds to the Conquests Rules Editor's `Improvements and Wonders` tab's "Combat Values"
 * groupbox, in its entirety — confirmed field-for-field against both the Conquests editor
 * (abbreviated labels: "Bombard"/"Defense"/"Air"/"Naval"/"Naval Bombard Def") and the PTW editor
 * (full labels: "Bombard Defense"/"Defense Bonus"/"Air Power"/"Naval Power"/"Naval Bombard
 * Defense"). Unconditionally present, though its 5 fields are not contiguous in the file — the
 * entire [BldgHappiness] cluster and 2 other fields sit between [defenseBonus] and [airPower].
 * Distinct from `BldgEntry.navalDefenseBonus`, a similarly-named field that — despite
 * appearances — doesn't correspond to any control in this box, or anywhere else in either editor.
 *
 * @param bombardDefense The "Bombard"/"Bombard Defense" field.
 * @param navalBombardDefense The "Naval Bombard Def"/"Naval Bombard Defense" field.
 * @param defenseBonus The "Defense"/"Defense Bonus" field.
 * @param airPower The "Air"/"Air Power" field.
 * @param navalPower The "Naval"/"Naval Power" field.
 */
data class BldgCombatValues(
    val bombardDefense: Int,
    val navalBombardDefense: Int,
    val defenseBonus: Int,
    val airPower: Int,
    val navalPower: Int,
)
