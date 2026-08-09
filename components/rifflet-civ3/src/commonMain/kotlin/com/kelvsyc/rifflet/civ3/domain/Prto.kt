package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.PrtoDomain
import okio.ByteString

/**
 * A unit type/prototype definition, mutable and cross-referenced by real object references — the
 * domain-layer counterpart to [com.kelvsyc.rifflet.civ3.PrtoEntry].
 *
 * A `data class`, like [Tech]/[com.kelvsyc.rifflet.civ3.domain.Race] and unlike the plain-class
 * [Government]: [upgradeTo][PrtoUnitStatistics.upgradeTo], [enslaveResults], and
 * [stealthTargetUnitTypes] are self-references, but `toDomain()` guarantees no cyclic graph is
 * ever constructed for [PrtoUnitStatistics.upgradeTo] (the only one of the three where a cycle
 * would even be meaningful — see that field's own KDoc), so there's no residual circular-reference
 * risk to protect against.
 *
 * There is no `otherStrategy` field: the wire format's "paired duplicate entry" mechanic (where a
 * unit's second simultaneous AI Strategy lives on a separate physical `PrtoEntry`, merged by the
 * real Units editor into one displayed unit) is fully absorbed into [aiStrategies]'s ability to
 * hold more than one bit — something no single wire `PrtoEntry` ever does in practice, but which
 * every domain `Prto` can. See `PrtoEntryMapping.kt`'s `toDomain()`/`toWire()` for the merge/split
 * logic this enables.
 *
 * There is also no `flags2`, `flags4`, `standardOrders`, `specialActions`, `workerActions`, or
 * `airMissions` field: every real Units-editor action these wire fields represent is exposed here
 * as one plain, era-independent `Boolean` property instead (see the trailing constructor
 * parameters below) — `toDomain(era, ...)`/`toWire(era, ...)` handle reading/writing whichever
 * wire storage location (the VANILLA-era packed `flags2`, or the later separate fields) matches a
 * given file's era. `flags4` (a fully game-computed "actions mix" cache) has no domain
 * representation at all — `toWire()` recomputes it from the final action values.
 *
 * @param name This unit type's name.
 * @param civilopediaEntry Encyclopedia/Civilopedia entry text.
 * @param iconIndex This unit type's icon index.
 * @param type This unit type's domain (Land/Sea/Air), per the Units editor's "Class" control.
 * @param unitStatistics This unit's numeric stats, upgrade path, and combat-support/creation
 *   flags. See [PrtoUnitStatistics].
 * @param required A prerequisite advance, per the Units editor's own dropdown.
 * @param requiredResources This unit's 3 required natural resources. Unlike a
 *   [com.kelvsyc.rifflet.civ3.domain.BldgRequiredResources]-style group class, this is a
 *   fixed-size list (like [com.kelvsyc.rifflet.civ3.domain.Race.freeTechs]) rather than 3 flat
 *   fields or a `Set`: the Units editor tolerates requiring the same resource more than once
 *   across its 3 dropdowns, which a `Set` would silently collapse. Use [requiredResourcesOf] to
 *   build a canonical, front-packed list by hand.
 * @param abilities The Units editor's Abilities checkboxes. See `PrtoFlags.kt` for named, settable
 *   accessors.
 * @param aiStrategies The Units editor's AI Strategy checkboxes. See `PrtoFlags.kt` for named,
 *   settable accessors. Unlike the wire type's field of the same name, this can genuinely have
 *   more than one bit set — see this class's own KDoc.
 * @param availableTo The civilizations this unit type is available to.
 * @param enslaveResults The unit type this unit's Enslave Special Action creates on a successful
 *   battle, if any — e.g. a Man-O-War's Enslave ability creates more Man-O-War units. No
 *   acyclicity guard applies to this field, unlike [PrtoUnitStatistics.upgradeTo].
 * @param ignoreMovementCost The terrain types this unit type ignores movement cost for.
 * @param stealthTargetUnitTypes The unit types this unit's Stealth Attack ability cannot target —
 *   an exclusion list, not an allow-list, despite the name.
 * @param unknown 16 bytes with zero documented behavior; preserved raw, not validated.
 * @param unknown2 4 bytes with zero documented behavior; preserved raw, not validated.
 * @param unknown3 8 bytes with zero documented behavior; preserved raw, not validated.
 * @param unknown4 4 bytes with zero documented behavior; preserved raw, not validated.
 */
