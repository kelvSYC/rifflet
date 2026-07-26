package com.kelvsyc.rifflet.civ3

import com.kelvsyc.kotlin.core.traits.integral.BitCollection
import com.kelvsyc.kotlin.core.traits.integral.extensionBitFlag
import com.kelvsyc.kotlin.core.traits.integral.int
import okio.ByteString

private fun ByteString.toIntLe(offset: Int): Int =
    (this[offset].toInt() and 0xFF) or
        ((this[offset + 1].toInt() and 0xFF) shl 8) or
        ((this[offset + 2].toInt() and 0xFF) shl 16) or
        ((this[offset + 3].toInt() and 0xFF) shl 24)

// --- Raw bit windows ---
//
// [PrtoEntry.abilities], [PrtoEntry.aiStrategies], [PrtoEntry.standardOrders],
// [PrtoEntry.specialActions], [PrtoEntry.workerActions], and [PrtoEntry.airMissions] were each
// originally windows over a larger opaque ByteString field; once every bit in each was decoded as
// a genuine Units editor checkbox, it was promoted to a real, directly-parsed Int property on
// PrtoEntry (see that class's KDoc) instead of staying a derived window. flags4 is the one
// remaining raw window: a still-undeciphered echo/cluster region. The accessors below are grouped
// by conceptual category (Abilities, Standard Orders, Special Actions, AI Strategies, Air
// Missions), not by which raw window they happen to live in, so a reader looking for "everything
// AI Strategies" or "everything Abilities" doesn't have to hunt across widely separated byte
// offsets.

/**
 * The entire (4-byte) [PrtoEntry.flags4] as an Int. Most of its bits duplicate a bit found
 * elsewhere on this entry (see e.g. [sentry], [bombard], [buildRoad]'s KDocs); a handful remain
 * unexplained, and the rest are unused.
 */
val PrtoEntry.flags4Bits: Int get() = flags4.toIntLe(0)

// --- Abilities (unit-class characteristics) ---

/**
 * The Units editor's Abilities → "Wheeled" checkbox. Marks the unit as wheeled, making it subject
 * to terrain that's impassable to wheeled units.
 */
val PrtoEntry.wheeledAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 0)

/**
 * The Units editor's Abilities → "Foot Unit" checkbox. Marks the unit as on-foot infantry, as
 * distinct from mounted, wheeled, naval, or air units.
 */
val PrtoEntry.footUnitAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 1)

/**
 * The Units editor's Abilities → "Blitz" checkbox. Lets the unit attack multiple times in a
 * single turn, as long as it still has movement remaining after each attack.
 */
val PrtoEntry.blitzAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 2)

/**
 * Marks the unit as a cruise missile: a guided weapon consumed on use rather than an ordinary
 * reusable unit.
 */
val PrtoEntry.cruiseMissileAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 3)

/**
 * The Units editor's Abilities → "All Terrain As Roads" checkbox. Treats all terrain as if it had
 * a road built on it; the actual movement cost depends on the ruleset's road movement-cost
 * setting (1/3 of a movement point under standard rules).
 */
val PrtoEntry.allTerrainAsRoadsAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 4)

/**
 * The Units editor's Abilities → "Radar" checkbox. Gives the unit a fixed sight range of 2,
 * regardless of terrain.
 */
val PrtoEntry.radarAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 5)

/**
 * The Units editor's Abilities → "Amphibious" checkbox. Lets the unit attack from a ship directly
 * onto land — normally not possible.
 */
val PrtoEntry.amphibiousAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 6)

/**
 * The Units editor's Abilities → "Invisible" checkbox. Hides the unit from other civs unless they
 * have a unit with [detectInvisibleAbility] nearby.
 */
val PrtoEntry.invisibleAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 7)

/**
 * The Units editor's Abilities → "Transports Only Aircraft" checkbox. Restricts the unit's cargo
 * capacity to air-domain units only.
 */
val PrtoEntry.transportsOnlyAircraftAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 8)

/**
 * The Units editor's Abilities → "Draft" checkbox. Marks the unit as eligible for the Draft
 * mechanic (converting city population directly into military units).
 */
val PrtoEntry.draftAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 9)

/**
 * The Units editor's Abilities → "Immobile" checkbox. The unit cannot move under its own power —
 * it's positioned via Air Missions instead of ordinary move orders, or is a stationary fixture.
 */
val PrtoEntry.immobileAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 10)

/**
 * The Units editor's Abilities → "Sinks in Sea" checkbox. The unit has a chance of being lost if
 * it ends a turn on Sea terrain.
 */
val PrtoEntry.sinksInSeaAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 11)

/**
 * The Units editor's Abilities → "Sinks in Ocean" checkbox. The unit has a chance of being lost
 * if it ends a turn on Ocean terrain.
 */
val PrtoEntry.sinksInOceanAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 12)

/**
 * The ability backing the Units editor's "Flag Unit" checkbox in the "Other Characteristics"-style
 * ability list. Distinct from [flagUnitStrategy] (AI Strategies → Land "Flag Unit").
 */
