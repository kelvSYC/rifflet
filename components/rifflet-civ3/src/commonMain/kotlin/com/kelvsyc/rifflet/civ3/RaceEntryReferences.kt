package com.kelvsyc.rifflet.civ3

/**
 * Resolves each of [RaceEntry.freeTechs]' 4 slots against [techs], preserving position (a `null`
 * at index *n* means slot *n*'s raw value doesn't resolve, typically because it's the "no free
 * tech" sentinel `-1`).
 */
fun RaceEntry.freeTechsTech(techs: List<TechEntry>): List<TechEntry?> = freeTechs.map { techs.getOrNull(it) }

/**
 * Resolves [RaceEntry.unitTypeForKing] against [units]. A `PRTO` section index, per existing
 * reverse-engineering documentation of the BIX/BIQ format ("King Unit... an index into the unit
 * list").
 */
fun RaceEntry.unitTypeForKingPrto(units: List<PrtoEntry>): PrtoEntry? = units.getOrNull(unitTypeForKing)

/**
 * Resolves [RacePersonality.shunnedGovernment] against [governments].
 */
fun RaceEntry.shunnedGovernmentGovt(governments: List<GovtEntry>): GovtEntry? =
    governments.getOrNull(personality.shunnedGovernment)

/**
 * Resolves [RacePersonality.favoriteGovernment] against [governments]. Same treatment as
 * [RaceEntry.shunnedGovernmentGovt].
 */
fun RaceEntry.favoriteGovernmentGovt(governments: List<GovtEntry>): GovtEntry? =
    governments.getOrNull(personality.favoriteGovernment)

/**
 * The 6 values of [RaceEntry.cultureGroup], per the Civilizations editor tab's "Culture Group"
 * dropdown.
 *
 * Ordinal position matches the raw file value offset by 1 (raw `-1` is [NONE], raw `0` is
 * [AMERICAN], etc.) — do not reorder these constants. Civilizations group by real-world region
 * (e.g. America/Aztecs/Iroquois/Inca/Maya are all [AMERICAN]; Germany/Russia/France/England/Spain
 * are all [EUROPEAN]).
 *
 * In practice, [NONE] is only ever used by the barbarian placeholder — but the Rules Editor
 * doesn't enforce that restriction, so nothing stops a real civilization from being assigned it
 * too.
 */
enum class RaceCultureGroup { NONE, AMERICAN, EUROPEAN, MEDITERRANEAN, MID_EAST, ASIAN }

/**
 * The 2 values of [RaceLeader.gender] and [RaceEntry.civilizationGender], per each field's own
 * "Gender" radio group in the Civilizations editor tab. Ordinal position matches the raw file
 * value directly — do not reorder these constants.
 */
enum class Gender { MALE, FEMALE }
