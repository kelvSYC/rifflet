package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.CtznEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

/** Writes [text] into [fieldSize] bytes, null-padding the remainder. */
private fun Buffer.writePaddedField(text: String, fieldSize: Int) {
    val start = size
    writeString(text, Charsets.US_ASCII)
    write(ByteArray((fieldSize - (size - start)).toInt()))
}

/** Builds a well-formed 124-byte CTZN item body (length prefix excluded, as with prior sections). */
private fun ctznItemBinary(
    defaultCitizen: Int = 1,
    singularName: String = "Entertainer",
    civilopediaEntry: String = "",
    pluralName: String = "Entertainers",
    prerequisite: Int = -1,
    luxuries: Int = 3,
    research: Int = 0,
    taxes: Int = 0,
    corruption: Int = 0,
    construction: Int = 0,
    includeTrailingFields: Boolean = true,
): Buffer = Buffer().apply {
    writeIntLe(defaultCitizen)
    writePaddedField(singularName, 32)
    writePaddedField(civilopediaEntry, 32)
    writePaddedField(pluralName, 32)
    writeIntLe(prerequisite)
    writeIntLe(luxuries)
    writeIntLe(research)
    writeIntLe(taxes)
    if (includeTrailingFields) {
        writeIntLe(corruption)
        writeIntLe(construction)
    }
}

class CtznEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields") {
        val entry = CtznEntryParser.parse(ctznItemBinary())
        entry shouldBe CtznEntry(
            defaultCitizen = 1,
            singularName = "Entertainer",
            civilopediaEntry = "",
            pluralName = "Entertainers",
            prerequisite = -1,
            luxuries = 3,
            research = 0,
            taxes = 0,
            corruption = 0,
            construction = 0,
        )
    }

    test("vanilla/PTW-length item (116 bytes, trailing fields absent) defaults them to zero") {
        val entry = CtznEntryParser.parse(ctznItemBinary(includeTrailingFields = false))
        entry.corruption shouldBe 0
        entry.construction shouldBe 0
    }
})