val PrtoEntry.flagUnitAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 13)

/**
 * The Units editor's Abilities → "Transports Only Foot Units" checkbox. Restricts the unit's
 * cargo capacity to on-foot land units only.
 */
val PrtoEntry.transportsOnlyFootUnitsAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 14)

/**
 * The Units editor's Abilities → "Starts Golden Age" checkbox. A victorious battle by this unit
 * starts a Golden Age for its civ (if it doesn't already have one); the unit can't be upgraded
 * until that Golden Age ends.
 */
val PrtoEntry.startsGoldenAgeAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 15)

/**
 * Marks the unit as a nuclear weapon, triggering nuclear-attack rules (diplomatic fallout,
 * radiation/pollution on impact).
 */
val PrtoEntry.nuclearWeaponAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 16)

/**
 * The Units editor's Abilities → "Hidden Nationality" checkbox. Conceals the owning civ's identity
 * when this unit attacks, so the attack doesn't trigger a war declaration against the true owner.
 */
val PrtoEntry.hiddenNationalityAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 17)

/**
 * Marks the unit as the Army unit type, matching [RuleEntry.buildArmyUnit]'s target: a unit that
 * can absorb several other units into a single stack fighting as one. [armyStrategy] (bit 4 of
 * [aiStrategies]) is a distinct, real AI Strategies checkbox that happens to share this same
 * population — not a redundant echo of this ability.
 */
val PrtoEntry.armyAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 18)

/**
 * The Units editor's Abilities → "Leader" checkbox. Marks the unit as the Leader unit type, used
 * to rush-produce Wonders or to form an [armyAbility] unit. Distinct from [armyAbility] itself.
 */
val PrtoEntry.leaderAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 19)

/**
 * Lets the unit's bombard attack reach any target regardless of distance.
 */
val PrtoEntry.infiniteBombardRangeAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 20)

/**
 * The Units editor's Abilities → "Stealth" checkbox. Makes the unit harder to detect and
 * intercept. Distinct from [stealthAttack] (a Special Action).
 */
val PrtoEntry.stealthAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 21)

/**
 * The Units editor's Abilities → "Detect Invisible" checkbox. Lets the unit see units that would
 * otherwise be hidden by [invisibleAbility].
 */
val PrtoEntry.detectInvisibleAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 22)

/**
 * Lets the unit (normally a missile) bombard while loaded aboard a transport.
 */
val PrtoEntry.tacticalMissileAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 23)

/**
 * The Units editor's Abilities → "Transports Only Tactical Missiles" checkbox. Restricts the
 * unit's cargo capacity to tactical-missile-type units only.
 */
val PrtoEntry.transportsOnlyTacticalMissilesAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 24)

/**
 * The Units editor's Abilities → "Ranged Attack Animation" checkbox. Purely a combat-animation
 * choice: the unit's attack is depicted as a projectile/ranged strike rather than melee contact.
 * Doesn't otherwise change combat rules — a unit with this flag still moves into the target's
 * tile after winning, if that tile is left unoccupied, same as any other attacker.
 */
val PrtoEntry.rangedAttackAnimationAbility: Boolean by
    BitCollection.int.extensionBitFlag({ abilities }, 25)

/**
 * The Units editor's Abilities → "Rotate Before Attack" checkbox. The unit visually turns to
 * present a specific facing when attacking (e.g. a ship shown making a broadside attack).
 */
val PrtoEntry.rotateBeforeAttackAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 26)

/**
 * The Units editor's Abilities → "Lethal Land Bombardment" checkbox. Lets the unit's bombard
 * attack potentially destroy land targets already reduced to 1 hit point — an ordinary bombard
 * attack can't finish them off.
 */
val PrtoEntry.lethalLandBombardmentAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 27)

/**
 * The Units editor's Abilities → "Lethal Sea Bombardment" checkbox. Lets the unit's bombard
 * attack potentially destroy naval targets already reduced to 1 hit point — an ordinary bombard
 * attack can't finish them off.
 */
val PrtoEntry.lethalSeaBombardmentAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 28)

/**
 * The Units editor's Abilities → "King" checkbox. Marks the unit as a civ's leaderhead/King
 * figure — a per-civ marker, not a narrower "royalty" concept. Places the unit at the bottom of a
 * defending stack (attacked last) and makes it unbuildable. [kingStrategy] (bit 19 of
 * [aiStrategies]) is a distinct, real AI Strategies checkbox that happens to share this same
 * population — not a redundant echo of this ability.
 */
val PrtoEntry.kingAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 29)

/**
 * The Units editor's Abilities → "Requires Escort" checkbox. The AI won't move the unit unless
 * it's escorted by a unit with [navalPowerStrategy] (AI Strategies → Sea "Naval Power").
 */
val PrtoEntry.requiresEscortAbility: Boolean by BitCollection.int.extensionBitFlag({ abilities }, 30)

// --- Standard Orders ---

/**
 * The Units editor's Standard Orders → "Skip Turn" checkbox.
 */
