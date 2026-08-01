package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `PRTO` section: a unit type (prototype) definition.
 *
 * @param unitStatistics This unit's numeric stats, upgrade path, and combat-support/creation
 *   flags. See [PrtoUnitStatistics].
 * @param required A `TECH` section index, per the Conquests Rules Editor.
 * @param requiredResource1 A `GOOD` section index, per the Conquests Rules Editor. Same treatment
 *   applies to [requiredResource2], [requiredResource3].
 * @param abilities The Units editor's Abilities checkboxes (Wheeled, Foot Unit, Blitz, Radar,
 *   Amphibious, Stealth, King, ...) plus a handful of single-unit-anchor traits (Nuclear Weapon,
 *   Army, Leader, ...). See `PrtoEntryFlags.kt` for the individual named accessors.
 * @param aiStrategies The Units editor's 20 AI Strategy checkboxes (Offense, Defense, Explore,
 *   Artillery, Naval Power, ..., Army, Leader, King). See `PrtoEntryFlags.kt` for the individual
 *   named accessors.
 * @param availableTo Per-civilization availability bitmask, per the Conquests Rules Editor: bit
 *   *n* means this unit is available to the file's own `RACE` section index *n*, including index 0
 *   (the barbarian placeholder). See [availableToRaces] to resolve it against the file's `RACE`
 *   entries directly.
 * @param flags2 8 bytes. In real [Civ3FormatEra.VANILLA] files this is where Standard Orders,
 *   Special Actions, Worker/Engineer Actions, and Air Missions live, packed together rather than
 *   in the later separate fields ([standardOrders] etc.); it is all zero in real
 *   [Civ3FormatEra.PTW] and [Civ3FormatEra.CONQUESTS] files, which use those separate fields
 *   instead. Not decomposed at the type level; no accessors exist for any part of it.
 * @param type This unit's domain — see [PrtoDomain] for what each value means, per the Conquests
 *   Rules Editor's "Class" control (Land/Sea/Air).
 * @param otherStrategy Despite the name inherited from prior reverse-engineering sources, this is
 *   not itself an AI-strategy bitmask: it is -1 for most entries, and a self-referencing `PRTO`
 *   section index — see [otherStrategyPrto] — for a trailing block of entries (indices 124-140 in
 *   the base Conquests ruleset). The same mechanism exists in real PTW files, so it predates
 *   Conquests entirely.
 *
 *   Each such entry duplicates an earlier entry's name, stats, and [abilities], but pairs a
 *   different, thematically distinct [aiStrategies] value with it instead of its own — e.g.
 *   Rifleman/Infantry/Impi/Legionary pair Offense on the canonical entry with Defense on the
 *   duplicate; Conquistador/Chasqui Scout pair Offense with Explore; Carrack/Dromon pair Naval
 *   Power with Naval Transport. This is how a unit gets a second AI Strategy alongside its own,
 *   which the real Units editor displays merged into one entry — see [effectiveAiStrategies] to
 *   compute the merged value programmatically.
 * @param standardOrders The Units editor's Standard Orders checkboxes (Skip Turn, Wait, Fortify,
 *   Disband, Go To, Explore, Sentry). See `PrtoEntryFlags.kt` for the individual named accessors.
 *   Absent from [Civ3FormatEra.VANILLA] files (the item ends immediately after
 *   [PrtoUnitStatistics.hpBonus]), read defensively.
 * @param specialActions The Units editor's Special Actions checkboxes (Load, Unload, Bombard,
 *   Pillage, ...). See `PrtoEntryFlags.kt` for the individual named accessors. Not decomposed at
 *   the type level. Absent from [Civ3FormatEra.VANILLA] files, read defensively (see
 *   [standardOrders]).
 * @param workerActions The Worker/Engineer Actions grid, one bit per action (Build Road, Build
 *   Mine, Join City, ...). See `PrtoEntryFlags.kt` for the 17 named per-bit accessors.
 * @param airMissions The 5 Air Missions checkboxes (Bombing, Recon, Interception, Re-base,
 *   Precision Bombing). See `PrtoEntryFlags.kt` for the named per-bit accessors.
 * @param flags4 4 bytes, most of which is an "actions mix": synthetic hotkey-availability bits the
 *   game computes from [standardOrders], [specialActions], [workerActions], and [airMissions],
 *   rather than checkboxes of their own. See [flags4Bits] in `PrtoEntryFlags.kt` for the per-bit
 *   breakdown. Not decomposed at the type level.
 * @param ignoreMovementCost One flag byte per `TERR` section entry, preserved raw as an opaque
 *   per-terrain-type bit array; not decomposed. Sized dynamically by the file's own `TERR`
 *   section entry count, threaded in from `Civ3RootParserImpl` — real [Civ3FormatEra.VANILLA] and
 *   [Civ3FormatEra.PTW] files always have 12 `TERR` entries, real [Civ3FormatEra.CONQUESTS] files
 *   always have 14 (Conquests added 2 new terrain types, marshes and volcanoes). Absent from
 *   [Civ3FormatEra.VANILLA] files, read defensively — see `PrtoEntryParser`.
 * @param unknown 16 bytes with zero documented behavior from either reverse-engineering source;
 *   preserved raw, not validated. Absent from [Civ3FormatEra.VANILLA] and
 *   [Civ3FormatEra.PTW] files (the entire tail from here through [PrtoUnitStatistics.airDefense]
 *   is a [Civ3FormatEra.CONQUESTS]-era expansion), read defensively.
 * @param unknown2 4 bytes with zero documented behavior from either reverse-engineering source;
 *   preserved raw, not validated. Absent from [Civ3FormatEra.VANILLA] and
 *   [Civ3FormatEra.PTW] files, read defensively.
 * @param enslaveResults A `PRTO` section self-reference, per the Conquests Rules Editor.
 * @param stealthTargetUnitTypes `PRTO` section self-references identifying units this entry's
 *   Stealth Attack ability (see [stealthAttack]) cannot target — an exclusion list, not an
 *   allow-list, despite the name. The Stealth Fighter's list, for example, excludes Leader,
 *   Princess, and King-ability units, with one known exception (Smoke-Jaguar). Absent from
 *   [Civ3FormatEra.VANILLA] and [Civ3FormatEra.PTW] files, read defensively.
 * @param unknown3 8 bytes with zero documented behavior from either reverse-engineering source;
 *   preserved raw, not validated. Absent from [Civ3FormatEra.VANILLA] and
 *   [Civ3FormatEra.PTW] files, read defensively.
 * @param unknown4 4 bytes with zero documented behavior from either reverse-engineering source;
 *   preserved raw, not validated. Absent from [Civ3FormatEra.VANILLA] and
 *   [Civ3FormatEra.PTW] files, read defensively.
 */
