package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.WmapEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString

/**
 * Builds a well-formed WMAP item body (length prefix excluded, as with prior sections): a
 * 4-byte count + that many 4-byte resource IDs, then the fixed 164-byte tail. Uses 2 resource
 * IDs (not a hardcoded larger count) to prove the read is genuinely dynamic, not hardcoded.
 */
private fun wmapItemBinary(
    resourceIds: List<Int> = listOf(3, 7),
    numberOfContinents: Int = 4,
    height: Int = 60,
    distanceBetweenCivs: Int = 6,
    numberOfCivs: Int = 7,
    unknown1: ByteString = ByteString.of(*ByteArray(8)),
    width: Int = 80,
    unknown2: ByteString = ByteString.of(*ByteArray(128)),
    mapSeed: Int = 12345,
    flags: Int = 0b101,
): Buffer = Buffer().apply {
    writeIntLe(resourceIds.size)
    resourceIds.forEach { writeIntLe(it) }
    writeIntLe(numberOfContinents)
    writeIntLe(height)
    writeIntLe(distanceBetweenCivs)
    writeIntLe(numberOfCivs)
    write(unknown1)
    writeIntLe(width)
    write(unknown2)
    writeIntLe(mapSeed)
    writeIntLe(flags)
}

class WmapEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields, including a dynamic-length resource ID list") {
        val entry = WmapEntryParser.parse(wmapItemBinary())
        entry shouldBe WmapEntry(
            resourceIds = listOf(3, 7),
            numberOfContinents = 4,
            height = 60,
            distanceBetweenCivs = 6,
            numberOfCivs = 7,
            unknown1 = ByteString.of(*ByteArray(8)),
            width = 80,
            unknown2 = ByteString.of(*ByteArray(128)),
            mapSeed = 12345,
            flags = 0b101,
        )
    }

    test("WmapEntry rejects an unknown1 field that is not exactly 8 bytes") {
        shouldThrow<IllegalArgumentException> {
            WmapEntry(emptyList(), 0, 0, 0, 0, ByteString.of(0, 0, 0), 0, ByteString.of(*ByteArray(128)), 0, 0)
        }
    }

    test("WmapEntry rejects an unknown2 field that is not exactly 128 bytes") {
        shouldThrow<IllegalArgumentException> {
            WmapEntry(emptyList(), 0, 0, 0, 0, ByteString.of(*ByteArray(8)), 0, ByteString.of(0, 0, 0), 0, 0)
        }
    }
})
