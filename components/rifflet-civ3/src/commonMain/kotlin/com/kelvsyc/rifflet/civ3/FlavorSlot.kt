package com.kelvsyc.rifflet.civ3

/**
 * A named identity for one of the fixed-position `FLAV` section slots within a single flavor
 * group. The Rules Editor's Bonuses group box labels these checkboxes literally "Flavor1" through
 * "Flavor7" — generic numbered categories, not richer semantic names — and real files always have
 * exactly one flavor group with exactly 7 flavors, though the file format itself declares both
 * counts dynamically (see [FlavGroupEntry]/[FlavorEntry]'s own KDoc).
 */
enum class FlavorSlot {
    FLAVOR_1, FLAVOR_2, FLAVOR_3, FLAVOR_4, FLAVOR_5, FLAVOR_6, FLAVOR_7,
}

/**
 * This slot's `FLAV` wire index — stable in every era, unlike [TerrainSlot.index]/
 * [WorkerJobSlot.index], since `FLAV`'s cardinality and ordering never vary by [Civ3FormatEra].
 */
val FlavorSlot.index: Int get() = ordinal
