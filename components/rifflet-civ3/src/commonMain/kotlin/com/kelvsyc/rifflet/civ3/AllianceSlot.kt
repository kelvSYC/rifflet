package com.kelvsyc.rifflet.civ3

/**
 * A named identity for one of the fixed-position `GameLockedAlliance` alliance-name slots. The
 * Rules Editor's Locked Alliance tab labels four of these boxes literally "Alliance 1" through
 * "Alliance 4" (renamable) — [NONE] is the tab's "No Alliances:" pool, wire index 0, not a
 * renamable identity of its own (no name field, no "At war with:" list in the editor), included
 * here only so every wire index has a corresponding slot.
 */
enum class AllianceSlot {
    NONE, ALLIANCE_1, ALLIANCE_2, ALLIANCE_3, ALLIANCE_4,
}

/**
 * This slot's `GameLockedAlliance` wire index — stable in every era, matching [FlavorSlot.index]'s
 * own reasoning.
 */
val AllianceSlot.index: Int get() = ordinal
