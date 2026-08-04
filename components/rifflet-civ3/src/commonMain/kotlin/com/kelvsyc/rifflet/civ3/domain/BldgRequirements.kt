package com.kelvsyc.rifflet.civ3.domain

/**
 * A building's prerequisites — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.BldgRequirements].
 *
 * @param requiredBuilding A prerequisite building, per the "Required" groupbox's "Improvement/
 *   Wonder" dropdown.
 * @param requiredGovernment A prerequisite government, per the "Required" groupbox's "Government"
 *   dropdown.
 * @param requiredAdvance A prerequisite advance, per the "Required" groupbox's "Advance" dropdown.
 */
data class BldgRequirements(
    var requiredBuilding: Building? = null,
    var requiredGovernment: Government? = null,
    var requiredAdvance: Tech? = null,
)
