package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `TECH` section: a civilization advance (technology).
 *
 * @param prerequisite1 Likely a self-referential `TECH` section index (naming convention only);
 *   not confirmed by either primary source. Same treatment applies to [prerequisite2],
 *   [prerequisite3], [prerequisite4].
 * @param flags 4 bytes of packed boolean flags. Apolyton's "Civilization III BIX/BIQ file
 *   format" thread — the fuller, later-era documentation — lists exactly 19 named bits across
 *   the first 3 of the 4 bytes; `QueryCiv3` claims 23, but this codebase could not confirm the
 *   extra 4 against either primary source, so only the 19 confirmed bits are exposed. See
 *   [TechEntry.enablesDiplomats] and its sibling accessors in `TechEntryFlags.kt`.
 * @param flavors Opaque; no claimed relationship to the `FLAV` section despite the shared name —
 *   neither primary source documents one. Same treatment as `RaceEntry.flavors`.
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
