package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.CityEntry
import okio.Buffer

/**
 * Parses one `CITY` item, per the Apolyton BIX/BIQ format documentation (the `name` field is 24
 * bytes, resolving a discrepancy between `QueryCiv3`'s `City.cs` struct declaration — 24 bytes —
 * and its own buggy 30-byte `Name` accessor; Apolyton's text confirms 24 bytes). Reads directly
 * off [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop.
 *
 * `numberOfBuildings` is validated via [requireSaneCount] before sizing
 * [CityEntry.buildingIds] — see that function's KDoc for why.
 */
internal object CityEntryParser {
    fun parse(item: Buffer): CityEntry {
        val hasWalls = item.readByte()
        val hasPalace = item.readByte()
        val name = item.readByteString(24L).truncateAtFirstNull()
        val ownerType = item.readIntLe()
        val numberOfBuildings = item.requireSaneCount(item.readIntLe(), 4L, "CityEntry.buildingIds")
        val buildingIds = List(numberOfBuildings) { item.readIntLe() }
        val culture = item.readIntLe()
        val owner = item.readIntLe()
        val size = item.readIntLe()
        val x = item.readIntLe()
        val y = item.readIntLe()
        val cityLevel = item.readIntLe()
        val borderLevel = item.readIntLe()
        val useAutoName = item.readIntLe()
        return CityEntry(
            hasWalls,
            hasPalace,
            name,
            ownerType,
            buildingIds,
            culture,
            owner,
            size,
            x,
            y,
            cityLevel,
            borderLevel,
            useAutoName,
        )
    }
}
