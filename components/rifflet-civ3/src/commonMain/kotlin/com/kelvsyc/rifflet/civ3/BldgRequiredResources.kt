package com.kelvsyc.rifflet.civ3

/**
 * The building's required natural resources.
 *
 * Corresponds to the Conquests Rules Editor's `Improvements and Wonders` tab's "Required
 * Resources" groupbox, in its entirety. Unconditionally present.
 *
 * @param requiredResource1 A `GOOD` section index for the first required resource, `-1` for none.
 * @param requiredResource2 A `GOOD` section index for the second required resource, `-1` for none.
 */
data class BldgRequiredResources(
    val requiredResource1: Int,
    val requiredResource2: Int,
)
