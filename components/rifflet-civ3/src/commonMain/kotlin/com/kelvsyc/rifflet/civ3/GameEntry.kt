package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `GAME` section: global scenario/ruleset settings (there is typically exactly
 * one `GAME` entry per file, per Apolyton's own "(1)" annotation on the section's item count).
 *
 * A 4-byte `mapVisible (long)` field is documented by Apolyton as present only in BIX files
 * (`major >= 11.19`, absent from BIQ/`major = 12`), positioned between [civAllianceStatuses] and
 * [victoryPointLimit]. `QueryCiv3`, this codebase's primary reference implementation, never
 * actually parses this field — it exists only as a comment in its source. This codebase
 * deliberately does not parse it either, deferring to a planned future validation pass against
 * real Civ3 install files rather than introducing untested format-version-conditional parsing.
 * Do not confuse this skipped field with [mapVisible] below, an unconditional 1-byte field that
 * IS modeled.
 *
 * @param numberOfPlayableCivs The number of civs enumerated in [playableCivIds] and
 *   [civAllianceStatuses] (0 means "all civs playable"); stored explicitly because its value
 *   0 carries distinct semantic meaning beyond mere list-size recoverability.
 * @param playableCivIds Likely `RACE` section indices (naming convention only); not confirmed
 *   by either cross-referenced source. Sized by [numberOfPlayableCivs].
 * @param flags 4 bytes with ~19 named booleans across both cross-referenced sources (victory
 *   condition toggles, game rule toggles); preserved raw, not decomposed. Same treatment as
 *   `RaceEntry.bonuses`.
 * @param civAllianceStatuses One alliance-status value (0-4, 0=none) per civ, in the same order
 *   as [playableCivIds]; sized by [numberOfPlayableCivs], not separately counted.
 * @param unknown 5 bytes with zero documented behavior from either cross-referenced source;
 *   preserved raw, not validated. Same treatment as `RaceEntry.unknown`.
 * @param allianceNames 5 fixed alliance-name slots (256 bytes each), index 0 conventionally
 *   "unallied"/blank, confirmed explicitly by Apolyton.
 * @param allianceWars A flat, row-major 5×5 matrix of war-status values between alliances;
 *   index `[i, j]` is `allianceWars[i * 5 + j]`. Confirmed explicitly by Apolyton's nested
 *   "for each alliance: war with alliance #0..#4" documentation.
 * @param unknown2 264 bytes with zero documented *meaning* from either cross-referenced source,
 *   though both sources agree on its byte sub-structure (a 4-byte int followed by a 260-byte
 *   string region) without confirming what either part represents; preserved raw as a single
 *   opaque region, not split, matching `QueryCiv3`'s own grouping.
 * @param mapVisible An unconditional 1-byte field, confirmed present in every file variant by
 *   both sources — distinct from the BIX-only `mapVisible (long)` field this codebase does not
 *   parse (see the class-level note above).
 * @param unknown3 4 bytes with zero documented behavior from either cross-referenced source;
 *   preserved raw, not validated.
 * @param eruptionPeriod Unconditionally present in every sampled real file (unlike the 3 fields
 *   below) — the last field before the confirmed Conquests-internal `minor=6` cutoff.
 * @param mpBasetime Present only in real Conquests files with `VER#` header `minor=7` or
 *   `minor=8`; absent in every sampled `minor=6` file, read defensively.
 * @param mpCityTime Present only in real Conquests files with `VER#` header `minor=7` or
 *   `minor=8`; absent in every sampled `minor=6` file, read defensively.
 * @param mpUnitTime Present only in real Conquests files with `VER#` header `minor=7` or
 *   `minor=8`; absent in every sampled `minor=6` file, read defensively.
 */
data class GameEntry(
    val defaultGameRules: Int,
    val defaultVictoryConditions: Int,
    val numberOfPlayableCivs: Int,
    val playableCivIds: List<Int>,
    val flags: ByteString,
    val placeCaptureUnits: Int,
    val autoPlaceKings: Int,
    val autoPlaceVictoryLocations: Int,
    val debugMode: Int,
    val useTimeLimit: Int,
    val baseTimeUnit: Int,
    val startMonth: Int,
    val startWeek: Int,
    val startYear: Int,
    val minuteTimeLimit: Int,
    val turnTimeLimit: Int,
    val timescaleNumberOfTurns: List<Int>,
    val turnNumberOfTimeUnits: List<Int>,
    val scenarioSearchFolders: String,
    val civAllianceStatuses: List<Int>,
    val victoryPointLimit: Int,
    val cityEliminationCount: Int,
    val oneCityCultureWin: Int,
    val allCitiesCultureWin: Int,
    val dominationTerrain: Int,
    val dominationPopulation: Int,
    val wonderCost: Int,
    val defeatingOpposingUnitCost: Int,
    val advancementCost: Int,
    val cityConquestPopulation: Int,
    val victoryPointScoring: Int,
    val capturingSpecialUnit: Int,
    val unknown: ByteString,
    val allianceNames: List<String>,
    val allianceWars: List<Int>,
    val allianceVictoryType: Int,
    val plagueName: String,
    val permitPlagues: Byte,
    val plagueEarliestStart: Int,
    val plagueVariation: Int,
    val plagueDuration: Int,
    val plagueStrength: Int,
    val plagueGracePeriod: Int,
    val plagueMaxOccurrence: Int,
    val unknown2: ByteString,
    val respawnFlagUnits: Int,
    val captureAnyFlag: Byte,
    val goldForCapture: Int,
    val mapVisible: Byte,
    val retainCulture: Byte,
    val unknown3: ByteString,
    val eruptionPeriod: Int,
    val mpBasetime: Int,
    val mpCityTime: Int,
    val mpUnitTime: Int,
) {
    init {
        require(playableCivIds.size == numberOfPlayableCivs) {
            "GameEntry.playableCivIds must have exactly $numberOfPlayableCivs elements, " +
                "had ${playableCivIds.size}"
        }
        require(civAllianceStatuses.size == numberOfPlayableCivs) {
            "GameEntry.civAllianceStatuses must have exactly $numberOfPlayableCivs elements, " +
                "had ${civAllianceStatuses.size}"
        }
        require(flags.size == 4) { "GameEntry.flags must be exactly 4 bytes, was ${flags.size}" }
        require(timescaleNumberOfTurns.size == 7) {
            "GameEntry.timescaleNumberOfTurns must have exactly 7 elements, had ${timescaleNumberOfTurns.size}"
        }
        require(turnNumberOfTimeUnits.size == 7) {
            "GameEntry.turnNumberOfTimeUnits must have exactly 7 elements, had ${turnNumberOfTimeUnits.size}"
        }
        require(unknown.size == 5) { "GameEntry.unknown must be exactly 5 bytes, was ${unknown.size}" }
        require(allianceNames.size == 5) {
            "GameEntry.allianceNames must have exactly 5 elements, had ${allianceNames.size}"
        }
        require(allianceWars.size == 25) {
            "GameEntry.allianceWars must have exactly 25 elements, had ${allianceWars.size}"
        }
        require(unknown2.size == 264) { "GameEntry.unknown2 must be exactly 264 bytes, was ${unknown2.size}" }
        require(unknown3.size == 4) { "GameEntry.unknown3 must be exactly 4 bytes, was ${unknown3.size}" }
    }
}