data class PrtoEntry(
    val unitStatistics: PrtoUnitStatistics,
    val name: String,
    val civilopediaEntry: String,
    val iconIndex: Int,
    val required: Int,
    val requiredResource1: Int,
    val requiredResource2: Int,
    val requiredResource3: Int,
    val abilities: Int,
    val aiStrategies: Int,
    val availableTo: Int,
    val flags2: ByteString,
    val type: Int,
    val otherStrategy: Int,
    val standardOrders: Int,
    val specialActions: Int,
    val workerActions: Int,
    val airMissions: Int,
    val flags4: ByteString,
    val ignoreMovementCost: ByteString,
    val unknown: ByteString,
    val enslaveResults: Int,
    val unknown2: ByteString,
    val stealthTargetUnitTypes: List<Int>,
    val unknown3: ByteString,
    val unknown4: ByteString,
) {
    init {
        require(flags2.size == 8) { "PrtoEntry.flags2 must be exactly 8 bytes, was ${flags2.size}" }
        require(flags4.size == 4) { "PrtoEntry.flags4 must be exactly 4 bytes, was ${flags4.size}" }
        require(unknown.size == 16) { "PrtoEntry.unknown must be exactly 16 bytes, was ${unknown.size}" }
        require(unknown2.size == 4) { "PrtoEntry.unknown2 must be exactly 4 bytes, was ${unknown2.size}" }
        require(unknown3.size == 8) { "PrtoEntry.unknown3 must be exactly 8 bytes, was ${unknown3.size}" }
        require(unknown4.size == 4) { "PrtoEntry.unknown4 must be exactly 4 bytes, was ${unknown4.size}" }
    }
}
