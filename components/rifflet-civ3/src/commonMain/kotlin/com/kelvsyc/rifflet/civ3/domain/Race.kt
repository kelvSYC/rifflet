package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.Gender
import com.kelvsyc.rifflet.civ3.RaceCultureGroup
import com.kelvsyc.rifflet.civ3.RaceEraFilenames
import com.kelvsyc.rifflet.civ3.RaceLeader
import okio.ByteString

/**
 * A civilization, mutable and cross-referenced by real object references — the domain-layer
 * counterpart to [com.kelvsyc.rifflet.civ3.RaceEntry].
 *
 * A `data class`, unlike [Government]: [com.kelvsyc.rifflet.civ3.RaceEntry] has no self-referencing
 * structure analogous to [Government.relationships], so there's no circular-reference risk that
 * would require plain-class identity semantics.
 *
 * @param name The civilization's own name (e.g. "Rome").
 * @param civilopediaEntry Encyclopedia/Civilopedia entry text.
 * @param adjective The civilization's adjectival form (e.g. "Roman").
 * @param noun The civilization's people, plural (e.g. "Romans").
 * @param leader This civilization's leader identity.
 * @param cultureGroup This civilization's Culture Group.
 * @param civilizationGender This civilization's grammatical gender.
 * @param personality This civilization's default diplomatic/AI personality. See [RacePersonality].
 * @param uniqueCivilizationCounter This civilization's unique-civilization counter.
 * @param defaultColor This civilization's default color.
 * @param uniqueColor This civilization's unique color.
 * @param freeTechs This civilization's 4 free-technology grants. Unlike the wire format's row
 *   (which some real files store with a real tech occupying a slot *after* a `-1` gap — confirmed
 *   via corpus survey), [com.kelvsyc.rifflet.civ3.domain.toWire] never reorders this list;
 *   whatever occupies each slot at encode time is exactly what's written. Use [freeTechsOf] to
 *   build a canonical, front-packed list by hand.
 * @param bonuses Packed boolean flags — this civilization's traits. See `RaceFlags.kt` for named,
 *   settable accessors.
 * @param governor This civilization's default Governor automation settings. See [RaceGovernor].
 * @param plurality This civilization's plurality setting.
 * @param unitTypeForKing The unit type used to represent this civilization's King, if resolved.
 * @param flavors Bitmask membership in the `FLAV` section's 7 flavor slots. See `RaceFlags.kt` for
 *   named, settable accessors.
 * @param unknown 4 bytes with zero documented behavior. Preserved raw, not validated.
 * @param diplomacyTextIndex Likely a dialogue/text-index reference (exact target undetermined) —
 *   stays a raw index since there's no known section it resolves against.
 * @param cityNames This civilization's city-naming pool.
 * @param greatLeaderNames This civilization's Great Leader naming pool.
 * @param scientificLeaderNames This civilization's Scientific Leader naming pool.
 * @param eras This civilization's per-era animation filenames.
 */
data class Race(
    var name: String,
    var civilopediaEntry: String,
    var adjective: String,
    var noun: String,
    var leader: RaceLeader,
    var cultureGroup: RaceCultureGroup,
    var civilizationGender: Gender,
    var personality: RacePersonality = RacePersonality(),
    var uniqueCivilizationCounter: Int = 0,
    var defaultColor: Int = 0,
    var uniqueColor: Int = 0,
    var freeTechs: MutableList<Tech?> = MutableList(4) { null },
    var bonuses: Int = 0,
    var governor: RaceGovernor = RaceGovernor(),
    var plurality: Int = 0,
    var unitTypeForKing: Prto? = null,
    var flavors: Int = 0,
    var unknown: ByteString = ByteString.of(0, 0, 0, 0),
    var diplomacyTextIndex: Int = 0,
    var cityNames: List<String> = emptyList(),
    var greatLeaderNames: List<String> = emptyList(),
    var scientificLeaderNames: List<String> = emptyList(),
    var eras: List<RaceEraFilenames> = emptyList(),
) {
    init {
        require(freeTechs.size == 4) { "Race.freeTechs must be exactly 4 elements, was ${freeTechs.size}" }
    }
}

/**
 * Builds a canonical [Race.freeTechs]-shaped list from 0–4 actual [techs], front-packed with any
 * remaining slots `null`. A construction-time convenience only — the `toWire()` mapping function
 * never performs this normalization itself, to avoid reordering data read from a real file.
 */
fun freeTechsOf(vararg techs: Tech): MutableList<Tech?> {
    require(techs.size <= 4) { "freeTechsOf accepts at most 4 techs, was ${techs.size}" }
    return (techs.toList() + List(4 - techs.size) { null }).toMutableList()
}
