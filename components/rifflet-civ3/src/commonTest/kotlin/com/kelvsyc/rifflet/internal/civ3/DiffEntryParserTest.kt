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

/** Builds a well-formed 120-byte DIFF item body (length prefix excluded, as with WSIZ).
 * [includeTrailingField] controls whether the last value (`militaryLaw`) is written, matching
 * the real vanilla (major=4, 116 bytes) vs PTW/Conquests (120 bytes) split. */
private fun diffItemBinary(
    name: String = "Chieftain",
    values: List<Int> = (0..13).toList(),
    includeTrailingField: Boolean = true,
): Buffer = Buffer().apply {
    writePaddedField(name, 64)
    val written = if (includeTrailingField) values else values.dropLast(1)
    written.forEach { writeIntLe(it) }
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

    test("vanilla-length item (116 bytes, militaryLaw absent) defaults it to zero") {
        val entry = DiffEntryParser.parse(diffItemBinary(includeTrailingField = false))
        entry.militaryLaw shouldBe 0
    }
})
