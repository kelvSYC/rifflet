package com.kelvsyc.rifflet.civ3.domain

import okio.ByteString

/**
 * A civilization advance (technology), mutable and cross-referenced by real object references —
 * the domain-layer counterpart to [com.kelvsyc.rifflet.civ3.TechEntry].
 *
 * A `data class`, like [Race] and unlike the plain-class [Government]: [prerequisite1]..
 * [prerequisite4] are self-references, but `toDomain()` guarantees it never constructs a cyclic
 * `Tech` graph (see that function's own KDoc), so there's no circular-reference risk to protect
 * against the way [Government.relationships] needed to.
 *
 * @param name This advance's name.
 * @param civilopediaEntry Encyclopedia/Civilopedia entry text.
 * @param cost This advance's research cost, in beakers.
 * @param era This advance's era.
 * @param advanceIcon This advance's icon index.
 * @param x This advance's X position in the Civilopedia tech-tree display.
 * @param y This advance's Y position in the Civilopedia tech-tree display.
 * @param prerequisite1 A prerequisite advance, per the Rules Editor's own dropdowns. Same
 *   treatment applies to [prerequisite2], [prerequisite3], and [prerequisite4].
 * @param flags Packed boolean flags. See `TechFlags.kt` for named, settable accessors.
 * @param flavors Bitmask membership in the `FLAV` section's 7 flavor slots. See `TechFlags.kt`
 *   for named, settable accessors.
 * @param unknown 4 bytes with disputed presence — preserved raw, not validated.
 */
data class Tech(
    var name: String,
    var civilopediaEntry: String,
    var cost: Int,
    var era: Int,
    var advanceIcon: Int,
    var x: Int,
    var y: Int,
    var prerequisite1: Tech? = null,
    var prerequisite2: Tech? = null,
    var prerequisite3: Tech? = null,
    var prerequisite4: Tech? = null,
    var flags: Int = 0,
    var flavors: Int = 0,
    var unknown: ByteString = ByteString.of(0, 0, 0, 0),
)
