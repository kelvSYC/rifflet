package com.kelvsyc.rifflet.civ3

/**
 * A named identity for one of the fixed-position `WSIZ` section slots. The Rules Editor's World
 * Sizes tab only ever offers a Rename control — no Add, no Delete — so every real `WSIZ` section
 * has exactly these 5 entries, in this order, in every era.
 */
enum class WorldSizeSlot {
    TINY, SMALL, STANDARD, LARGE, HUGE,
}

/**
 * This slot's `WSIZ` wire index — stable in every era, unlike [TerrainSlot.index]/
 * [WorkerJobSlot.index], since `WSIZ`'s cardinality and ordering never vary by [Civ3FormatEra].
 */
val WorldSizeSlot.index: Int get() = ordinal
