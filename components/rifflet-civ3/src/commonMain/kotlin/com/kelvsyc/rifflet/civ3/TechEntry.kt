package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `TECH` section: a civilization advance (technology).
 *
 * @param prerequisite1 A self-referential `TECH` section index, per the Conquests Rules Editor
 *   (not merely a naming-based inference). Same treatment applies to [prerequisite2],
 *   [prerequisite3], [prerequisite4].
 * @param flags 4 bytes of packed boolean flags, all 23 bits named — see [TechEntry.enablesDiplomats]
 *   and its sibling accessors in `TechEntryFlags.kt` for the full breakdown and sourcing.
 * @param flavors Bitmask membership in the `FLAV` section's 7 flavor slots: bit *n* means this
 *   advance belongs to Flavor(*n*+1). The same scheme applies to `BldgEntry.flavors` and
 *   `RaceEntry.flavors` (not `RaceEntry.bonuses`, despite that field's adjacent "Flavor1"
 *   through "Flavor7" checkboxes in the Rules Editor).
 * @param unknown 4 bytes with disputed presence: a separate reverse-engineered reference
 *   implementation includes this trailing field but existing reverse-engineering documentation
 *   omits it entirely — possibly a BIC/BIX/BIQ format-variant difference, deferred to the
 *   project's real-world validation plan. Defaults to 4 zero bytes
 *   when absent from a given file's declared item length, via the same length-aware parsing
 *   already used by `UnitEntryParser`.
 */
data class TechEntry(
    val name: String,
    val civilopediaEntry: String,
    val cost: Int,
    val era: Int,
    val advanceIcon: Int,
    val x: Int,
    val y: Int,
    val prerequisite1: Int,
    val prerequisite2: Int,
    val prerequisite3: Int,
    val prerequisite4: Int,
    val flags: Int,
    val flavors: Int,
    val unknown: ByteString,
)
