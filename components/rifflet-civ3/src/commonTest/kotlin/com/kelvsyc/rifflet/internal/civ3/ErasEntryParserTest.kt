package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.ErasEntry
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

/** Builds a well-formed 264-byte ERAS item body (length prefix excluded, as with WSIZ/DIFF). */
private fun erasItemBinary(
    name: String = "Ancient",
    civilopediaEntry: String = "",
    researchers: List<String> = listOf("", "", "", "", ""),
    numberOfUsedResearcherNames: Int = 0,
    unknown: ByteArray = byteArrayOf(1, 0, 0, 0),
): Buffer = Buffer().apply {
    writePaddedField(name, 64)
    writePaddedField(civilopediaEntry, 32)
    researchers.forEach { writePaddedField(it, 32) }
    writeIntLe(numberOfUsedResearcherNames)
    write(unknown)
}

class ErasEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields") {
        val entry = ErasEntryParser.parse(erasItemBinary())
        entry shouldBe ErasEntry(
            name = "Ancient",
            civilopediaEntry = "",
            researcher1 = "",
            researcher2 = "",
            researcher3 = "",
            researcher4 = "",
            researcher5 = "",
            numberOfUsedResearcherNames = 0,
            unknown = ByteString.of(1, 0, 0, 0),
        )
    }

    test("unknown trailing field is preserved raw, not validated") {
        val entry = ErasEntryParser.parse(erasItemBinary(unknown = byteArrayOf(9, 9, 9, 9)))
        entry.unknown shouldBe ByteString.of(9, 9, 9, 9)
    }
})
