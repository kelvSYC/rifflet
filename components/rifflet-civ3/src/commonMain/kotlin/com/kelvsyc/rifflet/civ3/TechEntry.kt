package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `TECH` section: a civilization advance (technology).
 *
 * @param prerequisite1 Likely a self-referential `TECH` section index (naming convention only);
 *   not confirmed by either cross-referenced source. Same treatment applies to [prerequisite2],
 *   [prerequisite3], [prerequisite4].
 * @param flags 4 bytes of packed boolean flags (23 named bits across 3 of the 4 bytes: e.g.
 *   diplomats, conscription, world-map reveal), kept opaque rather than decomposed into
 *   individual named booleans — see `QueryCiv3`'s `Tech.cs` for the full bit-accessor
 *   breakdown if this is ever revisited. Same treatment as `RaceEntry.bonuses`/
 *   `EspnEntry.missionFlags`/`WmapEntry.flags`.
 * @param flavors Opaque; no claimed relationship to the `FLAV` section despite the shared name —
 *   neither cross-referenced source documents one. Same treatment as `RaceEntry.flavors`.
 * @param unknown 4 bytes with disputed presence: `QueryCiv3`'s struct includes this trailing
 *   field but Apolyton's documentation omits it entirely — possibly a BIC/BIX/BIQ format-variant
 *   difference, deferred to the project's real-world validation plan. Defaults to 4 zero bytes
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