val PrtoEntry.skipTurn: Boolean by BitCollection.int.extensionBitFlag({ standardOrders }, 0)

/**
 * The Units editor's Standard Orders → "Wait" checkbox.
 */
val PrtoEntry.wait: Boolean by BitCollection.int.extensionBitFlag({ standardOrders }, 1)

/**
 * The Units editor's Standard Orders → "Fortify" checkbox.
 */
val PrtoEntry.fortify: Boolean by BitCollection.int.extensionBitFlag({ standardOrders }, 2)

/**
 * The Units editor's Standard Orders → "Go To" checkbox. Bit 15 of [flags4Bits] echoes this bit
 * OR'd with [rebase] — see that accessor's KDoc.
 */
val PrtoEntry.goTo: Boolean by BitCollection.int.extensionBitFlag({ standardOrders }, 4)

/**
 * The Units editor's Standard Orders → "Explore" checkbox. Distinct from [exploreStrategy] (AI
 * Strategies → Land "Explore").
 */
val PrtoEntry.explore: Boolean by BitCollection.int.extensionBitFlag({ standardOrders }, 5)

/**
 * The Units editor's Standard Orders → "Disband" checkbox.
 */
val PrtoEntry.disband: Boolean by BitCollection.int.extensionBitFlag({ standardOrders }, 3)

/**
 * The Units editor's Standard Orders → "Sentry" checkbox. Bit 0 of [flags4Bits] echoes this bit.
 */
val PrtoEntry.sentry: Boolean by BitCollection.int.extensionBitFlag({ standardOrders }, 6)

// --- Special Actions ---

/**
 * The Units editor's Special Actions → "Load" checkbox. Lets the unit load aboard a transport.
 */
val PrtoEntry.load: Boolean by BitCollection.int.extensionBitFlag({ specialActions }, 0)

/**
 * The Units editor's Special Actions → "Unload" checkbox.
 */
val PrtoEntry.unload: Boolean by BitCollection.int.extensionBitFlag({ specialActions }, 1)

/**
 * The Units editor's Special Actions → "Airlift" checkbox.
 */
val PrtoEntry.airlift: Boolean by BitCollection.int.extensionBitFlag({ specialActions }, 2)

/**
 * The Units editor's Special Actions → "Pillage" checkbox.
 */
val PrtoEntry.pillage: Boolean by BitCollection.int.extensionBitFlag({ specialActions }, 3)

/**
 * The Units editor's Special Actions → "Bombard" checkbox. Bit 1 of [flags4Bits] echoes this bit.
 */
val PrtoEntry.bombard: Boolean by BitCollection.int.extensionBitFlag({ specialActions }, 4)

/**
 * The Units editor's Special Actions → "Airdrop" checkbox.
 */
val PrtoEntry.airdrop: Boolean by BitCollection.int.extensionBitFlag({ specialActions }, 5)

/**
 * The Units editor's Special Actions → "Upgrade Unit" checkbox. Lets the unit be upgraded along
 * the tech-based upgrade chain starting at [PrtoEntry.upgradeTo] — a unit can skip multiple tiers
 * of that chain in a single upgrade if the further techs are already available.
 */
val PrtoEntry.upgradeUnit: Boolean by BitCollection.int.extensionBitFlag({ specialActions }, 8)

/**
 * The Units editor's Special Actions → "Capture" checkbox. Marks the unit as capturable: an
 * attacker that defeats it may capture it instead of destroying it (as with Settlers, Artillery,
 * and similar unit types).
 */
val PrtoEntry.capture: Boolean by BitCollection.int.extensionBitFlag({ specialActions }, 9)

/**
 * The Units editor's Special Actions → "Stealth Attack" checkbox. Lets the unit choose a specific
 * target within a defending stack to attack, rather than always fighting the stack's best
 * defender, subject to further restrictions — see [PrtoEntry.stealthTargetUnitTypes].
 */
val PrtoEntry.stealthAttack: Boolean by BitCollection.int.extensionBitFlag({ specialActions }, 16)

/**
 * The Units editor's Special Actions → "Enslave" checkbox. Gives the unit roughly a 1/3 chance of
 * creating an enslaved unit when it wins a battle.
 */
val PrtoEntry.enslave: Boolean by BitCollection.int.extensionBitFlag({ specialActions }, 18)

/**
 * The Units editor's Special Actions → "Sacrifice" checkbox. Lets an enslaved (foreign-origin)
 * unit of this type be sacrificed at a city with the required improvements, generating culture
 * for that city.
 */
val PrtoEntry.sacrifice: Boolean by BitCollection.int.extensionBitFlag({ specialActions }, 20)

/**
 * The Units editor's Special Actions → "Build Army" checkbox — true only for the dedicated
 * Leader-type unit. Lets the unit found an [armyAbility] unit.
 */
val PrtoEntry.buildArmy: Boolean by BitCollection.int.extensionBitFlag({ specialActions }, 6)

/**
 * The Units editor's Special Actions → "Finish Improvements" checkbox — true only for the
 * dedicated Leader-type unit. Lets the unit instantly complete a city's current production.
 */
