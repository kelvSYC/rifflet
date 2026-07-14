package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.ContEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

/** Builds a well-formed 8-byte CONT item body (length prefix excluded, as with prior sections). */
private fun contItemBinary(
    type: Int = 1,
    numberOfTiles: Int = 42,
): Buffer = Buffer().apply {
    writeIntLe(type)
    writeIntLe(numberOfTiles)
}

class ContEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields") {
        val entry = ContEntryParser.parse(contItemBinary())
        entry shouldBe ContEntry(
            type = 1,
            numberOfTiles = 42,
        )
    }
})
