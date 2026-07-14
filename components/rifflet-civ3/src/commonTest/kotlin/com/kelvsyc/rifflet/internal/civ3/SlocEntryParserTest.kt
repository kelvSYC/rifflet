package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.SlocEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

/** Builds a well-formed 16-byte SLOC item body (length prefix excluded, as with prior sections). */
private fun slocItemBinary(
    ownerType: Int = 2,
    owner: Int = 0,
    x: Int = 10,
    y: Int = 20,
): Buffer = Buffer().apply {
    writeIntLe(ownerType)
    writeIntLe(owner)
    writeIntLe(x)
    writeIntLe(y)
}

class SlocEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields") {
        val entry = SlocEntryParser.parse(slocItemBinary())
        entry shouldBe SlocEntry(
            ownerType = 2,
            owner = 0,
            x = 10,
            y = 20,
        )
    }
})
