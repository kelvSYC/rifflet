package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `GAME` section: global scenario/ruleset settings (there is typically exactly
 * one `GAME` entry per file, per existing reverse-engineering documentation's own "(1)"
 * annotation on the section's item count).
 *
 * A 4-byte `mapVisible (long)` field is documented by existing reverse-engineering work as
 * present only in BIX files (`major >= 11.19`, absent from BIQ/`major = 12`), positioned between
 * [civAllianceStatuses] and [victoryPointLimit]. A separate reverse-engineered reference
 * implementation never actually parses this field — it exists only as a comment in its source —
 * and this codebase does not parse it either. Do not confuse this skipped field
 * with [mapVisible] below, an unconditional 1-byte field that IS modeled.
 *
 * Every field after the fixed 5-field header ([defaultGameRules]/[defaultVictoryConditions]/
 * [numberOfPlayableCivs]/[playableCivIds]/[flags]) is version-dependent:
 * - [Civ3FormatEra.VANILLA] files (`major=3`/`4`) can end immediately after [flags] — a bare
 *   16-byte item.
 * - [Civ3FormatEra.PTW] files (`major=11`) are the one confirmed case of genuine within-era
 *   `minor` sensitivity in this codebase — they end at one of 3 different points depending on
 *   `minor`: [autoPlaceVictoryLocations] for `minor=6`/`9`/`10`, [debugMode] for `minor=13`, or
 *   [scenarioSearchFolders] for the dominant `minor=18` tier.
 * - [Civ3FormatEra.CONQUESTS] files (`major=12`) always include everything through
 *   [scenarioSearchFolders] and the entire alliance/plague/victory-scoring block through
 *   [eruptionPeriod]; they omit only the trailing mp-timing fields
 *   ([mpBasetime]/[mpCityTime]/[mpUnitTime]) on `minor=6` (present on `minor=7`/`8`).
 *
 * Alliances, plagues, victory scoring, and multiplayer timing were evidently introduced together
 * as a single [Civ3FormatEra.CONQUESTS]-era `GAME` expansion, and [Civ3FormatEra.PTW] itself grew
 * the fixed game-settings block ([placeCaptureUnits] through [scenarioSearchFolders])
 * incrementally across several [Civ3FormatEra.PTW] patch revisions before
 * [Civ3FormatEra.CONQUESTS]'s larger expansion. Every field from [placeCaptureUnits] onward is
 * read defensively — see `GameEntryParser`.
 *
 * @param numberOfPlayableCivs The number of civs enumerated in [playableCivIds] and
 *   [civAllianceStatuses] (0 means "all civs playable"); stored explicitly because its value
 *   0 carries distinct semantic meaning beyond mere list-size recoverability.
 * @param playableCivIds `RACE` section indices, per the Scenario Properties editor's "Playable
 *   Civilizations" listbox, which shows real civ names. Sized by [numberOfPlayableCivs]. Stays
 *   unconditional even after
 *   this class's other defensive-parsing extensions — every known cutoff tier includes it, though
 *   [Civ3FormatEra.VANILLA]'s behavior when `numberOfPlayableCivs > 0` is unconfirmed.
 * @param flags 4 bytes with 16 named booleans (victory condition toggles, game rule toggles);
 *   see [GameEntry.dominationVictoryEnabled] and its sibling accessors in `GameEntryFlags.kt`.
 * @param civAllianceStatuses One alliance-status value (0-4, 0=none) per civ, in the same order
 *   as [playableCivIds]; sized by [numberOfPlayableCivs], not separately counted. Absent from
 *   [Civ3FormatEra.PTW] files — defaults to [numberOfPlayableCivs] zeros ("no alliance", which is
 *   accurate: [Civ3FormatEra.PTW] has no alliance concept at all) rather than an empty list, to
 *   satisfy this property's own size invariant.
 * @param unknown 5 bytes with zero documented behavior from either reverse-engineering source; preserved raw,
 *   not validated. Same treatment as `RaceEntry.unknown`. Absent from [Civ3FormatEra.PTW] files,
 *   read defensively.
 * @param allianceNames 5 fixed alliance-name slots (256 bytes each), index 0 conventionally
 *   "unallied"/blank, confirmed explicitly by existing reverse-engineering documentation. Absent
 *   from [Civ3FormatEra.PTW] files (alliances are a [Civ3FormatEra.CONQUESTS]-only feature),
 *   read defensively.
 * @param allianceWars A flat, row-major 5×5 matrix of war-status values between alliances; index
 *   `[i, j]` is `allianceWars[i * 5 + j]`. Confirmed explicitly by existing reverse-engineering
 *   documentation's nested "for each alliance: war with alliance #0..#4" description. Absent
 *   from [Civ3FormatEra.PTW] files, read defensively.
 * @param unknown2 264 bytes with zero documented *meaning* from either reverse-engineering source, though
 *   both sources agree on its byte sub-structure (a 4-byte int followed by a 260-byte string
 *   region) without confirming what either part represents; preserved raw as a single opaque
 *   region, not split, matching a separate reverse-engineered reference implementation's own
 *   grouping. Absent from [Civ3FormatEra.PTW]
 *   files, read defensively. The leading int defaults to `0` and the string region defaults to
 *   the literal ASCII text `"Unknown"` — reproduced identically across multiple unrelated real
 *   files, which argues against this being uninitialized memory (unlike the padding bytes that
 *   follow it, which vary and do look uninitialized). Structural position (directly between the
 *   plague fields and [eruptionPeriod]) is suggestive but unconfirmed: possibly a name field for
 *   Conquests' other catastrophe type (eruptions), analogous to [plagueName] for plagues.
 * @param mapVisible An unconditional 1-byte field per both reverse-engineering sources' original
 *   documentation — but absent from [Civ3FormatEra.PTW] files along with every other field from
 *   [civAllianceStatuses] onward, so "unconditional" only holds for [Civ3FormatEra.CONQUESTS].
 *   Distinct from the BIX-only `mapVisible (long)` field this codebase does not parse (see the
 *   class-level note above).
 * @param unknown3 4 bytes with zero documented behavior from either reverse-engineering source; preserved
 *   raw, not validated. Absent from [Civ3FormatEra.PTW] files, read defensively. Defaults to
 *   `0xFFFFFFFF` (-1 as a signed Int) — matching Civ3's common "-1 = none/unset" sentinel
 *   convention used elsewhere in this format (e.g. `barbarianTribe`, `victoryPointLocation`),
 *   suggesting this may be a reference-like field (an index) rather than a flag or count field,
 *   which would more typically default to `0`. Not confirmed further.
 * @param eruptionPeriod The last field present in every [Civ3FormatEra.CONQUESTS] file regardless
 *   of the `minor=6` mp-timing-fields cutoff below — but, like every field since
 *   [civAllianceStatuses], absent from [Civ3FormatEra.PTW] files, read defensively.
 * @param mpBasetime Present only in [Civ3FormatEra.CONQUESTS] files with `VER#` header `minor=7`
 *   or `minor=8`; absent on `minor=6`, read defensively.
 * @param mpCityTime Present only in [Civ3FormatEra.CONQUESTS] files with `VER#` header `minor=7`
 *   or `minor=8`; absent on `minor=6`, read defensively.
 * @param mpUnitTime Present only in [Civ3FormatEra.CONQUESTS] files with `VER#` header `minor=7`
 *   or `minor=8`; absent on `minor=6`, read defensively.
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
