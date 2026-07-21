package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.TileEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString

/**
 * Builds a TILE item body (length prefix excluded, as with prior sections). [tier] controls how
 * many of the three confirmed format-era tiers are written: 1 = vanilla (22 bytes), 2 = PTW
 * (29 bytes), 3 = Conquests (45 bytes, default) — matching the confirmed real-file byte-prefix
 * relationship (each tier is an exact prefix of the next), so a lower tier is simply a truncated
 * write, not a differently-ordered one. Uses distinct, non-default values throughout to prove
 * genuine field-order correctness, matching `BldgEntryParserTest`'s established precedent (no
 * dynamic-array landmarks in this section to otherwise catch ordering bugs).
 *
 * [includeVictoryPointAndRuin] additionally controls whether `victoryPointLocation`/`ruin` are
 * written when `tier >= 2` — set to `false` to build the real major=3/4 vanilla-revision shape
 * (23 bytes: `unknown2` present, but `victoryPointLocation`/`ruin` genuinely absent until PTW).
 *
 * [includeUnknown6] additionally controls whether `unknown6` is written when `tier >= 3` — set to
 * `false` (the default tier=3 shape) for the ordinary 45-byte Conquests item; `true` builds the
 * real 49-byte Conquests sub-tier confirmed in exactly 2 of 21 sampled real Conquests files.
 */
private fun tileItemBinary(
    tier: Int = 3,
    includeVictoryPointAndRuin: Boolean = true,
    includeUnknown6: Boolean = false,
): Buffer = Buffer().apply {
    writeByte(0b00001111) // riverConnections
    writeByte(1) // border
    writeIntLe(3) // resource
    writeByte(42) // textureLocation
    writeByte(5) // textureFile
    write(byteArrayOf(0x11, 0x22)) // unknown
    writeByte(2) // overlayFlags
    writeByte(0x21) // terrain: base=1, overlay=2 packed nibbles
    writeByte(4) // bonusFlags
    writeByte(0) // riverCrossingFlags
    writeShortLe(7) // barbarianTribe
    writeShortLe(2) // city (first short in the item; confirmed swapped vs. the field's own name
    // — see TileEntryParser's KDoc)
    writeShortLe(9) // colony (second short in the item; see above)
    writeShortLe(1) // continent
    if (tier >= 2) {
        write(byteArrayOf(0x33)) // unknown2
        if (includeVictoryPointAndRuin) {
            writeShortLe(-1) // victoryPointLocation
            writeIntLe(100) // ruin
        }
    }
    if (tier >= 3) {
        write(byteArrayOf(0x01, 0x02, 0x03, 0x04)) // c3cOverlays
        write(byteArrayOf(0x44)) // unknown3
        writeByte(0x21) // c3cTerrain
        write(byteArrayOf(0x55, 0x66)) // unknown4
        writeShortLe(3) // fogOfWar
        write(byteArrayOf(0x07, 0x08, 0x09, 0x0A)) // c3cBonuses
        write(byteArrayOf(0x77, 0x12)) // unknown5
        if (includeUnknown6) {
            write(byteArrayOf(0x13, 0x14, 0x15, 0x16)) // unknown6
        }
    }
}

