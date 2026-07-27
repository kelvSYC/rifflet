package com.kelvsyc.rifflet.civ3

/**
 * The scenario's 3 city size level names and the population thresholds between them.
 *
 * Corresponds to the Conquests Rules Editor's `General Settings` tab's "City Size Levels"
 * groupbox, in its entirety. Level 3 (the top tier) has no maximum — a city stays at Level 3
 * indefinitely once it reaches it.
 *
 * @param citySizeLevel1Name The "Level 1" name field (e.g. "Town").
 * @param citySizeLevel2Name The "Level 2" name field (e.g. "City").
 * @param citySizeLevel3Name The "Level 3" name field (e.g. "Metropolis").
 * @param maximumLevel1CitySize The "Level 1" row's "Maximum" field: the population at which a
 *   city is promoted from Level 1 to Level 2.
 * @param maximumLevel2CitySize The "Level 2" row's "Maximum" field: the population at which a
 *   city is promoted from Level 2 to Level 3.
 */
data class RuleCitySizeLevels(
    val citySizeLevel1Name: String,
    val citySizeLevel2Name: String,
    val citySizeLevel3Name: String,
    val maximumLevel1CitySize: Int,
    val maximumLevel2CitySize: Int,
)
