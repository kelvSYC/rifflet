package com.kelvsyc.rifflet.civ3.domain

import okio.ByteString

/**
 * A spaceship part — the Rules Editor locks this to a materially different field set than a
 * plain [Improvement] (no combat values, no happiness effect, no unit production, exactly 1
 * required to build, never obsoleted), confirmed `ERROR`-level always-true for every real file.
 * Some fields (maintenance/culture/production/pollution, [Building.improvements],
 * [Building.otherCharacteristics] beyond agricultural/seafaring) are only *conventionally* zero
 * (`WARNING`-level — 2 known real PTW exceptions), so they stay real, mutable fields here rather
 * than being hard-coded.
 *
 * @param partIndex Which spaceship part this building produces, per
 *   `RuleEntry.spaceshipPartQuantities`'s index — always a real value here (the wire format's
 *   `-1` "doesn't produce a spaceship part" sentinel is exactly what makes an entry NOT a
 *   `SpaceshipPart` in the first place).
 */
data class SpaceshipPart(
    override var description: String,
    override var name: String,
    override var civilopediaEntry: String,
    override var cost: Int,
    override var culture: Int,
    override var maintenanceCost: Int,
    override var pollution: Int,
    override var production: Int,
    var partIndex: Int,
    override var requirements: BldgRequirements = BldgRequirements(),
    override var requiredResources: BldgRequiredResources = BldgRequiredResources(),
    override var improvements: Int = 0,
    override var otherCharacteristics: Int = 0,
    override var flavors: Int = 0,
    override var unknown: ByteString = ByteString.of(0, 0, 0, 0),
) : Building
