package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.TerrEntry
import okio.Buffer

/**
 * Parses one `TERR` item, per the Apolyton BIX/BIQ format documentation, which reconciles
 * exactly with `QueryCiv3`'s struct field-by-field (a first for this project — every other
 * section needed at least one cross-source reconciliation). Reads directly off [item], a
 * zero-copy-transferred [Buffer] already stripped of its own length prefix by the generic
 * section loop.
 *
 * [TerrEntry.possibleResources] is this codebase's first dynamically-sized opaque `ByteString`:
 * its length is computed from the preceding [TerrEntry.numberOfPossibleResources] count via
 * ceiling division to bytes (`(numberOfPossibleResources + 7) / 8`), rather than being a fixed
 * constant or a count-prefixed element list.
 */
internal object TerrEntryParser {
    fun parse(item: Buffer): TerrEntry {
        val numberOfPossibleResources = item.readIntLe()
        val possibleResourcesLength = (numberOfPossibleResources + 7) / 8
        val possibleResources = item.readByteString(possibleResourcesLength.toLong())
        val name = item.readByteString(32L).truncateAtFirstNull()
        val civilopediaEntry = item.readByteString(32L).truncateAtFirstNull()
        val irrigationBonus = item.readIntLe()
        val miningBonus = item.readIntLe()
        val roadBonus = item.readIntLe()
        val defenseBonus = item.readIntLe()
        val movementCost = item.readIntLe()
        val food = item.readIntLe()
        val shields = item.readIntLe()
        val commerce = item.readIntLe()
        val workerJobAllowed = item.readIntLe()
        val pollutionEffect = item.readIntLe()
        val allowCities = item.readByte()
        val allowColonies = item.readByte()
        val impassable = item.readByte()
        val impassableByWheeled = item.readByte()
        val allowAirfields = item.readByte()
        val allowForts = item.readByte()
        val allowOutposts = item.readByte()
        val allowRadarTowers = item.readByte()
        val unknown = item.readByteString(4L)
        val landmarkEnabled = item.readByte()
        val landmarkFood = item.readIntLe()
        val landmarkShields = item.readIntLe()
        val landmarkCommerce = item.readIntLe()
        val landmarkIrrigationBonus = item.readIntLe()
        val landmarkMiningBonus = item.readIntLe()
        val landmarkRoadBonus = item.readIntLe()
        val landmarkMovementBonus = item.readIntLe()
        val landmarkDefensiveBonus = item.readIntLe()
        val landmarkName = item.readByteString(32L).truncateAtFirstNull()
        val landmarkCivilopediaEntry = item.readByteString(32L).truncateAtFirstNull()
        val unknown2 = item.readByteString(4L)
        val terrainFlags = item.readIntLe()
        val diseaseStrength = item.readIntLe()
        return TerrEntry(
            numberOfPossibleResources,
            possibleResources,
            name,
            civilopediaEntry,
            irrigationBonus,
            miningBonus,
            roadBonus,
            defenseBonus,
            movementCost,
            food,
            shields,
            commerce,
            workerJobAllowed,
            pollutionEffect,
            allowCities,
            allowColonies,
            impassable,
            impassableByWheeled,
            allowAirfields,
            allowForts,
            allowOutposts,
            allowRadarTowers,
            unknown,
            landmarkEnabled,
            landmarkFood,
            landmarkShields,
            landmarkCommerce,
            landmarkIrrigationBonus,
            landmarkMiningBonus,
            landmarkRoadBonus,
            landmarkMovementBonus,
            landmarkDefensiveBonus,
            landmarkName,
            landmarkCivilopediaEntry,
            unknown2,
            terrainFlags,
            diseaseStrength,
        )
    }
}
