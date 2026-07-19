package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.TerrEntry
import com.kelvsyc.rifflet.core.RiffletParseException
import okio.Buffer
import okio.ByteString

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
 *
 * [TerrEntry.impassable] through [TerrEntry.diseaseStrength] (21 fields) form a three-tier
 * cutoff, confirmed via byte-count algebra across all real `TERR` items in a mounted install:
 * vanilla items end right after [TerrEntry.allowColonies] (none of the 21 present, zero
 * anomalies across all sampled vanilla items); PTW items include the six boolean flags
 * [TerrEntry.impassable] through [TerrEntry.allowRadarTowers] only (zero anomalies across all
 * sampled PTW items); Conquests items include all 21, including the landmark system and
 * [TerrEntry.diseaseStrength] — both new Conquests features (zero anomalies across all sampled
 * Conquests items). Each field is guarded independently, not nested, since PTW reads six fields
 * and then stops.
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
        val impassable = if (item.size >= 1L) item.readByte() else 0.toByte()
        val impassableByWheeled = if (item.size >= 1L) item.readByte() else 0.toByte()
        val allowAirfields = if (item.size >= 1L) item.readByte() else 0.toByte()
        val allowForts = if (item.size >= 1L) item.readByte() else 0.toByte()
        val allowOutposts = if (item.size >= 1L) item.readByte() else 0.toByte()
        val allowRadarTowers = if (item.size >= 1L) item.readByte() else 0.toByte()
        val unknown = if (item.size >= 4L) item.readByteString(4L) else ByteString.of(0, 0, 0, 0)
        val landmarkEnabled = if (item.size >= 1L) item.readByte() else 0.toByte()
        val landmarkFood = if (item.size >= 4L) item.readIntLe() else 0
        val landmarkShields = if (item.size >= 4L) item.readIntLe() else 0
        val landmarkCommerce = if (item.size >= 4L) item.readIntLe() else 0
        val landmarkIrrigationBonus = if (item.size >= 4L) item.readIntLe() else 0
        val landmarkMiningBonus = if (item.size >= 4L) item.readIntLe() else 0
        val landmarkRoadBonus = if (item.size >= 4L) item.readIntLe() else 0
        val landmarkMovementBonus = if (item.size >= 4L) item.readIntLe() else 0
        val landmarkDefensiveBonus = if (item.size >= 4L) item.readIntLe() else 0
        val landmarkName = if (item.size >= 32L) item.readByteString(32L).truncateAtFirstNull() else ""
        val landmarkCivilopediaEntry = if (item.size >= 32L) item.readByteString(32L).truncateAtFirstNull() else ""
        val unknown2 = if (item.size >= 4L) item.readByteString(4L) else ByteString.of(0, 0, 0, 0)
        val terrainFlags = if (item.size >= 4L) item.readIntLe() else 0
        val diseaseStrength = if (item.size >= 4L) item.readIntLe() else 0
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
