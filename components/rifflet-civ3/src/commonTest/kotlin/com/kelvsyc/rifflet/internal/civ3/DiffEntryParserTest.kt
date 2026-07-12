package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.DiffEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

/** Writes [text] into [fieldSize] bytes, null-padding the remainder. */
private fun Buffer.writePaddedField(text: String, fieldSize: Int) {
    val start = size
    writeString(text, Charsets.US_ASCII)
    write(ByteArray((fieldSize - (size - start)).toInt()))
}

/** Builds a well-formed 120-byte DIFF item body (length prefix excluded, as with WSIZ). */
private fun diffItemBinary(name: String = "Chieftain", values: List<Int> = (0..13).toList()): Buffer = Buffer().apply {
    writePaddedField(name, 64)
    values.forEach { writeIntLe(it) }
}

class DiffEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields in declared order") {
        val entry = DiffEntryParser.parse(diffItemBinary())
        entry shouldBe DiffEntry(
            name = "Chieftain",
            numberOfCitizensBornContent = 0,
            maxGovernmentTransitionTime = 1,
            numberOfAiDefensiveStartingUnits = 2,
            numberOfAiOffensiveStartingUnits = 3,
            extraStartUnit1 = 4,
            extraStartUnit2 = 5,
            additionalFreeSupport = 6,
            unitSupportBonusForEachSettlement = 7,
            attackBonusAgainstBarbarians = 8,
            costFactor = 9,
            percentageOfOptimalCities = 10,
            aiToAiTradeRate = 11,
            corruptionPercentage = 12,
            militaryLaw = 13,
        )
    }
})
