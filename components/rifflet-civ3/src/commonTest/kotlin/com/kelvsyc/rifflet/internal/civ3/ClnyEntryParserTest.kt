package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.ClnyEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

/** Builds a well-formed 20-byte CLNY item body (length prefix excluded, as with prior sections). */
private fun clnyItemBinary(
    ownerType: Int = 2,
    owner: Int = 0,
    x: Int = 5,
    y: Int = 15,
    improvementType: Int = 3,
): Buffer = Buffer().apply {
    writeIntLe(ownerType)
    writeIntLe(owner)
    writeIntLe(x)
    writeIntLe(y)
    writeIntLe(improvementType)
}

class ClnyEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields") {
        val entry = ClnyEntryParser.parse(clnyItemBinary())
        entry shouldBe ClnyEntry(
            ownerType = 2,
            owner = 0,
            x = 5,
            y = 15,
            improvementType = 3,
        )
    }
})
