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
data class Civ3Header(val major: Int, val minor: Int, val description: String, val title: String)
