package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.TfrmEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

/** Writes [text] into [fieldSize] bytes, null-padding the remainder. */
private fun Buffer.writePaddedField(text: String, fieldSize: Int) {
    val start = size
    writeString(text, Charsets.US_ASCII)
    write(ByteArray((fieldSize - (size - start)).toInt()))
}

/** Builds a well-formed 112-byte TFRM item body (length prefix excluded, as with prior sections). */
private fun tfrmItemBinary(
    name: String = "Build Road",
    civilopediaEntry: String = "",
    turnsToComplete: Int = 2,
    required: Int = -1,
    requiredResource1: Int = -1,
    requiredResource2: Int = -1,
    order: String = "",
): Buffer = Buffer().apply {
    writePaddedField(name, 32)
    writePaddedField(civilopediaEntry, 32)
    writeIntLe(turnsToComplete)
    writeIntLe(required)
    writeIntLe(requiredResource1)
    writeIntLe(requiredResource2)
    writePaddedField(order, 32)
}

class TfrmEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields") {
        val entry = TfrmEntryParser.parse(tfrmItemBinary())
        entry shouldBe TfrmEntry(
            name = "Build Road",
            civilopediaEntry = "",
            turnsToComplete = 2,
            required = -1,
            requiredResource1 = -1,
            requiredResource2 = -1,
            order = "",
        )
    }
})
