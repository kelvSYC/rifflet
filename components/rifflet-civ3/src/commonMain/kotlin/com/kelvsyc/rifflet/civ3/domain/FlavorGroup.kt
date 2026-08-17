package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.FlavorSlot

/**
 * One `FLAV` flavor group, mutable — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.FlavGroupEntry], with each flavor's identity ([flavors]) separated
 * from the group's bulk relationship data ([relations]).
 */
data class FlavorGroup(
    var flavors: MutableMap<FlavorSlot, Flavor> = mutableMapOf(),
    var relations: FlavorRelations = FlavorRelations(),
)
