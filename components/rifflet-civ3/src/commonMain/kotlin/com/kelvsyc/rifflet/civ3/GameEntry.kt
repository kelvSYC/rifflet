package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `GAME` section: global scenario/ruleset settings (there is typically exactly
 * one `GAME` entry per file, per existing reverse-engineering documentation's own "(1)"
 * annotation on the section's item count).
 *
 * A 4-byte `mapVisible (long)` field is documented by existing reverse-engineering work as
 * present only in BIX files (`major >= 11.19`, absent from BIQ/`major = 12`), positioned between
 * [civAllianceStatuses] and [victoryPointLimits]. A separate reverse-engineered reference
 * implementation never actually parses this field — it exists only as a comment in its source —
 * and this codebase does not parse it either. Do not confuse this skipped field
 * with [mapVisible] below, an unconditional 1-byte field that IS modeled.
 *
 * Every field/group after the fixed 5-field header ([defaultGameRules]/[defaultVictoryConditions]/
 * [numberOfPlayableCivs]/[playableCivIds]/[flags]) is version-dependent:
 * - [Civ3FormatEra.VANILLA] files (`major=3`/`4`) can end immediately after [flags] — a bare
 *   16-byte item.
 * - [Civ3FormatEra.PTW] files (`major=11`) are the one confirmed case of genuine within-era
 *   `minor` sensitivity in this codebase — they end at one of 3 different points depending on
 *   `minor`: [autoPlaceVictoryLocations] for `minor=6`/`9`/`10`, [debugMode] for `minor=13`, or
 *   the end of [scenarioSearchFolders] (with [timeOptions] present) for the dominant `minor=18`
 *   tier.
 * - [Civ3FormatEra.CONQUESTS] files (`major=12`) always include everything through
 *   [scenarioSearchFolders] and the entire alliance/plague/victory-scoring block through
 *   [eruptionPeriod]; they omit only [mpTimers] on `minor=6` (present on `minor=7`/`8`).
 *
 * Alliances, plagues, victory scoring, and multiplayer timing were evidently introduced together
 * as a single [Civ3FormatEra.CONQUESTS]-era `GAME` expansion — grouped below into
 * [victoryPointLimits], [lockedAlliance], [plagueSettings], and [mpTimers], each `null` whenever
 * its fields are absent from the file, matching the official Conquests Rules Editor's own
 * `Scenario Properties` tab structure. [Civ3FormatEra.PTW] itself grew the fixed game-settings
 * block ([placeCaptureUnits] through [scenarioSearchFolders]) incrementally across several
 * [Civ3FormatEra.PTW] patch revisions before [Civ3FormatEra.CONQUESTS]'s larger expansion, with
 * [timeOptions] grouping the tier that's present from `minor=18` onward. Every field/group from
 * [placeCaptureUnits] onward is read defensively — see `GameEntryParser`.
 *
 * @param numberOfPlayableCivs The number of civs enumerated in [playableCivIds] and
 *   [civAllianceStatuses] (0 means "all civs playable"); stored explicitly because its value
 *   0 carries distinct semantic meaning beyond mere list-size recoverability.
 * @param playableCivIds `RACE` section indices, per the Scenario Properties editor's "Playable
 *   Civilizations" listbox, which shows real civ names. Sized by [numberOfPlayableCivs]. Stays
 *   unconditional even after this class's other defensive-parsing extensions — every known cutoff
 *   tier includes it, though [Civ3FormatEra.VANILLA]'s behavior when `numberOfPlayableCivs > 0`
 *   is unconfirmed.
 * @param flags 4 bytes with 16 named booleans (victory condition toggles, game rule toggles);
 *   see [GameEntry.dominationVictoryEnabled] and its sibling accessors in `GameEntryFlags.kt`.
 * @param timeOptions The `Scenario` tab's "Time Options" groupbox (calendar/turn-timing
 *   settings), absent before [Civ3FormatEra.PTW] `minor=18`. See [GameTimeOptions].
 * @param scenarioSearchFolders The "Scenario Search" field. Present exactly when [timeOptions]
 *   is non-`null`, but a separate editor field (its own groupbox), not part of that group.
 * @param civAllianceStatuses One alliance-status value (0-4, 0=none) per civ, in the same order
 *   as [playableCivIds]; sized by [numberOfPlayableCivs], not separately counted. Absent from
 *   [Civ3FormatEra.PTW] files — defaults to [numberOfPlayableCivs] zeros ("no alliance", which is
 *   accurate: [Civ3FormatEra.PTW] has no alliance concept at all) rather than an empty list, to
 *   satisfy this property's own size invariant.
 * @param victoryPointLimits The `Victory Point Limits` tab, in its entirety — absent outside
 *   [Civ3FormatEra.CONQUESTS]. See [GameVictoryPointLimits].
 * @param unknown 5 bytes with zero documented behavior from either reverse-engineering source; preserved raw,
 *   not validated. Same treatment as `RaceEntry.unknown`. Absent from [Civ3FormatEra.PTW] files,
 *   read defensively.
 * @param lockedAlliance The `Locked Alliance` tab, in its entirety — absent outside
 *   [Civ3FormatEra.CONQUESTS]. See [GameLockedAlliance].
 * @param plagueSettings The `Disasters!` tab's "Plague Information" groupbox — absent outside
 *   [Civ3FormatEra.CONQUESTS]. See [GamePlagueSettings]. Does not include [eruptionPeriod] (the
 *   same tab's separate "Volcanos" groupbox).
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
 *   Conquests' other catastrophe type (eruptions), analogous to `GamePlagueSettings.plagueName`.
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
 * @param eruptionPeriod The `Disasters!` tab's "Volcanos" groupbox "Max Eruption Period" field —
 *   the last field present in every [Civ3FormatEra.CONQUESTS] file regardless of the [mpTimers]
 *   cutoff below — but, like every field since [civAllianceStatuses], absent from
 *   [Civ3FormatEra.PTW] files, read defensively.
 * @param mpTimers The `Scenario` tab's "Time Options" groupbox → "MP Timers" sub-section —
 *   present only in [Civ3FormatEra.CONQUESTS] files with `minor=7`/`8` (absent on `minor=6`). See
 *   [GameMpTimers].
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
    val timeOptions: GameTimeOptions?,
    val scenarioSearchFolders: String,
    val civAllianceStatuses: List<Int>,
    val victoryPointLimits: GameVictoryPointLimits?,
    val unknown: ByteString,
    val lockedAlliance: GameLockedAlliance?,
    val plagueSettings: GamePlagueSettings?,
    val unknown2: ByteString,
    val mapVisible: Byte,
    val retainCulture: Byte,
    val unknown3: ByteString,
    val eruptionPeriod: Int,
    val mpTimers: GameMpTimers?,
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
        require(unknown.size == 5) { "GameEntry.unknown must be exactly 5 bytes, was ${unknown.size}" }
        require(unknown2.size == 264) { "GameEntry.unknown2 must be exactly 264 bytes, was ${unknown2.size}" }
        require(unknown3.size == 4) { "GameEntry.unknown3 must be exactly 4 bytes, was ${unknown3.size}" }
    }
}
