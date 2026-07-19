package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.BldgEntry
import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import okio.Buffer
import okio.ByteString

/**
 * Parses one `BLDG` item, per the Apolyton BIX/BIQ format documentation cross-validated against
 * `QueryCiv3`'s grouped flags region (the two sources' byte counts for the flags region between
 * `requiredResource2` and `numberOfArmiesRequired` reconcile exactly at 16 bytes, confirming
 * `QueryCiv3`'s consolidated `Flags[16]` grouping matches Apolyton's four separate 4-byte
 * binary-flag fields for the same region). Reads directly off [item], a zero-copy-transferred
 * [Buffer] already stripped of its own length prefix by the generic section loop.
 *
 * The trailing four fields (`flavors`, `unknown`, `unitProduced`, `unitFrequency`) are read
 * defensively: real [Civ3FormatEra.VANILLA] and [Civ3FormatEra.PTW] files omit them entirely
 * (confirmed by diffing real `BLDG` items byte-for-byte — the [Civ3FormatEra.VANILLA]/
 * [Civ3FormatEra.PTW] 252-byte record is an exact prefix of the [Civ3FormatEra.CONQUESTS]
 * 268-byte record), so each read checks `item.size` first and defaults when absent, matching
 * `TechEntryParser`/`UnitEntryParser`/`RuleEntryParser`'s established length-aware defensive
 * parsing pattern. PTW minor sensitivity was not separately tracked during the original
 * investigation of this section — treat the [Civ3FormatEra.PTW] shape as confirmed only in
 * aggregate, not per minor.
 */
internal object BldgEntryParser {
    fun parse(item: Buffer): BldgEntry {
        val description = item.readByteString(64L).truncateAtFirstNull()
        val name = item.readByteString(32L).truncateAtFirstNull()
        val civilopediaEntry = item.readByteString(32L).truncateAtFirstNull()
        val doublesHappiness = item.readIntLe()
        val gainInEveryCity = item.readIntLe()
        val gainInEveryCityOnContinent = item.readIntLe()
        val requiredBuilding = item.readIntLe()
        val cost = item.readIntLe()
        val culture = item.readIntLe()
        val bombardDefense = item.readIntLe()
        val navalBombardDefense = item.readIntLe()
        val defenseBonus = item.readIntLe()
        val navalDefenseBonus = item.readIntLe()
        val maintenanceCost = item.readIntLe()
        val contentFacesAllCities = item.readIntLe()
        val contentFaces = item.readIntLe()
        val unhappyFacesAllCities = item.readIntLe()
        val unhappyFaces = item.readIntLe()
        val numberOfRequiredBuildings = item.readIntLe()
        val airPower = item.readIntLe()
        val navalPower = item.readIntLe()
        val pollution = item.readIntLe()
        val production = item.readIntLe()
        val requiredGovernment = item.readIntLe()
        val spaceshipPart = item.readIntLe()
        val requiredAdvance = item.readIntLe()
        val renderedObsoleteBy = item.readIntLe()
        val requiredResource1 = item.readIntLe()
        val requiredResource2 = item.readIntLe()
        val flags = item.readByteString(16L)
        val numberOfArmiesRequired = item.readIntLe()
        val flavors = if (item.size >= 4L) item.readIntLe() else 0
        val unknown = if (item.size >= 4L) item.readByteString(4L) else ByteString.of(*ByteArray(4))
        val unitProduced = if (item.size >= 4L) item.readIntLe() else 0
        val unitFrequency = if (item.size >= 4L) item.readIntLe() else 0
        return BldgEntry(
            description,
            name,
            civilopediaEntry,
            doublesHappiness,
            gainInEveryCity,
            gainInEveryCityOnContinent,
            requiredBuilding,
            cost,
            culture,
            bombardDefense,
            navalBombardDefense,
            defenseBonus,
            navalDefenseBonus,
            maintenanceCost,
            contentFacesAllCities,
            contentFaces,
            unhappyFacesAllCities,
            unhappyFaces,
            numberOfRequiredBuildings,
            airPower,
            navalPower,
            pollution,
            production,
            requiredGovernment,
            spaceshipPart,
            requiredAdvance,
            renderedObsoleteBy,
            requiredResource1,
            requiredResource2,
            flags,
            numberOfArmiesRequired,
            flavors,
            unknown,
            unitProduced,
            unitFrequency,
        )
    }
}
