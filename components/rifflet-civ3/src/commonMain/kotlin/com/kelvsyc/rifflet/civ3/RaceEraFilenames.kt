package com.kelvsyc.rifflet.civ3

/**
 * One entry of `RACE`'s embedded per-era filename array: the forward and reverse animation
 * filenames used for a civilization at a given game era. Sized from the already-parsed `ERAS`
 * section's entry count, not from any field within the `RACE` record itself.
 */
data class RaceEraFilenames(
    val forwardFilename: String,
    val reverseFilename: String,
)