class TileEntryParserTest : FunSpec({

    test("vanilla-length item (22 bytes, PTW/Conquests fields absent) defaults them to zero") {
        val entry = TileEntryParser.parse(tileItemBinary(tier = 1))
        entry shouldBe TileEntry(
            riverConnections = 0b00001111,
            border = 1,
            resource = 3,
            textureLocation = 42,
            textureFile = 5,
            unknown = ByteString.of(0x11, 0x22),
            overlayFlags = 2,
            terrain = 0x21,
            bonusFlags = 4,
            riverCrossingFlags = 0,
            barbarianTribe = 7,
            colony = 9,
            city = 2,
            continent = 1,
            unknown2 = ByteString.of(0),
            victoryPointLocation = 0,
            ruin = 0,
            c3cOverlays = ByteString.of(0, 0, 0, 0),
            unknown3 = ByteString.of(0),
            c3cTerrain = 0,
            unknown4 = ByteString.of(0, 0),
            fogOfWar = 0,
            c3cBonuses = ByteString.of(0, 0, 0, 0),
            unknown5 = ByteString.of(0, 0),
            unknown6 = ByteString.of(0, 0, 0, 0),
        )
    }

    test("major=3/4-length item (23 bytes, unknown2 present but victoryPointLocation/ruin absent) parses unknown2 and defaults the rest") {
        val entry = TileEntryParser.parse(tileItemBinary(tier = 2, includeVictoryPointAndRuin = false))
        entry.unknown2 shouldBe ByteString.of(0x33)
        entry.victoryPointLocation shouldBe 0
        entry.ruin shouldBe 0
        entry.c3cOverlays shouldBe ByteString.of(0, 0, 0, 0)
    }

    test("PTW-length item (29 bytes, Conquests fields absent) defaults them to zero") {
        val entry = TileEntryParser.parse(tileItemBinary(tier = 2))
        entry shouldBe TileEntry(
            riverConnections = 0b00001111,
            border = 1,
            resource = 3,
            textureLocation = 42,
            textureFile = 5,
            unknown = ByteString.of(0x11, 0x22),
            overlayFlags = 2,
            terrain = 0x21,
            bonusFlags = 4,
            riverCrossingFlags = 0,
            barbarianTribe = 7,
            colony = 9,
            city = 2,
            continent = 1,
            unknown2 = ByteString.of(0x33),
            victoryPointLocation = -1,
            ruin = 100,
            c3cOverlays = ByteString.of(0, 0, 0, 0),
            unknown3 = ByteString.of(0),
            c3cTerrain = 0,
            unknown4 = ByteString.of(0, 0),
            fogOfWar = 0,
            c3cBonuses = ByteString.of(0, 0, 0, 0),
            unknown5 = ByteString.of(0, 0),
            unknown6 = ByteString.of(0, 0, 0, 0),
        )
    }

    test("Conquests-length item (45 bytes, all fields present) is parsed into all fields") {
        val entry = TileEntryParser.parse(tileItemBinary(tier = 3))
        entry shouldBe TileEntry(
            riverConnections = 0b00001111,
            border = 1,
            resource = 3,
            textureLocation = 42,
            textureFile = 5,
            unknown = ByteString.of(0x11, 0x22),
            overlayFlags = 2,
            terrain = 0x21,
            bonusFlags = 4,
            riverCrossingFlags = 0,
            barbarianTribe = 7,
            colony = 9,
            city = 2,
            continent = 1,
            unknown2 = ByteString.of(0x33),
            victoryPointLocation = -1,
            ruin = 100,
            c3cOverlays = ByteString.of(0x01, 0x02, 0x03, 0x04),
            unknown3 = ByteString.of(0x44),
            c3cTerrain = 0x21,
            unknown4 = ByteString.of(0x55, 0x66),
            fogOfWar = 3,
            c3cBonuses = ByteString.of(0x07, 0x08, 0x09, 0x0A),
            unknown5 = ByteString.of(0x77, 0x12),
            unknown6 = ByteString.of(0, 0, 0, 0),
        )
    }

    test("Conquests-sub-tier-length item (49 bytes, unknown6 present) is parsed into all fields") {
        val entry = TileEntryParser.parse(tileItemBinary(tier = 3, includeUnknown6 = true))
        entry shouldBe TileEntry(
            riverConnections = 0b00001111,
            border = 1,
            resource = 3,
            textureLocation = 42,
            textureFile = 5,
            unknown = ByteString.of(0x11, 0x22),
            overlayFlags = 2,
            terrain = 0x21,
            bonusFlags = 4,
            riverCrossingFlags = 0,
            barbarianTribe = 7,
            colony = 9,
            city = 2,
            continent = 1,
            unknown2 = ByteString.of(0x33),
            victoryPointLocation = -1,
            ruin = 100,
            c3cOverlays = ByteString.of(0x01, 0x02, 0x03, 0x04),
            unknown3 = ByteString.of(0x44),
            c3cTerrain = 0x21,
            unknown4 = ByteString.of(0x55, 0x66),
            fogOfWar = 3,
            c3cBonuses = ByteString.of(0x07, 0x08, 0x09, 0x0A),
            unknown5 = ByteString.of(0x77, 0x12),
            unknown6 = ByteString.of(0x13, 0x14, 0x15, 0x16),
        )
    }

    test("TileEntry rejects an unknown field that is not exactly 2 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedTileEntry(unknown = ByteString.of(0))
        }
    }

    test("TileEntry rejects an unknown2 field that is not exactly 1 byte") {
        shouldThrow<IllegalArgumentException> {
            wellFormedTileEntry(unknown2 = ByteString.of(0, 0))
        }
    }

    test("TileEntry rejects a c3cOverlays field that is not exactly 4 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedTileEntry(c3cOverlays = ByteString.of(0, 0))
        }
    }

    test("TileEntry rejects an unknown3 field that is not exactly 1 byte") {
        shouldThrow<IllegalArgumentException> {
            wellFormedTileEntry(unknown3 = ByteString.of(0, 0))
        }
    }

    test("TileEntry rejects an unknown4 field that is not exactly 2 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedTileEntry(unknown4 = ByteString.of(0))
        }
    }

    test("TileEntry rejects a c3cBonuses field that is not exactly 4 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedTileEntry(c3cBonuses = ByteString.of(0, 0))
        }
    }

    test("TileEntry rejects an unknown5 field that is not exactly 2 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedTileEntry(unknown5 = ByteString.of(0))
        }
    }

    test("TileEntry rejects an unknown6 field that is not exactly 4 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedTileEntry(unknown6 = ByteString.of(0, 0))
        }
    }
})

/** Builds a well-formed [TileEntry] with all-zero/empty values, for domain-invariant tests that
 * only care about overriding one field. */
private fun wellFormedTileEntry(
    unknown: ByteString = ByteString.of(0, 0),
    unknown2: ByteString = ByteString.of(0),
    c3cOverlays: ByteString = ByteString.of(0, 0, 0, 0),
    unknown3: ByteString = ByteString.of(0),
    unknown4: ByteString = ByteString.of(0, 0),
    c3cBonuses: ByteString = ByteString.of(0, 0, 0, 0),
    unknown5: ByteString = ByteString.of(0, 0),
    unknown6: ByteString = ByteString.of(0, 0, 0, 0),
): TileEntry = TileEntry(
    riverConnections = 0, border = 0, resource = 0, textureLocation = 0, textureFile = 0,
    unknown = unknown,
    overlayFlags = 0, terrain = 0, bonusFlags = 0, riverCrossingFlags = 0,
    barbarianTribe = 0, colony = 0, city = 0, continent = 0,
    unknown2 = unknown2,
    victoryPointLocation = 0, ruin = 0,
    c3cOverlays = c3cOverlays,
    unknown3 = unknown3,
    c3cTerrain = 0,
    unknown4 = unknown4,
    fogOfWar = 0,
    c3cBonuses = c3cBonuses,
    unknown5 = unknown5,
    unknown6 = unknown6,
)
