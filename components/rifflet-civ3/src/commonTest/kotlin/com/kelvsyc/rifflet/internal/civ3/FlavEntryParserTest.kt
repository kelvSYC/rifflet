package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.FlavEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString

/** Writes [text] into [fieldSize] bytes, null-padding the remainder. */
private fun Buffer.writePaddedField(text: String, fieldSize: Int) {
    val start = size
    writeString(text, Charsets.US_ASCII)
    write(ByteArray((fieldSize - (size - start)).toInt()))
}

/**
 * Builds a well-formed FLAV item body (no length prefix, unlike every other section — see
 * Global Constraints): 4-byte unknown + 256-byte name + 4-byte count + count * 4-byte relations.
 * Uses 3 flavor relationships (not the in-practice-always-7 count) to prove the read is
 * genuinely dynamic, not hardcoded.
 */
private fun flavItemBinary(
    unknown: ByteString = ByteString.of(0, 0, 0, 0),
    name: String = "Military",
    flavorRelationships: List<Int> = listOf(10, -5, 3),
): Buffer = Buffer().apply {
    write(unknown)
    writePaddedField(name, 256)
    writeIntLe(flavorRelationships.size)
    flavorRelationships.forEach { writeIntLe(it) }
}

class FlavEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields, including a dynamic-length relationships list") {
        val entry = FlavEntryParser.parse(flavItemBinary())
        entry shouldBe FlavEntry(
            unknown = ByteString.of(0, 0, 0, 0),
            name = "Military",
            flavorRelationships = listOf(10, -5, 3),
        )
    }

    test("FlavEntry rejects an unknown field that is not exactly 4 bytes") {
        shouldThrow<IllegalArgumentException> {
            FlavEntry(ByteString.of(0, 0, 0), "Military", listOf(1, 2, 3))
        }
    }
})
