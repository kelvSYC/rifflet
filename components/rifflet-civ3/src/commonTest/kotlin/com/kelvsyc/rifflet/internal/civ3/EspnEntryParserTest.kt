package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.EspnEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

/** Writes [text] into [fieldSize] bytes, null-padding the remainder. */
private fun Buffer.writePaddedField(text: String, fieldSize: Int) {
    val start = size
    writeString(text, Charsets.US_ASCII)
    write(ByteArray((fieldSize - (size - start)).toInt()))
}

/** Builds a well-formed 232-byte ESPN item body (length prefix excluded, as with prior sections). */
private fun espnItemBinary(
    description: String = "Steal Technology",
    name: String = "Steal Tech",
    civilopediaEntry: String = "",
    missionFlags: Int = 0b10,
    baseCost: Int = 100,
): Buffer = Buffer().apply {
    writePaddedField(description, 128)
    writePaddedField(name, 64)
    writePaddedField(civilopediaEntry, 32)
    writeIntLe(missionFlags)
    writeIntLe(baseCost)
}

class EspnEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields") {
        val entry = EspnEntryParser.parse(espnItemBinary())
        entry shouldBe EspnEntry(
            description = "Steal Technology",
            name = "Steal Tech",
            civilopediaEntry = "",
            missionFlags = 0b10,
            baseCost = 100,
        )
    }
})
