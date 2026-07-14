package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.UnitEntry
import okio.Buffer

/**
 * Parses one `UNIT` item, per the Apolyton BIX/BIQ format documentation. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop.
 *
 * Unlike every other `EntryParser` in this codebase, this one checks [item]'s remaining size
 * before reading its two trailing fields (`ptwName`, `useCivilizationKing`): a vanilla-era file
 * predating the PTW expansion may declare a shorter per-item length that omits them entirely,
 * not merely leave them blank. Because the generic section loop in `Civ3RootParserImpl` already
 * slices [item] to the file's own declared length, `item.size` reliably reflects how many bytes
 * actually remain for this specific file — no special-casing at the section-loop level is
 * needed, unlike `FLAV`.
 */
internal object UnitEntryParser {
    fun parse(item: Buffer): UnitEntry {
        val legacyName = item.readByteString(32L).truncateAtFirstNull()
        val ownerType = item.readIntLe()
        val experienceLevel = item.readIntLe()
        val owner = item.readIntLe()
        val unitType = item.readIntLe()
        val aiStrategy = item.readIntLe()
        val x = item.readIntLe()
        val y = item.readIntLe()
        val ptwName = if (item.size >= 57L) item.readByteString(57L).truncateAtFirstNull() else ""
        val useCivilizationKing = if (item.size >= 4L) item.readIntLe() else 0
        return UnitEntry(
            legacyName,
            ownerType,
            experienceLevel,
            owner,
            unitType,
            aiStrategy,
            x,
            y,
            ptwName,
            useCivilizationKing,
        )
    }
}
