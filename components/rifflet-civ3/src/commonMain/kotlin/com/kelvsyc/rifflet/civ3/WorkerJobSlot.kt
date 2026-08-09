package com.kelvsyc.rifflet.civ3

/**
 * A named identity for one of the fixed-position `TFRM` section slots. The Rules Editor's Worker
 * Jobs page only ever offers a Rename control — no Add, no Delete — so every real `TFRM` section
 * has exactly one entry per slot valid for its era: 9 slots ([Civ3FormatEra.VANILLA]), 12
 * ([Civ3FormatEra.PTW], which adds [AIRFIELD]/[RADAR_TOWER]/[OUTPOST]), or 13
 * ([Civ3FormatEra.CONQUESTS], which further adds [BARRICADE]).
 */
enum class WorkerJobSlot {
    MINE, IRRIGATION, FORTRESS, ROAD, RAILROAD, PLANT_FOREST, CLEAR_FOREST,
    CLEAR_WETLANDS, CLEAR_DAMAGE, AIRFIELD, RADAR_TOWER, OUTPOST, BARRICADE,
}

/**
 * This slot's `TFRM` wire index for [era], or `null` if this slot doesn't exist in that era.
 * Indices 0-8 are stable across every era. [WorkerJobSlot.AIRFIELD]/[WorkerJobSlot.RADAR_TOWER]/
 * [WorkerJobSlot.OUTPOST] (9-11) require at least [Civ3FormatEra.PTW].
 * [WorkerJobSlot.BARRICADE] (12) requires [Civ3FormatEra.CONQUESTS]. Unlike
 * [com.kelvsyc.rifflet.civ3.TerrainSlot.index], growth here is pure append — no slot ever shifts
 * position between eras.
 */
fun WorkerJobSlot.index(era: Civ3FormatEra): Int? = when (this) {
    WorkerJobSlot.MINE -> 0
    WorkerJobSlot.IRRIGATION -> 1
    WorkerJobSlot.FORTRESS -> 2
    WorkerJobSlot.ROAD -> 3
    WorkerJobSlot.RAILROAD -> 4
    WorkerJobSlot.PLANT_FOREST -> 5
    WorkerJobSlot.CLEAR_FOREST -> 6
    WorkerJobSlot.CLEAR_WETLANDS -> 7
    WorkerJobSlot.CLEAR_DAMAGE -> 8
    WorkerJobSlot.AIRFIELD -> if (era != Civ3FormatEra.VANILLA) 9 else null
    WorkerJobSlot.RADAR_TOWER -> if (era != Civ3FormatEra.VANILLA) 10 else null
    WorkerJobSlot.OUTPOST -> if (era != Civ3FormatEra.VANILLA) 11 else null
    WorkerJobSlot.BARRICADE -> if (era == Civ3FormatEra.CONQUESTS) 12 else null
}
