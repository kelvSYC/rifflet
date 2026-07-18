package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.TileEntry
import okio.Buffer
import okio.ByteString

/**
 * Parses one `TILE` item, per `QueryCiv3`'s struct, confirmed against real files of all three
 * Civ3 format eras. Reads directly off [item], a zero-copy-transferred [Buffer] already stripped
 * of its own length prefix by the generic section loop.
 *
 * The real version ladder has (at least) four steps by exact header `major` value — major=2 (22
 * bytes), major=3/4 (23 bytes, +`unknown2`), major=11/PTW (29 bytes, +`victoryPointLocation`/
 * `ruin`), major=12/Conquests (45 bytes, +7 more fields) — but the code only needs three
 * independent guards, since `unknown2`/`victoryPointLocation`/`ruin` are each checked against
 * `item.size` on their own, not as a combined tier. The same length-aware defensive parsing
 * pattern is already used by `BldgEntryParser`/`CtznEntryParser`/`DiffEntryParser`/
 * `ErasEntryParser`/`TechEntryParser`/`UnitEntryParser`/`RuleEntryParser`.
 */
internal object TileEntryParser {
    fun parse(item: Buffer): TileEntry {
        val riverConnections = item.readByte()
        val border = item.readByte()
        val resource = item.readIntLe()
        val textureLocation = item.readByte()
        val textureFile = item.readByte()
        val unknown = item.readByteString(2L)
        val overlayFlags = item.readByte()
        val terrain = item.readByte()
        val bonusFlags = item.readByte()
        val riverCrossingFlags = item.readByte()
        val barbarianTribe = item.readShortLe()
        val colony = item.readShortLe()
        val city = item.readShortLe()
        val continent = item.readShortLe()
        val unknown2 = if (item.size >= 1L) item.readByteString(1L) else ByteString.of(0)
        val victoryPointLocation = if (item.size >= 2L) item.readShortLe() else 0.toShort()
        val ruin = if (item.size >= 4L) item.readIntLe() else 0
        val c3cOverlays = if (item.size >= 4L) item.readByteString(4L) else ByteString.of(0, 0, 0, 0)
        val unknown3 = if (item.size >= 1L) item.readByteString(1L) else ByteString.of(0)
        val c3cTerrain = if (item.size >= 1L) item.readByte() else 0.toByte()
        val unknown4 = if (item.size >= 2L) item.readByteString(2L) else ByteString.of(0, 0)
        val fogOfWar = if (item.size >= 2L) item.readShortLe() else 0.toShort()
        val c3cBonuses = if (item.size >= 4L) item.readByteString(4L) else ByteString.of(0, 0, 0, 0)
        val unknown5 = if (item.size >= 2L) item.readByteString(2L) else ByteString.of(0, 0)
        return TileEntry(
            riverConnections,
            border,
            resource,
            textureLocation,
            textureFile,
            unknown,
            overlayFlags,
            terrain,
            bonusFlags,
            riverCrossingFlags,
            barbarianTribe,
            colony,
            city,
            continent,
            unknown2,
            victoryPointLocation,
            ruin,
            c3cOverlays,
            unknown3,
            c3cTerrain,
            unknown4,
            fogOfWar,
            c3cBonuses,
            unknown5,
        )
    }
}
