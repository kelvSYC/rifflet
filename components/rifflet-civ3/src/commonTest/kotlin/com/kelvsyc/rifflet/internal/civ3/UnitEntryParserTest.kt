package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.UnitEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

/** Writes [text] into [fieldSize] bytes, null-padding the remainder. */
private fun Buffer.writePaddedField(text: String, fieldSize: Int) {
    val start = size
    writeString(text, Charsets.US_ASCII)
    write(ByteArray((fieldSize - (size - start)).toInt()))
}

/**
 * Builds a well-formed 121-byte UNIT item body (length prefix excluded, as with prior
 * sections) — both name regions present.
 */
private fun unitItemBinary(
    legacyName: String = "Phalanx",
    ownerType: Int = 2,
    experienceLevel: Int = 0,
    owner: Int = 0,
    unitType: Int = 5,
    aiStrategy: Int = 0,
    x: Int = 10,
    y: Int = 20,
    ptwName: String = "Legion",
    useCivilizationKing: Int = 0,
): Buffer = Buffer().apply {
    writePaddedField(legacyName, 32)
    writeIntLe(ownerType)
    writeIntLe(experienceLevel)
    writeIntLe(owner)
    writeIntLe(unitType)
    writeIntLe(aiStrategy)
    writeIntLe(x)
    writeIntLe(y)
    writePaddedField(ptwName, 57)
    writeIntLe(useCivilizationKing)
}

/**
 * Builds a short ~60-byte UNIT item body with no `ptwName`/`useCivilizationKing` bytes at
 * all — modeling a hypothetical vanilla-era item that predates the PTW expansion (see Global
 * Constraints).
 */
private fun shortUnitItemBinary(
    legacyName: String = "OldName",
    ownerType: Int = 2,
    experienceLevel: Int = 0,
    owner: Int = 0,
    unitType: Int = 5,
    aiStrategy: Int = 0,
    x: Int = 10,
    y: Int = 20,
): Buffer = Buffer().apply {
    writePaddedField(legacyName, 32)
    writeIntLe(ownerType)
    writeIntLe(experienceLevel)
    writeIntLe(owner)
    writeIntLe(unitType)
    writeIntLe(aiStrategy)
    writeIntLe(x)
    writeIntLe(y)
}

class UnitEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields") {
        val entry = UnitEntryParser.parse(unitItemBinary())
        entry shouldBe UnitEntry(
            legacyName = "Phalanx",
            ownerType = 2,
            experienceLevel = 0,
            owner = 0,
            unitType = 5,
            aiStrategy = 0,
            x = 10,
            y = 20,
            ptwName = "Legion",
            useCivilizationKing = 0,
        )
    }

    test("name accessor prefers ptwName when present and non-blank") {
        val entry = UnitEntryParser.parse(unitItemBinary(legacyName = "OldName", ptwName = "NewName"))
        entry.name shouldBe "NewName"
    }

    test("name accessor falls back to legacyName when ptwName is present but blank") {
        val entry = UnitEntryParser.parse(unitItemBinary(legacyName = "OldName", ptwName = ""))
        entry.ptwName shouldBe ""
        entry.name shouldBe "OldName"
    }

    test("well-formed item with no PTW-era fields at all parses with defaults and falls back to legacyName") {
        val entry = UnitEntryParser.parse(shortUnitItemBinary())
        entry.ptwName shouldBe ""
        entry.useCivilizationKing shouldBe 0
        entry.name shouldBe "OldName"
    }
})
