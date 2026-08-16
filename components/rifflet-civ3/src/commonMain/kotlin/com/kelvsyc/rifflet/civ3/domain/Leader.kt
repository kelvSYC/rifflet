package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.Gender
import okio.ByteString

/**
 * One `LEAD` player/leader slot definition, mutable — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.LeadEntry].
 *
 * @param name This player's name.
 * @param humanPlayer Whether this slot is played by a human.
 * @param customCivData Whether this player's data is customized away from its civilization's
 *   defaults, per the Players tab's "Civilization Defaults" checkbox (inverted).
 * @param civilization This player's civilization choice. See [LeaderCivilization].
 * @param genderOfLeaderName This player's leader's gender.
 * @param government This player's starting government.
 * @param difficulty This player's difficulty restriction. See [LeaderDifficulty].
 * @param initialEra This player's starting era.
 * @param startCash This player's starting treasury.
 * @param color This player's civilization color index.
 * @param startUnits This player's starting unit allotments. See [StartUnit].
 * @param startingTechnologies This player's starting free technologies, positionally resolved:
 *   same length as the wire `startingTechnologyIds` list, `null` per-slot for a dangling id.
 * @param skipFirstTurn Whether this player skips their first turn.
 * @param startEmbassies Whether this player starts with embassies with every other civilization.
 * @param unknown 8 bytes with zero documented behavior; preserved raw, not validated.
 * @param unknown2 4 bytes with zero documented behavior; preserved raw, not validated.
 */
data class Leader(
    var name: String,
    var humanPlayer: Boolean = false,
    var customCivData: Boolean = false,
    var civilization: LeaderCivilization = LeaderCivilization.Unrestricted,
    var genderOfLeaderName: Gender = Gender.MALE,
    var government: Government? = null,
    var difficulty: LeaderDifficulty = LeaderDifficulty.Unrestricted,
    var initialEra: Era? = null,
    var startCash: Int = 0,
    var color: Int = 0,
    var startUnits: MutableList<StartUnit> = mutableListOf(),
    var startingTechnologies: MutableList<Tech?> = mutableListOf(),
    var skipFirstTurn: Boolean = false,
    var startEmbassies: Boolean = false,
    var unknown: ByteString = ByteString.of(*ByteArray(8)),
    var unknown2: ByteString = ByteString.of(0, 0, 0, 0),
)

/**
 * The resolved meaning of [Leader.civilization] — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.LeadEntry.civ].
 */
sealed interface LeaderCivilization {
    /** `civ == -2`: the Players tab's "Random" option — a random civilization at game start. */
    data object Random : LeaderCivilization

    /** `civ == -3`: the Players tab's "Any" option — this slot isn't restricted to one civilization. */
    data object Unrestricted : LeaderCivilization

    /** `civ >= 0`: a specific civilization. [race] is `null` when the wire index doesn't resolve. */
    data class Preset(val race: Race?) : LeaderCivilization
}

/**
 * The resolved meaning of [Leader.difficulty] — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.LeadEntry.difficulty].
 */
sealed interface LeaderDifficulty {
    /** `difficulty == -2`: the Players tab's "Any" option — this slot isn't restricted to one difficulty. */
    data object Unrestricted : LeaderDifficulty

    /** `difficulty >= 0`: a specific difficulty. [difficulty] is `null` when the wire index doesn't
     * resolve. */
    data class Preset(val difficulty: Difficulty?) : LeaderDifficulty
}
