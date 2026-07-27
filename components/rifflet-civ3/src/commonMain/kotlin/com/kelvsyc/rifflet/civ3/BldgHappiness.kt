package com.kelvsyc.rifflet.civ3

/**
 * The building's happiness effect.
 *
 * Corresponds to the Conquests Rules Editor's `Improvements and Wonders` tab's "Happy/Unhappy
 * Faces" groupbox, in its entirety. Unconditionally present — unlike [BldgUnitsProduced], none of
 * these fields are read defensively.
 *
 * @param contentFacesAllCities The "Happy (all)" field: happy faces granted in every city in the
 *   civilization.
 * @param contentFaces The "Happy" field: happy faces granted in the city containing this building.
 * @param unhappyFacesAllCities The "Unhappy (all)" field: unhappy faces caused in every city in
 *   the civilization.
 * @param unhappyFaces The "Unhappy" field: unhappy faces caused in the city containing this
 *   building.
 */
data class BldgHappiness(
    val contentFacesAllCities: Int,
    val contentFaces: Int,
    val unhappyFacesAllCities: Int,
    val unhappyFaces: Int,
)
