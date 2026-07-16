package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.TechEntry
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
 * Builds a well-formed 112-byte TECH item body (length prefix excluded, as with prior
 * sections) — the trailing `unknown` field present.
 */
private fun techItemBinary(
    name: String = "Bronze Working",
    civilopediaEntry: String = "",
    cost: Int = 20,
    era: Int = 1,
    advanceIcon: Int = 5,
    x: Int = 10,
    y: Int = 20,
    prerequisite1: Int = -1,
    prerequisite2: Int = -1,
    prerequisite3: Int = -1,
    prerequisite4: Int = -1,
    flags: Int = 0b10000001,
    flavors: Int = 0,
    unknown: ByteString = ByteString.of(0, 0, 0, 0),
): Buffer = Buffer().apply {
    writePaddedField(name, 32)
    writePaddedField(civilopediaEntry, 32)
    writeIntLe(cost)
    writeIntLe(era)
    writeIntLe(advanceIcon)
    writeIntLe(x)
    writeIntLe(y)
    writeIntLe(prerequisite1)
    writeIntLe(prerequisite2)
    writeIntLe(prerequisite3)
    writeIntLe(prerequisite4)
    writeIntLe(flags)
    writeIntLe(flavors)
    write(unknown)
}

/**
 * Builds a short 108-byte TECH item body with no trailing `unknown` bytes at all — modeling
 * the Apolyton-documented variant of the format that omits this field entirely (see Global
 * Constraints).
 */
private fun shortTechItemBinary(
    name: String = "Bronze Working",
    civilopediaEntry: String = "",
    cost: Int = 20,
    era: Int = 1,
    advanceIcon: Int = 5,
    x: Int = 10,
    y: Int = 20,
    prerequisite1: Int = -1,
    prerequisite2: Int = -1,
    prerequisite3: Int = -1,
    prerequisite4: Int = -1,
    flags: Int = 0b10000001,
    flavors: Int = 0,
): Buffer = Buffer().apply {
    writePaddedField(name, 32)
    writePaddedField(civilopediaEntry, 32)
    writeIntLe(cost)
    writeIntLe(era)
    writeIntLe(advanceIcon)
    writeIntLe(x)
    writeIntLe(y)
    writeIntLe(prerequisite1)
    writeIntLe(prerequisite2)
    writeIntLe(prerequisite3)
    writeIntLe(prerequisite4)
    writeIntLe(flags)
    writeIntLe(flavors)
}

class TechEntryParserTest : FunSpec({

    test("well-formed 112-byte item is parsed into all fields") {
        val entry = TechEntryParser.parse(techItemBinary())
        entry shouldBe TechEntry(
            name = "Bronze Working",
            civilopediaEntry = "",
            cost = 20,
            era = 1,
            advanceIcon = 5,
            x = 10,
            y = 20,
            prerequisite1 = -1,
            prerequisite2 = -1,
            prerequisite3 = -1,
            prerequisite4 = -1,
            flags = 0b10000001,
            flavors = 0,
            unknown = ByteString.of(0, 0, 0, 0),
        )
    }

    test("well-formed 108-byte item with no trailing unknown bytes defaults unknown to zero bytes") {
        val entry = TechEntryParser.parse(shortTechItemBinary())
        entry.unknown shouldBe ByteString.of(0, 0, 0, 0)
        entry.flavors shouldBe 0
    }
})
