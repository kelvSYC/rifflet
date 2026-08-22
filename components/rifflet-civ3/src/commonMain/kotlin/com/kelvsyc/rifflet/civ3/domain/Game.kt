package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.GameMpTimers
import com.kelvsyc.rifflet.civ3.GamePlagueSettings
import com.kelvsyc.rifflet.civ3.GameTimeOptions
import com.kelvsyc.rifflet.civ3.GameVictoryPointLimits
import okio.ByteString

/**
 * The scenario's global settings, mutable — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.GameEntry].
 *
 * @param playableCivs Resolved [com.kelvsyc.rifflet.civ3.GameEntry.playableCivIds], positionally
 *   — `null` per-slot for a dangling index. The wire entry's own `numberOfPlayableCivs` isn't a
 *   stored field here — it's `playableCivs.size`, derived on `toWire()` (the wire type's own
 *   `init` block already makes list-size-equals-count a hard invariant, so nothing is lost).
 * @param flags Packed boolean flags, converted from the wire layer's little-endian `ByteString`.
 *   See `GameFlags.kt` for named, settable accessors.
 * @param timeOptions/[victoryPointLimits]/[plagueSettings]/[mpTimers] Reused directly from the
 *   wire layer — no cross-references, nothing a domain-layer clone would add. Each stays `null`
 *   under the exact same absence conditions documented on the wire type.
 * @param lockedAlliance This scenario's locked-alliance identities, war relations, and victory
 *   type — the wire layer's `GameLockedAlliance` split into an identity/table shape matching
 *   `FLAV`'s [FlavorGroup]. See [LockedAlliance].
 * @param allianceStatuses One alliance-status value (0-4) per civ, parallel to [playableCivs] —
 *   not itself a cross-reference. Its value range and PTW-absence both line up with
 *   [com.kelvsyc.rifflet.civ3.AllianceSlot], but it isn't corpus-confirmed as that same identity,
 *   so it stays a plain `Int` rather than being retrofitted.
 */
data class Game(
    var defaultGameRules: Boolean = false,
    var defaultVictoryConditions: Boolean = false,
    var playableCivs: MutableList<Race?> = mutableListOf(),
    var flags: Int = 0,
    var placeCaptureUnits: Boolean = false,
    var autoPlaceKings: Boolean = false,
    var autoPlaceVictoryLocations: Boolean = false,
    var debugMode: Boolean = false,
    var timeOptions: GameTimeOptions? = null,
    var scenarioSearchFolders: String = "",
    var allianceStatuses: MutableList<Int> = mutableListOf(),
    var victoryPointLimits: GameVictoryPointLimits? = null,
    var unknown: ByteString = ByteString.of(*ByteArray(5)),
    var lockedAlliance: LockedAlliance? = null,
    var plagueSettings: GamePlagueSettings? = null,
    var unknown2: ByteString = ByteString.of(*ByteArray(264)),
    var mapVisible: Boolean = false,
    var retainCulture: Boolean = false,
    var unknown3: ByteString = ByteString.of(*ByteArray(4)),
    var eruptionPeriod: Int = 0,
    var mpTimers: GameMpTimers? = null,
)