val PrtoEntry.finishImprovements: Boolean by BitCollection.int.extensionBitFlag({ specialActions }, 7)

/**
 * The Units editor's Special Actions → "Starts Science Age" checkbox — true only for the
 * dedicated Leader-type unit.
 */
val PrtoEntry.startsScienceAge: Boolean by BitCollection.int.extensionBitFlag({ specialActions }, 21)

// --- Special Actions: the Worker/Engineer Actions grid ([PrtoEntry.workerActions]) ---
//
// Several of these bits are also echoed in [flags4Bits] — see the internal note below for the
// full breakdown; noted individually below only where the echo is a single, unambiguous bit.

/**
 * The Units editor's Worker/Engineer Actions → "Build Colony" checkbox.
 */
val PrtoEntry.buildColony: Boolean by BitCollection.int.extensionBitFlag({ workerActions }, 0)

/**
 * The Units editor's Worker/Engineer Actions → "Build City" checkbox — true only for the Settler
 * unit, which founds new cities. Distinct from [joinCity], which adds population to an existing
 * one.
 */
val PrtoEntry.buildCity: Boolean by BitCollection.int.extensionBitFlag({ workerActions }, 1)

/**
 * The Units editor's Worker/Engineer Actions → "Build Road" checkbox. Bits 3 and 5 of
 * [flags4Bits] both echo this bit.
 */
val PrtoEntry.buildRoad: Boolean by BitCollection.int.extensionBitFlag({ workerActions }, 2)

/**
 * The Units editor's Worker/Engineer Actions → "Build Railroad" checkbox. Bit 4 of [flags4Bits]
 * echoes this bit.
 */
val PrtoEntry.buildRailroad: Boolean by BitCollection.int.extensionBitFlag({ workerActions }, 3)

/**
 * The Units editor's Worker/Engineer Actions → "Build Fort" checkbox.
 */
val PrtoEntry.buildFort: Boolean by BitCollection.int.extensionBitFlag({ workerActions }, 4)

/**
 * The Units editor's Worker/Engineer Actions → "Build Mine" checkbox.
 */
val PrtoEntry.buildMine: Boolean by BitCollection.int.extensionBitFlag({ workerActions }, 5)

/**
 * The Units editor's Worker/Engineer Actions → "Irrigate" checkbox. Bit 6 of [flags4Bits] echoes
 * this bit.
 */
val PrtoEntry.irrigate: Boolean by BitCollection.int.extensionBitFlag({ workerActions }, 6)

/**
 * The Units editor's Worker/Engineer Actions → "Clear Forest" checkbox. Bit 7 of [flags4Bits]
 * echoes this bit; bit 2 of [flags4Bits] requires both this bit and [clearJungle] together.
 */
val PrtoEntry.clearForest: Boolean by BitCollection.int.extensionBitFlag({ workerActions }, 7)

/**
 * The Units editor's Worker/Engineer Actions → "Clear Jungle" checkbox. Bit 8 of [flags4Bits]
 * echoes this bit; see [clearForest] for bit 2's conjunction of the two.
 */
val PrtoEntry.clearJungle: Boolean by BitCollection.int.extensionBitFlag({ workerActions }, 8)

/**
 * The Units editor's Worker/Engineer Actions → "Plant Forest" checkbox.
 */
val PrtoEntry.plantForest: Boolean by BitCollection.int.extensionBitFlag({ workerActions }, 9)

/**
 * The Units editor's Worker/Engineer Actions → "Clear Pollution" checkbox. Bit 9 of [flags4Bits]
 * echoes this bit.
 */
val PrtoEntry.clearPollution: Boolean by BitCollection.int.extensionBitFlag({ workerActions }, 10)

/**
 * The Units editor's Worker/Engineer Actions → "Automate" checkbox. Bits 10, 11, and 14 of
 * [flags4Bits] all echo this bit.
 */
val PrtoEntry.automate: Boolean by BitCollection.int.extensionBitFlag({ workerActions }, 11)

/**
 * The Units editor's Worker/Engineer Actions → "Join City" checkbox — lets the unit add its
 * population into an existing city, as opposed to [buildCity]'s founding a new one.
 */
val PrtoEntry.joinCity: Boolean by BitCollection.int.extensionBitFlag({ workerActions }, 12)

/**
 * The Units editor's Worker/Engineer Actions → "Build Airfield" checkbox.
 */
val PrtoEntry.buildAirfield: Boolean by BitCollection.int.extensionBitFlag({ workerActions }, 13)

/**
 * The Units editor's Worker/Engineer Actions → "Build Radar Tower" checkbox.
 */
val PrtoEntry.buildRadarTower: Boolean by BitCollection.int.extensionBitFlag({ workerActions }, 14)

/**
 * The Units editor's Worker/Engineer Actions → "Build Outpost" checkbox.
 */
val PrtoEntry.buildOutpost: Boolean by BitCollection.int.extensionBitFlag({ workerActions }, 15)

