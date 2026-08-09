package com.kelvsyc.rifflet.civ3.domain

/**
 * A tile's river data: which sides a river runs along, and which sides a unit can cross the river
 * on — the domain-layer counterpart to [com.kelvsyc.rifflet.civ3.TileEntry.riverConnections]/
 * [com.kelvsyc.rifflet.civ3.TileEntry.riverCrossingFlags].
 *
 * @param north Whether a river runs along this tile's north side.
 * @param west Whether a river runs along this tile's west side.
 * @param east Whether a river runs along this tile's east side.
 * @param south Whether a river runs along this tile's south side.
 * @param crossingN Whether a unit can cross the river on this tile's north side.
 * @param crossingNe Whether a unit can cross the river on this tile's northeast side.
 * @param crossingE Whether a unit can cross the river on this tile's east side.
 * @param crossingSe Whether a unit can cross the river on this tile's southeast side.
 * @param crossingS Whether a unit can cross the river on this tile's south side.
 * @param crossingSw Whether a unit can cross the river on this tile's southwest side.
 * @param crossingW Whether a unit can cross the river on this tile's west side.
 * @param crossingNw Whether a unit can cross the river on this tile's northwest side.
 */
data class TileRivers(
    var north: Boolean = false,
    var west: Boolean = false,
    var east: Boolean = false,
    var south: Boolean = false,
    var crossingN: Boolean = false,
    var crossingNe: Boolean = false,
    var crossingE: Boolean = false,
    var crossingSe: Boolean = false,
    var crossingS: Boolean = false,
    var crossingSw: Boolean = false,
    var crossingW: Boolean = false,
    var crossingNw: Boolean = false,
)
