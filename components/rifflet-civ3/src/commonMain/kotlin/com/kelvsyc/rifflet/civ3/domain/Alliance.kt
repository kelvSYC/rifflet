package com.kelvsyc.rifflet.civ3.domain

/**
 * One `GameLockedAlliance` alliance's identity, mutable — the domain-layer counterpart to one
 * position in [com.kelvsyc.rifflet.civ3.GameLockedAlliance.allianceNames], minus its war-relation
 * data (see [AllianceRelations]). [com.kelvsyc.rifflet.civ3.AllianceSlot.NONE]'s [name] is
 * conventionally blank, per the wire type's own KDoc — the editor exposes no field to rename it.
 */
data class Alliance(
    var name: String,
)
