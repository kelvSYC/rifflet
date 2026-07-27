package com.kelvsyc.rifflet.civ3

/**
 * Resolves [RaceEntry.freeTech1] against [techs].
 */
fun RaceEntry.freeTech1Tech(techs: List<TechEntry>): TechEntry? = techs.getOrNull(freeTech1)

/**
 * Resolves [RaceEntry.freeTech2] against [techs]. Same treatment as [RaceEntry.freeTech1].
 */
fun RaceEntry.freeTech2Tech(techs: List<TechEntry>): TechEntry? = techs.getOrNull(freeTech2)

/**
 * Resolves [RaceEntry.freeTech3] against [techs]. Same treatment as [RaceEntry.freeTech1].
 */
fun RaceEntry.freeTech3Tech(techs: List<TechEntry>): TechEntry? = techs.getOrNull(freeTech3)

/**
 * Resolves [RaceEntry.freeTech4] against [techs]. Same treatment as [RaceEntry.freeTech1].
 */
fun RaceEntry.freeTech4Tech(techs: List<TechEntry>): TechEntry? = techs.getOrNull(freeTech4)

/**
 * Resolves [RaceEntry.unitTypeForKing] against [units]. A `PRTO` section index — explicitly
 * documented by existing reverse-engineering work ("King Unit... an index into the unit
 * list"), not merely a naming-based inference.
 */
fun RaceEntry.unitTypeForKingPrto(units: List<PrtoEntry>): PrtoEntry? = units.getOrNull(unitTypeForKing)

/**
 * Resolves [RaceEntry.shunnedGovernment] against [governments].
 */
fun RaceEntry.shunnedGovernmentGovt(governments: List<GovtEntry>): GovtEntry? = governments.getOrNull(shunnedGovernment)

/**
 * Resolves [RaceEntry.favoriteGovernment] against [governments]. Same treatment as
 * [RaceEntry.shunnedGovernment].
 */
fun RaceEntry.favoriteGovernmentGovt(governments: List<GovtEntry>): GovtEntry? = governments.getOrNull(favoriteGovernment)

/**
 * The 6 values of [RaceEntry.cultureGroup], per the Civilizations editor tab's "Culture Group"
 * dropdown. Ordinal position matches the raw file value offset by 1 (raw `-1` is [NONE], raw `0`
 * is [AMERICAN], etc.) — do not reorder these constants. Confirmed by the Conquests base
 * ruleset's civilizations, whose values group by real-world region exactly (e.g. America/Aztecs/
 * Iroquois/Inca/Maya all [AMERICAN]; Germany/Russia/France/England/Spain all [EUROPEAN]).
 *
 * [NONE] is only ever used by the barbarian placeholder in every real file checked, but the
 * Rules Editor doesn't enforce that restriction — nothing stops a real civilization from being
 * assigned it too.
 */
enum class RaceCultureGroup { NONE, AMERICAN, EUROPEAN, MEDITERRANEAN, MID_EAST, ASIAN }

/**
 * Decodes [RaceEntry.cultureGroup] into [RaceCultureGroup], or `null` if the raw value is outside
 * the documented -1..4 range.
 */
val RaceEntry.cultureGroupEnum: RaceCultureGroup?
    get() = RaceCultureGroup.entries.getOrNull(cultureGroup + 1)
