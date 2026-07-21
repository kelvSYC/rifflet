package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `TECH` section: a civilization advance (technology).
 *
 * @param prerequisite1 A self-referential `TECH` section index, per the Conquests Rules Editor
 *   (not merely a naming-based inference). Same treatment applies to [prerequisite2],
 *   [prerequisite3], [prerequisite4].
 * @param flags 4 bytes of packed boolean flags. The fuller, later-era existing
 *   reverse-engineering documentation of the BIX/BIQ format lists exactly 19 named bits across
 *   the first 3 of the 4 bytes; a separate reverse-engineered reference implementation claims
 *   23. The Conquests Rules Editor corroborates that count of 23 and names the 4 bits beyond the
 *   19 documented ones — "Permits Sacrifices", "Cannot be Traded", "Bonus Tech", and "Reveal
 *   Map" — but not their bit positions within the 4-byte field, so only the 19 documented bits
 *   are exposed as named accessors in `TechEntryFlags.kt` pending byte-level validation of the
 *   remaining 4.
 * @param flavors Likely bitmask membership in the `FLAV` section's 7 flavor slots, not opaque:
 *   the Conquests Rules Editor's flavor-relationship editor treats advances, buildings (see
 *   `BldgEntry.flavors`), and civilizations (see `RaceEntry.bonuses`'s Flavor1..7 bits) as
 *   sharing the same Flavor1..7 concept. Exact bit-to-slot mapping unconfirmed.
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
