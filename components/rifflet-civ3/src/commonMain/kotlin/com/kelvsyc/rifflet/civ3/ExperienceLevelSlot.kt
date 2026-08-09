package com.kelvsyc.rifflet.civ3

/**
 * A named identity for one of the fixed-position `EXPR` section slots. The Rules Editor's
 * Experience Levels tab only ever offers a Rename control — no Add, no Delete — so every real
 * `EXPR` section has exactly these 4 entries, in this order, in every era.
 */
enum class ExperienceLevelSlot {
    CONSCRIPT, REGULAR, VETERAN, ELITE,
}

/**
 * This slot's `EXPR` wire index — stable in every era, unlike [TerrainSlot.index]/
 * [WorkerJobSlot.index], since `EXPR`'s cardinality and ordering never vary by
 * [Civ3FormatEra].
 */
val ExperienceLevelSlot.index: Int get() = ordinal