/**
 * The Units editor's Worker/Engineer Actions → "Build Barricade" checkbox.
 */
val PrtoEntry.buildBarricade: Boolean by BitCollection.int.extensionBitFlag({ workerActions }, 16)

// --- AI Strategies ---

/**
 * The Units editor's AI Strategies → Land "Defense" checkbox. Named `defenseStrategy` rather
 * than `defense` to avoid colliding with [PrtoEntry.defense] (the numeric defense stat). See
 * [PrtoEntry.otherStrategy]'s KDoc and [effectiveAiStrategies] for why a unit's Defense strategy
 * can live on a separate `PRTO` entry from its Offense strategy.
 */
val PrtoEntry.defenseStrategy: Boolean by BitCollection.int.extensionBitFlag({ aiStrategies }, 1)

/**
 * The Units editor's AI Strategies → Land "Offense" checkbox.
 */
val PrtoEntry.offenseStrategy: Boolean by BitCollection.int.extensionBitFlag({ aiStrategies }, 0)

/**
 * The Units editor's AI Strategies → Land "Explore" checkbox. Distinct from [explore] (the
 * Standard Orders "Explore" checkbox).
 */
val PrtoEntry.exploreStrategy: Boolean by BitCollection.int.extensionBitFlag({ aiStrategies }, 3)

/**
 * The Units editor's AI Strategies → Land "Artillery" checkbox.
 */
val PrtoEntry.artilleryStrategy: Boolean by BitCollection.int.extensionBitFlag({ aiStrategies }, 2)

/**
 * The Units editor's AI Strategies → Land "Cruise Missile" checkbox. Distinct from
 * [cruiseMissileAbility].
 */
val PrtoEntry.cruiseMissileStrategy: Boolean by BitCollection.int.extensionBitFlag({ aiStrategies }, 5)

/**
 * The Units editor's AI Strategies → Air "Air Bombard" checkbox.
 */
val PrtoEntry.airBombardStrategy: Boolean by BitCollection.int.extensionBitFlag({ aiStrategies }, 6)

/**
 * The Units editor's AI Strategies → Air "Defense" checkbox. Named `airDefenseStrategy` rather
 * than `airDefense` to avoid colliding with [PrtoEntry.airDefense] (the numeric stat). Distinct
 * from [interception] (an Air Mission).
 */
val PrtoEntry.airDefenseStrategy: Boolean by BitCollection.int.extensionBitFlag({ aiStrategies }, 7)

/**
 * The Units editor's AI Strategies → Sea "Naval Power" checkbox.
 */
val PrtoEntry.navalPowerStrategy: Boolean by BitCollection.int.extensionBitFlag({ aiStrategies }, 8)

/**
 * The Units editor's AI Strategies → Air "Air Transport" checkbox.
 */
val PrtoEntry.airTransportStrategy: Boolean by BitCollection.int.extensionBitFlag({ aiStrategies }, 9)

/**
 * The Units editor's AI Strategies → Sea "Naval Transport" checkbox.
 */
val PrtoEntry.navalTransportStrategy: Boolean by BitCollection.int.extensionBitFlag({ aiStrategies }, 10)

/**
 * The Units editor's AI Strategies → Sea "Naval Carrier" checkbox.
 */
val PrtoEntry.navalCarrierStrategy: Boolean by BitCollection.int.extensionBitFlag({ aiStrategies }, 11)

/**
 * The Units editor's AI Strategies → Sea "Naval Missile Transport" checkbox.
 */
val PrtoEntry.navalMissileTransportStrategy: Boolean by BitCollection.int.extensionBitFlag({ aiStrategies }, 17)

/**
 * The Units editor's AI Strategies → Land "Tactical Nuke" checkbox.
 */
val PrtoEntry.tacticalNukeStrategy: Boolean by BitCollection.int.extensionBitFlag({ aiStrategies }, 15)

/**
 * The Units editor's AI Strategies → Land "ICBM" checkbox.
 */
val PrtoEntry.icbmStrategy: Boolean by BitCollection.int.extensionBitFlag({ aiStrategies }, 16)

/**
 * The Units editor's AI Strategies → Land "Flag Unit" checkbox. Distinct from [flagUnitAbility].
 */
val PrtoEntry.flagUnitStrategy: Boolean by BitCollection.int.extensionBitFlag({ aiStrategies }, 18)

/**
 * The Units editor's AI Strategies → Land "Terraform" checkbox.
 */
val PrtoEntry.terraformStrategy: Boolean by BitCollection.int.extensionBitFlag({ aiStrategies }, 12)

/**
 * The Units editor's AI Strategies → Land "Settle" checkbox.
 */
val PrtoEntry.settleStrategy: Boolean by BitCollection.int.extensionBitFlag({ aiStrategies }, 13)

/**
 * The Units editor's AI Strategies → "Army" checkbox — true only for the Army unit. A distinct,
 * real checkbox that shares its population with [armyAbility] (bit 18 of [abilities]) rather than
 * being a redundant echo of it.
 */
