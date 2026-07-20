package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.LeadEntry
import com.kelvsyc.rifflet.civ3.LeadStartUnit
import okio.Buffer
import okio.ByteString

/**
 * Parses one `LEAD` item, per existing reverse-engineering documentation of the BIX/BIQ format. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop. Reads its two dynamic arrays — [LeadEntry.startUnits] (a paired record
 * type, matching `GovtEntryParser`'s `relationships` read) and [LeadEntry.startingTechnologyIds]
 * (a flat `Int` list, matching `WmapEntryParser`/`CityEntryParser`) — with a preceding count
 * field each, same local-array pattern used throughout this codebase.
 *
 * Both counts are validated via [requireSaneCount] before sizing their respective lists — see
 * that function's KDoc for why.
 *
 * [LeadEntry.skipFirstTurn], [LeadEntry.unknown2], and [LeadEntry.startEmbassies] are absent from
 * [Civ3FormatEra.PTW] items and present, as a group, in every [Civ3FormatEra.CONQUESTS] item —
 * they default to zero/empty when the item ends after [LeadEntry.color].
 * [Civ3FormatEra.VANILLA]'s shape here is unconfirmed: no known real file has a `LEAD` section at
 * all in that era.
 */
internal object LeadEntryParser {
    fun parse(item: Buffer): LeadEntry {
        val customCivData = item.readIntLe()
        val humanPlayer = item.readIntLe()
        val name = item.readByteString(32L).truncateAtFirstNull()
        val unknown = item.readByteString(8L)
        val numberOfStartUnitTypes = item.requireSaneCount(item.readIntLe(), 8L, "LeadEntry.startUnits")
        val startUnits = List(numberOfStartUnitTypes) {
            val quantity = item.readIntLe()
            val unitType = item.readIntLe()
            LeadStartUnit(quantity, unitType)
        }
        val genderOfLeaderName = item.readIntLe()
        val numberOfStartingTechnologies = item.requireSaneCount(
            item.readIntLe(),
            4L,
            "LeadEntry.startingTechnologyIds",
        )
        val startingTechnologyIds = List(numberOfStartingTechnologies) { item.readIntLe() }
        val difficulty = item.readIntLe()
        val initialEra = item.readIntLe()
        val startCash = item.readIntLe()
        val government = item.readIntLe()
        val civ = item.readIntLe()
        val color = item.readIntLe()
        val skipFirstTurn = if (item.size >= 4L) item.readIntLe() else 0
        val unknown2 = if (item.size >= 4L) item.readByteString(4L) else ByteString.of(0, 0, 0, 0)
        val startEmbassies = if (item.size >= 1L) item.readByte() else 0.toByte()
        return LeadEntry(
            customCivData, humanPlayer, name, unknown, startUnits, genderOfLeaderName,
            startingTechnologyIds, difficulty, initialEra, startCash, government, civ, color,
            skipFirstTurn, unknown2, startEmbassies,
        )
    }
}
