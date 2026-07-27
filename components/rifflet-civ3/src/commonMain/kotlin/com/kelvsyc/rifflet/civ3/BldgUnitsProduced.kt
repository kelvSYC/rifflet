package com.kelvsyc.rifflet.civ3

/**
 * The unit the building produces each turn, if any.
 *
 * Corresponds to the Conquests Rules Editor's `Improvements and Wonders` tab's "Units Produced"
 * groupbox, in its entirety — present ([BldgEntry.unitsProduced] non-`null`) only in
 * [Civ3FormatEra.CONQUESTS] files: real [Civ3FormatEra.VANILLA]/[Civ3FormatEra.PTW] `BLDG` items
 * are an exact 252-byte prefix of the 268-byte [Civ3FormatEra.CONQUESTS] shape, confirmed by a
 * byte-for-byte diff of real files.
 *
 * @param unitProduced A `PRTO` section index, `-1` for none.
 * @param unitFrequency How many turns between each production of [unitProduced].
 */
data class BldgUnitsProduced(
    val unitProduced: Int,
    val unitFrequency: Int,
)