val PrtoEntry.armyStrategy: Boolean by BitCollection.int.extensionBitFlag({ aiStrategies }, 4)

/**
 * The Units editor's AI Strategies → "Leader" checkbox — true only for the Leader unit.
 */
val PrtoEntry.leaderStrategy: Boolean by BitCollection.int.extensionBitFlag({ aiStrategies }, 14)

/**
 * The Units editor's AI Strategies → "King" checkbox — true only on King-ability units. A
 * distinct, real checkbox that shares its population with [kingAbility] (bit 29 of [abilities])
 * rather than being a redundant echo of it.
 */
val PrtoEntry.kingStrategy: Boolean by BitCollection.int.extensionBitFlag({ aiStrategies }, 19)

// --- Air Missions ---

/**
 * The Units editor's Air Missions → "Bombing" checkbox. Distinct from [bombard] (Special
 * Actions). Bit 12 of [flags4Bits] echoes this bit.
 */
val PrtoEntry.bombing: Boolean by BitCollection.int.extensionBitFlag({ airMissions }, 0)

/**
 * The Units editor's Air Missions → "Recon" checkbox.
 */
val PrtoEntry.recon: Boolean by BitCollection.int.extensionBitFlag({ airMissions }, 1)

/**
 * The Units editor's Air Missions → "Interception" checkbox. Distinct from [airDefenseStrategy]
 * (AI Strategies → Air "Defense").
 */
val PrtoEntry.interception: Boolean by BitCollection.int.extensionBitFlag({ airMissions }, 2)

/**
 * The Units editor's Air Missions → "Re-base" checkbox. Bit 15 of [flags4Bits] echoes this bit
 * OR'd with [goTo] — see that accessor's KDoc.
 */
val PrtoEntry.rebase: Boolean by BitCollection.int.extensionBitFlag({ airMissions }, 3)

/**
 * The Units editor's Air Missions → "Precision Bombing" checkbox. Bit 13 of [flags4Bits] echoes
 * this bit.
 */
val PrtoEntry.precisionBombing: Boolean by BitCollection.int.extensionBitFlag({ airMissions }, 4)

