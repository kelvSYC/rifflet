package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3EnumDecodeException
import com.kelvsyc.rifflet.civ3.ContEntry
import com.kelvsyc.rifflet.civ3.ContType
import io.kotest.assertions.throwables.shouldThrow
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
            type = ContType.LAND,
            numberOfTiles = 42,
        )
    }

    test("type decodes each raw value to the matching ContType") {
        ContType.entries.forEachIndexed { raw, expected ->
            val item = contItemBinary(type = raw)
            ContEntryParser.parse(item).type shouldBe expected
        }
    }

    test("an out-of-range type throws Civ3EnumDecodeException") {
        val item = contItemBinary(type = 2)
        val e = shouldThrow<Civ3EnumDecodeException> { ContEntryParser.parse(item) }
        e.field shouldBe "ContEntry.type"
        e.rawValue shouldBe 2
    }
})
