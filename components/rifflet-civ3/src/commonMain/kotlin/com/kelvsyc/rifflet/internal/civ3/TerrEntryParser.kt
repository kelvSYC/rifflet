package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.TerrAllowances
import com.kelvsyc.rifflet.civ3.TerrEntry
import com.kelvsyc.rifflet.civ3.TerrLandmark
import com.kelvsyc.rifflet.civ3.TerrTerraformBonuses
import com.kelvsyc.rifflet.civ3.TerrTileValues
import com.kelvsyc.rifflet.core.RiffletParseException
import okio.Buffer
import okio.ByteString

/**
 * Parses one `TERR` item, per existing reverse-engineering documentation of the BIX/BIQ format,
 * which reconciles exactly with a separate reverse-engineered reference implementation's struct
 * field-by-field. Reads directly off `item`, a
 * zero-copy-transferred [Buffer] already stripped of its own length prefix by the generic
 * section loop.
 *
 * [TerrEntry.possibleResources] is this codebase's first dynamically-sized opaque `ByteString`:
 * its length is computed from the preceding [TerrEntry.numberOfPossibleResources] count via
 * ceiling division to bytes (`(numberOfPossibleResources + 7) / 8`), rather than being a fixed
 * constant or a count-prefixed element list. The ceiling division is computed in `Long`
 * arithmetic specifically to avoid `Int` overflow when [TerrEntry.numberOfPossibleResources] is
 * within 7 of `Int.MAX_VALUE` (an `Int + Int` addition there would wrap to a large negative
 * number before the division ever ran). The result is validated against `item`'s actual
 * remaining size via [okio.BufferedSource.request] — the same technique [requireSaneCount] uses
 * — before [TerrEntry.possibleResources] is read, since this field's ceiling-division shape
 * doesn't fit `requireSaneCount`'s `count * minBytesPerElement` model directly.
 *
 * [TerrEntry.allowances]' 6 defensively-read members ([TerrAllowances.impassable] through
 * [TerrAllowances.allowRadarTowers]) and [TerrEntry.landmark]'s entire 11-field group form a
 * staggered two-tier cutoff: [Civ3FormatEra.VANILLA] items end right after
 * [TerrAllowances.allowColonies] (neither tier present, so all 6 [TerrAllowances] defensive
 * members are `null` and [TerrEntry.landmark] is `null`); [Civ3FormatEra.PTW] items include the 6
 * [TerrAllowances] defensive members only (only `minor=18` [Civ3FormatEra.PTW] files are
 * confirmed to include a `TERR` section, so other PTW minors' shape here is unconfirmed);
 * [Civ3FormatEra.CONQUESTS] items include both tiers, including [TerrEntry.landmark] and
 * [TerrEntry.diseaseStrength] — both new Conquests features. Each field is guarded
 * independently, not nested, since [Civ3FormatEra.PTW] reads the 6 flags and then stops.
 * [TerrEntry.landmark]'s 11 members are each read into their own nullable local first, then
 * assembled into one [TerrLandmark] only if all 11 are non-`null` — the same pattern
 * `GameEntryParser` uses for `GameLockedAlliance`/`GamePlagueSettings`.
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
        val impassable = if (item.size >= 1L) item.readByte() else null
        val impassableByWheeled = if (item.size >= 1L) item.readByte() else null
        val allowAirfields = if (item.size >= 1L) item.readByte() else null
        val allowForts = if (item.size >= 1L) item.readByte() else null
        val allowOutposts = if (item.size >= 1L) item.readByte() else null
        val allowRadarTowers = if (item.size >= 1L) item.readByte() else null
        val unknown = if (item.size >= 4L) item.readByteString(4L) else ByteString.of(0, 0, 0, 0)
        val landmarkEnabled = if (item.size >= 1L) item.readByte() else null
        val landmarkFood = if (item.size >= 4L) item.readIntLe() else null
        val landmarkShields = if (item.size >= 4L) item.readIntLe() else null
        val landmarkCommerce = if (item.size >= 4L) item.readIntLe() else null
        val landmarkIrrigationBonus = if (item.size >= 4L) item.readIntLe() else null
        val landmarkMiningBonus = if (item.size >= 4L) item.readIntLe() else null
        val landmarkRoadBonus = if (item.size >= 4L) item.readIntLe() else null
        val landmarkMovementBonus = if (item.size >= 4L) item.readIntLe() else null
        val landmarkDefensiveBonus = if (item.size >= 4L) item.readIntLe() else null
        val landmarkName = if (item.size >= 32L) item.readByteString(32L).truncateAtFirstNull() else null
        val landmarkCivilopediaEntry = if (item.size >= 32L) item.readByteString(32L).truncateAtFirstNull() else null
        val unknown2 = if (item.size >= 4L) item.readByteString(4L) else ByteString.of(0, 0, 0, 0)
        val terrainFlags = if (item.size >= 4L) item.readIntLe() else 0
        val diseaseStrength = if (item.size >= 4L) item.readIntLe() else 0
        val landmark = if (
            landmarkEnabled != null && landmarkFood != null && landmarkShields != null &&
            landmarkCommerce != null && landmarkIrrigationBonus != null && landmarkMiningBonus != null &&
            landmarkRoadBonus != null && landmarkMovementBonus != null && landmarkDefensiveBonus != null &&
            landmarkName != null && landmarkCivilopediaEntry != null
        ) {
            TerrLandmark(
                landmarkEnabled = landmarkEnabled,
                tileValues = TerrTileValues(
                    food = landmarkFood,
                    shields = landmarkShields,
                    commerce = landmarkCommerce,
                ),
                terraformBonuses = TerrTerraformBonuses(
                    irrigationBonus = landmarkIrrigationBonus,
                    miningBonus = landmarkMiningBonus,
                    roadBonus = landmarkRoadBonus,
                ),
                landmarkMovementBonus = landmarkMovementBonus,
                landmarkDefensiveBonus = landmarkDefensiveBonus,
                landmarkName = landmarkName,
                landmarkCivilopediaEntry = landmarkCivilopediaEntry,
            )
        } else {
            null
        }
        return TerrEntry(
            numberOfPossibleResources,
            possibleResources,
            name,
            civilopediaEntry,
            TerrTerraformBonuses(irrigationBonus, miningBonus, roadBonus),
            defenseBonus,
            movementCost,
            TerrTileValues(food, shields, commerce),
            workerJobAllowed,
            pollutionEffect,
            TerrAllowances(
                allowCities,
                allowColonies,
                impassable,
                impassableByWheeled,
                allowAirfields,
                allowForts,
                allowOutposts,
                allowRadarTowers,
            ),
            unknown,
            landmark,
            unknown2,
            terrainFlags,
            diseaseStrength,
        )
    }
}
