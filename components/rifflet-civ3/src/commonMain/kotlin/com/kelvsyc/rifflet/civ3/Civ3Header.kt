package com.kelvsyc.rifflet.civ3

/**
 * The parsed `VER#` section: a Civ3 file's format version and descriptive text.
 *
 * @param major The major version number (e.g. 2-4 for original Civ3, 11 for PTW, 12 for
 *   Conquests).
 * @param minor The minor version number.
 * @param description The scenario/save description text.
 * @param title The scenario/save title text.
 */
data class Civ3Header(val major: Int, val minor: Int, val description: String, val title: String) {
    /**
     * The format era for [major], derived from the closed, final mapping documented on
     * [Civ3FormatEra]. Evaluated eagerly at construction — a [major] outside the recognized set
     * (2-4, 11, or 12) throws immediately, matching this codebase's established
     * invariant-validation style for domain types (e.g. `RaceEntry`, `TerrEntry`).
     */
    val formatEra: Civ3FormatEra = when (major) {
        in 2..4 -> Civ3FormatEra.VANILLA
        11 -> Civ3FormatEra.PTW
        12 -> Civ3FormatEra.CONQUESTS
        else -> throw IllegalArgumentException(
            "Civ3Header.major must be a recognized Civ3 format version (2-4, 11, or 12), was $major",
        )
    }
}
