package com.kelvsyc.rifflet.civ3

/**
 * Resolves [RaceEntry.freeTech1] against [techs]. Likely a `TECH` section index — inferred from
 * the field name and the same naming convention as `CtznEntry.prerequisite`/
 * `GovtEntry.prerequisiteTechnology` (see [CtznEntry.prerequisite]'s KDoc); not confirmed by
 * either primary source.
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
 * documented by Apolyton's BIX/BIQ format reference ("King Unit... an index into the unit
 * list"), not merely a naming-based inference.
 */
fun RaceEntry.unitTypeForKingPrto(units: List<PrtoEntry>): PrtoEntry? = units.getOrNull(unitTypeForKing)

/**
 * Resolves [RaceEntry.shunnedGovernment] against [governments]. Likely a `GOVT` section index —
 * inferred from the field name; not itself documented by either primary source. Real Conquests
 * map-editor default-ruleset data supports the inference: every civilization's value is a small
 * non-negative int within the file's actual `GOVT` count, with `-1` for the Barbarian entry — the
 * same shape as every other confirmed reference field in this codebase. Same treatment as
 * [RaceEntry.freeTech1].
 */
fun RaceEntry.shunnedGovernmentGovt(governments: List<GovtEntry>): GovtEntry? = governments.getOrNull(shunnedGovernment)

/**
 * Resolves [RaceEntry.favoriteGovernment] against [governments]. Same treatment as
 * [RaceEntry.shunnedGovernment].
 */
fun RaceEntry.favoriteGovernmentGovt(governments: List<GovtEntry>): GovtEntry? = governments.getOrNull(favoriteGovernment)
