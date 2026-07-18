package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.TerrEntry
import com.kelvsyc.rifflet.core.RiffletParseException
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
 * constant or a count-prefixed element list. The ceiling division is computed in `Long`
 * arithmetic specifically to avoid `Int` overflow when [TerrEntry.numberOfPossibleResources] is
 * within 7 of `Int.MAX_VALUE` (an `Int + Int` addition there would wrap to a large negative
 * number before the division ever ran). The result is validated against [item]'s actual
 * remaining size via [okio.BufferedSource.request] — the same technique [requireSaneCount] uses
 * — before [TerrEntry.possibleResources] is read, since this field's ceiling-division shape
 * doesn't fit `requireSaneCount`'s `count * minBytesPerElement` model directly.
 */
internal object TerrEntryParser {
    fun parse(item: Buffer): TerrEntry {
        val numberOfPossibleResources = item.readIntLe()
        val possibleResourcesLength = (numberOfPossibleResources.toLong() + 7) / 8
        if (possibleResourcesLength < 0 || !item.request(possibleResourcesLength)) {
            throw RiffletParseException(
                "TerrEntry.possibleResources requires $possibleResourcesLength bytes, but insufficient data remains",
            )
        }
        val possibleResources = item.readByteString(possibleResourcesLength)
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
