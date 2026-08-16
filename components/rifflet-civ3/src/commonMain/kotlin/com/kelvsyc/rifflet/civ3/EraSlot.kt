package com.kelvsyc.rifflet.civ3

/**
 * A named identity for one of the fixed-position `ERAS` section slots. The Rules Editor's Eras
 * tab only ever offers a Rename control — no Add, no Delete — so every real `ERAS` section has
 * exactly these 4 entries, in this order, in every era. Not to be confused with [Civ3FormatEra]
 * (the Civ III *file format* era) — this is the game's in-scenario historical era.
 */
enum class EraSlot {
    ANCIENT_TIMES, MIDDLE_AGES, INDUSTRIAL_AGES, MODERN_TIMES,
}

/**
 * This slot's `ERAS` wire index — stable in every era, unlike [TerrainSlot.index]/
 * [WorkerJobSlot.index], since `ERAS`'s cardinality and ordering never vary by
 * [Civ3FormatEra].
 */
val EraSlot.index: Int get() = ordinal
