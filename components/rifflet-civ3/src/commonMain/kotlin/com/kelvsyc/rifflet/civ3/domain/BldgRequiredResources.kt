package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.GoodEntry

/**
 * A building's required natural resources — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.BldgRequiredResources].
 *
 * @param requiredResource1 The first required resource. References the wire `GoodEntry` —
 *   `GOOD` doesn't have its own domain type yet.
 * @param requiredResource2 The second required resource. References the wire `GoodEntry` —
 *   `GOOD` doesn't have its own domain type yet.
 */
data class BldgRequiredResources(
    var requiredResource1: GoodEntry? = null,
    var requiredResource2: GoodEntry? = null,
)