// --- Internal note: bits not yet confirmed, deliberately left without accessors ---
//
// Do not add a bit accessor until one candidate is isolated to a single bit with real-data
// evidence, per this project's usual bar. A few editor items (Zone of Control, Create Craters,
// Requires Support, Worker Strength, ...) turned out NOT to be packed bits at all: they're already
// their own typed fields on PrtoEntry (see that class's KDoc).
//
// Everything below was cross-checked against a full sweep of the base Conquests ruleset plus all
// 21 official scenario files (plus, for the most recent checks, the additional single-player
// Conquests-folder scenarios) — see each item for its own real-data anchors.
//
// ## Still-unresolved single bits
// - [specialActions] bit 19 (global flags3 bit 51): present only on Marauder, Warlord, and
//   Pillager (all MP Fall of Rome) and a Middle-Ages-scenario-specific Berserk (distinct from the
//   base ruleset's Berserk, which lacks it) — 4 real anchors, all raiding/pillaging-themed
//   barbarian or barbarian-adjacent units, confirmed unchanged even after expanding the search to
//   every civ3PTW scenario file too (4745 entries total, zero new anchors). Confirmed NOT an
//   Ability or AI Strategy. The real Units editor names exactly 14 Special Actions, and this
//   codebase already has 14 named ([load] through [startsScienceAge]) — so this bit is NOT one of
//   them; it's presumed a non-editor-visible internal engine flag rather than a checkbox, which is
//   why [specialActions] was promoted to a directly-parsed `Int` despite this bit staying
//   unexplained. "Pillager"'s name is suggestive but this is a different bit from the
//   already-confirmed [pillage].
//
// ## flags4Bits — confirmed to be (mostly) an "actions mix" of bits copied from elsewhere
// A source Apolyton reverse-engineering thread describes this field as a mix of bytes copied from
// the already-decoded action fields. First confirmed via an exhaustive per-bit correlation sweep
// across the entire corpus checked (4745 entries: base ruleset, all 21 official scenario files,
// the additional single-player Conquests-folder scenarios, every civ3PTW scenario file, and
// civ3X.bix), then fully resolved bit-by-bit via a real purpose-built test scenario (17 test units,
// each isolating exactly one [workerActions] bit) that split apart several ties the corpus-only
// sweep couldn't.
//
// Real Civ3 hotkey documentation explains WHY this looks like a "mix" rather than pointless
// duplication: the game exposes several synthetic/composite commands layered on top of the raw
// editor checkboxes — e.g. "Build Road to destination" (Ctrl-R) and "Build Railroad to
// destination" (Ctrl-Shift-R) alongside the plain Build Road/Railroad actions; "Irrigate to
// Nearest City" (Ctrl-I); restricted-automate variants "Automate, Clear forests only" (Shift-F),
// "...Clear jungles only" (Shift-J), and "...Clean up pollution only" (Shift-P), alongside plain
// Automate and further variants "...leave existing improvements" (Shift-A), "...this city only"
// (Shift-I), and "...this city only, leave existing improvements" (Ctrl-Shift-I); "Sentry (wake
// near enemy only)" alongside plain Sentry; a Settler-specific composite "Build road to
// destination, then build colony" (Ctrl-B); and "Automated Bombard"/"Automated Precision Bombing"
// (Shift-B/Shift-P) alongside plain Bombard/Precision Bombing. Each synthetic command's
// availability is fully determined by (and therefore population-identical to) whichever raw
// ability it's built from, which is exactly what would produce multiple flags4Bits echoing the
// same source bit (e.g. bits 3 and 5 both echoing [buildRoad]) without either bit being a
// meaningless duplicate. The Units editor itself almost certainly does NOT expose these synthetic
// commands as their own checkboxes — they're a UI/engine-level feature layered on top of the raw
// actions — so flags4 is very likely a computed cache the game builds from the real editor data
// for fast hotkey-availability lookup, not itself raw editor-authored data. This also means real
// data can never break a tie between two synthetic commands built from the same raw ability (e.g.
// which of bits 10, 11, and 14 is plain Automate vs. one of its several variants) — no file will
// ever exist where one is set and not the others, since neither the editor nor any real save can
// independently control them.
// - Bit 0: exact echo of [sentry] — plausibly specifically "Sentry (wake near enemy only)" per
//   the synthetic-command reading above, rather than a redundant re-cache of plain Sentry itself.
// - Bit 1: exact echo of [bombard] — plausibly specifically "Automated Bombard" per the
//   synthetic-command reading above.
// - Bit 2: requires BOTH [clearForest] AND [clearJungle] to be set — a conjunction, not a plain
//   echo of either bit alone (confirmed via the purpose-built "Lumberjack" and "Jungle Clearer"
//   test units, which each isolate one of the two and neither alone sets this bit).
// - Bits 3, 5: each an exact echo of [buildRoad].
// - Bit 4: exact echo of [buildRailroad] — previously thought ambiguous among a 10-bit cluster of
//   [workerActions] bits that a corpus-only sweep could never split apart (no real official file
//   has ever had a Worker/Engineer-type unit with only some of those 10 actions), resolved by the
//   purpose-built "Railroad Builder" test unit isolating this one alone.
// - Bit 6: exact echo of [irrigate] (same previously-ambiguous cluster, resolved by "Irrigator").
// - Bit 7: exact echo of [clearForest] (resolved by "Lumberjack").
// - Bit 8: exact echo of [clearJungle] (resolved by "Jungle Clearer").
// - Bit 9: exact echo of [clearPollution] (same previously-ambiguous cluster, resolved by
//   "Pollution Cleanup Crew").
// - Bit 12: exact echo of [bombing].
// - Bit 13: exact echo of [precisionBombing] — plausibly specifically "Automated Precision
//   Bombing" per the synthetic-command reading above.
// - Bit 14: exact echo of [automate] (same previously-ambiguous cluster, resolved by "Automated
//   Worker").
// - Bit 15: exact echo of [goTo] OR'd with [rebase] — a genuine disjunction of two bits from two
//   different fields (Standard Orders and Air Missions), confirmed with zero exceptions across
//   4901 real entries. A source Apolyton reverse-engineering thread independently suggested this
//   same goTo/rebase pairing, under a different bit layout from this codebase's.
// - Bits 10, 11: each an echo of [automate] — matching a source Apolyton reverse-engineering
//   thread's independent claim that both bits are tied to Automate — confirmed for 4899 of 4901
//   real entries checked. The lone 2 exceptions are both deliberately boundary-probing purpose-
//   built test units, not real gameplay data: "Automated Worker" (isolates [automate] alone, with
//   every other [workerActions] bit clear) and "Non-AI Worker" (has every [workerActions] bit set,
//   including [automate], but was purpose-built to lack [terraformStrategy]) — in both, bits 10
//   and 11 stay unset despite [automate] being set. A follow-up check ruled out
//   "[automate] AND [terraformStrategy]" as the real rule: that combination is wrong on several
//   real (non-test) PTW scenario units (e.g. Legionary and Colonist in "Ancient World.bix", which
//   have [automate] set without [terraformStrategy], yet do have bits 10/11 set) — so the 2
//   exceptions remain unexplained curiosities specific to those two test units, not evidence of a
//   different underlying rule. Per the synthetic-command reading above, real Automate has at least
//   4 distinct hotkey variants (plain, "leave existing improvements", "this city only", and both
//   combined) sharing bits 10, 11, and 14 — one bit short of one-per-variant, and since every
//   variant's availability is identical to plain [automate]'s, no real data will ever be able to
//   say which bit is which variant (or confirm whether a 4th variant simply has no dedicated bit).
// - Bit 16: always 1 across the entire corpus, including Princess (the sparsest real entry seen).
//   Not an echo of anything in the 4 action fields — possibly a separate, unrelated reserved or
//   version marker, outside the scope of the "actions mix" claim.
// - Bits 17-31: always 0 across the entire corpus — confirmed unused.
//
// The same purpose-built test scenario also caught a real bug: its "Outpost Builder" and "Radar
// Tower Builder" units revealed [buildOutpost] and [buildRadarTower] had been swapped (an
// unverified guess from before either had a real single-unit anchor) — now corrected.
//
// ## flags2 — macro-layout confirmed, bit-for-bit packing within each range still open
// Confirmed via real vanilla files (civ3mod.bic and both real "Earth" scenario files, all
// major=4/minor=1) cross-checked against a real PTW file (civ3X.bix, major=11/minor=18): in
// [Civ3FormatEra.VANILLA], flags2 is where Standard Orders, Special Actions, Worker/Engineer
// Actions, and Air Missions actually live, packed together into these 8 bytes — Worker's real
// flags2 is dramatically more complex than Warrior's, and Fighter/Bomber carry distinct trailing
// bytes, while [standardOrders]/[specialActions]/[workerActions]/[airMissions]/flags4 are all zero
// (absent, as already documented). In the same real PTW file, it's the exact opposite: flags2 is
// zero on every entry checked, while [standardOrders]/[specialActions]/[workerActions]/
// [airMissions]/flags4 are populated exactly like real Conquests files (Settler's workerActions
// matches [buildCity]+[joinCity], etc.) — confirming PTW is already on the modern separate-fields
// layout, so the schema break is at the vanilla→PTW boundary, not PTW→Conquests.
//
// A source Apolyton reverse-engineering thread's claimed exact vanilla bit layout — bits 0-4
// Standard Orders (Skip Turn, Wait, Fortify, Disband, Go To), 5-13 Special Actions (Load, Unload,
// Airlift, Pillage, Bombard, Airdrop, Build Army, Finish Improvements, Upgrade Unit), 14-26
// Worker/Engineer Actions (Build Colony, Build City, Build Road, Build Railroad, Build Fort, Build
// Mine, Irrigate, Clear Forest, Clear Jungle, Plant Forest, Clear Pollution, Automate, Join City),
// 27-31 unused, 32-36 Air Missions (Bombing, Recon, Interception, Re-base, Precision Bombing), 37
// unused, 38-50 a flags4-style synthetic-commands mix, 51-63 unused — is confirmed against the
// real vanilla base ruleset (civ3mod.bic, 77 entries):
// - Zero bits ever appear in any of the 3 claimed-unused ranges (27-31, 37, 51-63).
// - Settler's real bits 14-26 are exactly {15 (Build City), 26 (Join City)} — matching
//   [workerActions]'s own confirmed Settler population exactly.
// - Worker's real bits 14-26 are every one of them except 15 (Build City) — matching
//   [workerActions]'s own confirmed Worker population exactly (everything except [buildCity]).
// - Fighter's real bits 32-35 (Bombing, Recon, Interception, Re-base, missing Precision Bombing)
//   and Bomber's real bits 32 and 35 (Bombing, Re-base only) exactly match their known real
//   [airMissions] populations.
// - The 38-50 range decomposes exactly like [flags4]'s own structure: bit 38 is an exact
//   bit-for-bit match for bit 9 (Bombard) across all 77 entries; bits 39-48 (10 bits) are all
//   mutually identical to each other and confined to the sole real Worker entry — the same shape
//   as [flags4]'s own Worker-exclusive cluster; bit 49 is an exact match for bit 32 (Bombing); and
//   bit 50 is an exact match for bit 36 (Precision Bombing) — directly mirroring [flags4]'s own
//   [bombard]/[bombing]/[precisionBombing] echoes and Worker-exclusive cluster, bit-for-bit in
//   structure if not in absolute position.
//
// This confirms the exact macro-layout, but NOT which specific action within each multi-item range
// is which specific bit (e.g. which of bits 14-26 is Build Road vs. Build Railroad) — vanilla
// lacks the scenario-file richness Conquests has (no known real vanilla scenario carries its own
// PRTO section; only the base ruleset does), so there's no equivalent of the purpose-built test
// scenario that resolved [flags4]'s per-bit ambiguities. Decoding the exact intra-range order
// remains a distinct, still out-of-scope reverse-engineering effort.
//
// Separately, on real Conquests-era files specifically: flags2 is zero on every entry carried over
// unmodified from the original ruleset (e.g. this corpus's Settler and Worker), but a real
// purpose-built test scenario (10 custom units, each added or edited directly in the Units editor
// in one sitting) has every one of those units sharing the exact same nonzero flags2 value —
// suggesting that on Conquests-era files, any nonzero flags2 left over from vanilla's original use
// of this region gets overwritten with an editor/session stamp (a save timestamp, version marker,
// or similar) when a unit is touched in the modern editor, rather than encoding anything about the
// unit itself. Consistent with Princess and Lincoln (both plausibly hand-edited into their
// scenarios) also carrying nonzero flags2, each with its own large, regular ~32-bit pattern (bits
// set in pairs every 4 positions), while ordinary carried-over Conquests-era units don't. No
// accessor exists for any part of flags2.
