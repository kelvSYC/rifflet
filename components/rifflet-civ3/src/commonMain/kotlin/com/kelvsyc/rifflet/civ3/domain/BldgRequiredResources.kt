package com.kelvsyc.rifflet.civ3.domain

/**
 * A building's required natural resources — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.BldgRequiredResources].
 *
 * @param requiredResource1 The first required resource.
 * @param requiredResource2 The second required resource.
 */
data class BldgRequiredResources(
    var requiredResource1: Resource? = null,
    var requiredResource2: Resource? = null,
)
