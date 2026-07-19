package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.LeadEntry
import com.kelvsyc.rifflet.civ3.LeadStartUnit
import com.kelvsyc.rifflet.core.RiffletParseException
import io.kotest.assertions.throwables.shouldThrow
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
 * Builds a well-formed LEAD item body (length prefix excluded, as with prior sections). Uses
 * small (2-element) dynamic-array sizes to prove both dynamic reads are genuine, not hardcoded.
 */
private fun leadItemBinary(
    customCivData: Int = 1,
    humanPlayer: Int = 1,
    name: String = "Caesar",
    unknown: ByteString = ByteString.of(*ByteArray(8)),
    startUnits: List<LeadStartUnit> = listOf(LeadStartUnit(2, 5), LeadStartUnit(1, 8)),
    genderOfLeaderName: Int = 0,
    startingTechnologyIds: List<Int> = listOf(3, 7),
    difficulty: Int = 2,
    initialEra: Int = 0,
    startCash: Int = 50,
    government: Int = 0,
    civ: Int = 0,
    color: Int = 1,
    skipFirstTurn: Int = 0,
    unknown2: ByteString = ByteString.of(*ByteArray(4)),
    startEmbassies: Byte = 1,
    includeTrailingFields: Boolean = true,
): Buffer = Buffer().apply {
    writeIntLe(customCivData)
    writeIntLe(humanPlayer)
    writePaddedField(name, 32)
    write(unknown)
    writeIntLe(startUnits.size)
    startUnits.forEach {
        writeIntLe(it.quantity)
        writeIntLe(it.unitType)
    }
    writeIntLe(genderOfLeaderName)
    writeIntLe(startingTechnologyIds.size)
    startingTechnologyIds.forEach { writeIntLe(it) }
    writeIntLe(difficulty)
    writeIntLe(initialEra)
    writeIntLe(startCash)
    writeIntLe(government)
    writeIntLe(civ)
    writeIntLe(color)
    if (includeTrailingFields) {
        writeIntLe(skipFirstTurn)
        write(unknown2)
        writeByte(startEmbassies.toInt())
    }
}

class LeadEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields, including two dynamic-length lists") {
        val entry = LeadEntryParser.parse(leadItemBinary())
        entry shouldBe LeadEntry(
            customCivData = 1,
            humanPlayer = 1,
            name = "Caesar",
            unknown = ByteString.of(*ByteArray(8)),
            startUnits = listOf(LeadStartUnit(2, 5), LeadStartUnit(1, 8)),
            genderOfLeaderName = 0,
            startingTechnologyIds = listOf(3, 7),
            difficulty = 2,
            initialEra = 0,
            startCash = 50,
            government = 0,
            civ = 0,
            color = 1,
            skipFirstTurn = 0,
            unknown2 = ByteString.of(*ByteArray(4)),
            startEmbassies = 1,
        )
    }

    test("item missing skipFirstTurn/unknown2/startEmbassies defaults them to zero/empty") {
        val entry = LeadEntryParser.parse(leadItemBinary(includeTrailingFields = false))
        entry.skipFirstTurn shouldBe 0
        entry.unknown2 shouldBe ByteString.of(0, 0, 0, 0)
        entry.startEmbassies shouldBe 0
    }

    test("LeadEntry rejects an unknown field that is not exactly 8 bytes") {
        shouldThrow<IllegalArgumentException> {
            LeadEntry(
                0, 0, "", ByteString.of(0, 0, 0), emptyList(), 0, emptyList(), 0, 0, 0, 0, 0, 0, 0,
                ByteString.of(*ByteArray(4)), 0,
            )
        }
    }

    test("LeadEntry rejects an unknown2 field that is not exactly 4 bytes") {
        shouldThrow<IllegalArgumentException> {
            LeadEntry(
                0, 0, "", ByteString.of(*ByteArray(8)), emptyList(), 0, emptyList(), 0, 0, 0, 0, 0, 0, 0,
                ByteString.of(0, 0, 0), 0,
            )
        }
    }

    test("an implausibly large numberOfStartUnitTypes throws RiffletParseException before attempting to allocate") {
        val buffer = Buffer().apply {
            writeIntLe(1) // customCivData
            writeIntLe(1) // humanPlayer
            write(ByteArray(32)) // name
            write(ByteArray(8)) // unknown
            writeIntLe(Int.MAX_VALUE) // numberOfStartUnitTypes
        }
        shouldThrow<RiffletParseException> { LeadEntryParser.parse(buffer) }
    }

    test("an implausibly large numberOfStartingTechnologies throws RiffletParseException before attempting to allocate") {
        val buffer = Buffer().apply {
            writeIntLe(1) // customCivData
            writeIntLe(1) // humanPlayer
            write(ByteArray(32)) // name
            write(ByteArray(8)) // unknown
            writeIntLe(0) // numberOfStartUnitTypes
            writeIntLe(0) // genderOfLeaderName
            writeIntLe(Int.MAX_VALUE) // numberOfStartingTechnologies
        }
        shouldThrow<RiffletParseException> { LeadEntryParser.parse(buffer) }
    }
})
