package com.kelvsyc.rifflet.civ3

/**
 * The building's prerequisites.
 *
 * Corresponds to the Conquests Rules Editor's `Improvements and Wonders` tab's "Required"
 * groupbox, in its entirety. Unconditionally present, though its 3 fields are not contiguous in
 * the file — other fields sit between [requiredBuilding] and [requiredGovernment], and between
 * [requiredGovernment] and [requiredAdvance].
 *
 * @param requiredBuilding A `BLDG` section self-reference, `-1` for none.
 * @param requiredGovernment A `GOVT` section index, `-1` for none.
 * @param requiredAdvance A `TECH` section index, `-1` for none.
 */
data class BldgRequirements(
    val requiredBuilding: Int,
    val requiredGovernment: Int,
    val requiredAdvance: Int,
)