data class Prto(
    var name: String,
    var civilopediaEntry: String,
    var iconIndex: Int,
    var type: PrtoDomain,
    var unitStatistics: PrtoUnitStatistics = PrtoUnitStatistics(),
    var required: Tech? = null,
    var requiredResources: MutableList<Resource?> = MutableList(3) { null },
    var abilities: Int = 0,
    var aiStrategies: Int = 0,
    var availableTo: MutableSet<Race> = mutableSetOf(),
    var enslaveResults: Prto? = null,
    var ignoreMovementCost: MutableSet<Terrain> = mutableSetOf(),
    var stealthTargetUnitTypes: MutableSet<Prto> = mutableSetOf(),
    var unknown: ByteString = ByteString.of(*ByteArray(16)),
    var unknown2: ByteString = ByteString.of(0, 0, 0, 0),
    var unknown3: ByteString = ByteString.of(*ByteArray(8)),
    var unknown4: ByteString = ByteString.of(0, 0, 0, 0),
    // 32 actions with a real VANILLA-era storage location, era-resolved by toDomain()/toWire():
    var skipTurn: Boolean = false,
    var wait: Boolean = false,
    var fortify: Boolean = false,
    var disband: Boolean = false,
    var goTo: Boolean = false,
    var load: Boolean = false,
    var unload: Boolean = false,
    var airlift: Boolean = false,
    var pillage: Boolean = false,
    var bombard: Boolean = false,
    var airdrop: Boolean = false,
    var buildArmy: Boolean = false,
    var finishImprovements: Boolean = false,
    var upgradeUnit: Boolean = false,
    var buildColony: Boolean = false,
    var buildCity: Boolean = false,
    var buildRoad: Boolean = false,
    var buildRailroad: Boolean = false,
    var buildFort: Boolean = false,
    var buildMine: Boolean = false,
    var irrigate: Boolean = false,
    var clearForest: Boolean = false,
    var clearJungle: Boolean = false,
    var plantForest: Boolean = false,
    var clearPollution: Boolean = false,
    var automate: Boolean = false,
    var joinCity: Boolean = false,
    var bombing: Boolean = false,
    var recon: Boolean = false,
    var interception: Boolean = false,
    var rebase: Boolean = false,
    var precisionBombing: Boolean = false,
    // 11 actions with no VANILLA-era storage location at all (always read/written directly,
    // silently omitted — left at 0 — when the target era is VANILLA):
    var explore: Boolean = false,
    var sentry: Boolean = false,
    var capture: Boolean = false,
    var stealthAttack: Boolean = false,
    var enslave: Boolean = false,
    var sacrifice: Boolean = false,
    var startsScienceAge: Boolean = false,
    var buildAirfield: Boolean = false,
    var buildRadarTower: Boolean = false,
    var buildOutpost: Boolean = false,
    var buildBarricade: Boolean = false,
) {
    init {
        require(requiredResources.size == 3) {
            "Prto.requiredResources must be exactly 3 elements, was ${requiredResources.size}"
        }
    }
}

/**
 * Builds a canonical [Prto.requiredResources]-shaped list from 0–3 actual [resources], front-packed
 * with any remaining slots `null`. A construction-time convenience only — the `toWire()` mapping
 * function never performs this normalization itself, to avoid reordering data read from a real
 * file.
 */
fun requiredResourcesOf(vararg resources: Resource): MutableList<Resource?> {
    require(resources.size <= 3) { "requiredResourcesOf accepts at most 3 resources, was ${resources.size}" }
    return (resources.toList() + List(3 - resources.size) { null }).toMutableList()
}
