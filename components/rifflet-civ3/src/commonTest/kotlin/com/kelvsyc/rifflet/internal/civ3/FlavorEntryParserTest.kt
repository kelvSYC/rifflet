package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.FlavorEntry
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
 * Builds a well-formed single FLAV-inner-flavor item body (no length prefix, matching the file
 * format — see `FlavGroupEntryParserTest` for the outer nesting level this feeds into): 4-byte
 * unknown + 256-byte name + 4-byte count + count * 4-byte relations. Uses 3 relations (not the
 * real file's typical 7) to prove the read is genuinely dynamic, not hardcoded.
 */
private fun flavorItemBinary(
    unknown: ByteString = ByteString.of(0, 0, 0, 0),
    name: String = "Military",
    relations: List<Int> = listOf(10, -5, 3),
): Buffer = Buffer().apply {
    write(unknown)
    writePaddedField(name, 256)
    writeIntLe(relations.size)
    relations.forEach { writeIntLe(it) }
}

class FlavorEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields, including a dynamic-length relations list") {
        val entry = FlavorEntryParser.parse(flavorItemBinary())
        entry shouldBe FlavorEntry(
            unknown = ByteString.of(0, 0, 0, 0),
            name = "Military",
            relations = listOf(10, -5, 3),
        )
    }

    test("FlavorEntry rejects an unknown field that is not exactly 4 bytes") {
        shouldThrow<IllegalArgumentException> {
            FlavorEntry(ByteString.of(0, 0, 0), "Military", listOf(1, 2, 3))
        }
    }
})
