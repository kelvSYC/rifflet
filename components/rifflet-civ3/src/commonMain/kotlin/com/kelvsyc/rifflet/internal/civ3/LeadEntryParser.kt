package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.LeadEntry
import com.kelvsyc.rifflet.civ3.LeadStartUnit
import okio.Buffer

/**
 * Parses one `LEAD` item, per the Apolyton BIX/BIQ format documentation. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop. Reads its two dynamic arrays — [LeadEntry.startUnits] (a paired record
 * type, matching `GovtEntryParser`'s `relationships` read) and [LeadEntry.startingTechnologyIds]
 * (a flat `Int` list, matching `WmapEntryParser`/`CityEntryParser`) — with a preceding count
 * field each, same local-array pattern used throughout this codebase.
 *
 * Both counts are validated via [requireSaneCount] before sizing their respective lists — see
 * that function's KDoc for why.
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
        val skipFirstTurn = item.readIntLe()
        val unknown2 = item.readByteString(4L)
        val startEmbassies = item.readByte()
        return LeadEntry(
            customCivData,
            humanPlayer,
            name,
            unknown,
            startUnits,
            genderOfLeaderName,
            startingTechnologyIds,
            difficulty,
            initialEra,
            startCash,
            government,
            civ,
            color,
            skipFirstTurn,
            unknown2,
            startEmbassies,
        )
    }
}
