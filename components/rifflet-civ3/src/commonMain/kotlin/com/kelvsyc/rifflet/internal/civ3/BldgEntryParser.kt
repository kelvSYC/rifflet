package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.BldgEntry
import okio.Buffer

/**
 * Parses one `BLDG` item, per the Apolyton BIX/BIQ format documentation cross-validated against
 * `QueryCiv3`'s grouped flags region (the two sources' byte counts for the flags region between
 * `requiredResource2` and `numberOfArmiesRequired` reconcile exactly at 16 bytes, confirming
 * `QueryCiv3`'s consolidated `Flags[16]` grouping matches Apolyton's four separate 4-byte
 * binary-flag fields for the same region). Reads directly off [item], a zero-copy-transferred
 * [Buffer] already stripped of its own length prefix by the generic section loop. Unlike most
 * recently-modeled sections, `BLDG` has no dynamic-length regions.
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
        val flavors = item.readIntLe()
        val unknown = item.readByteString(4L)
        val unitProduced = item.readIntLe()
        val unitFrequency = item.readIntLe()
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
