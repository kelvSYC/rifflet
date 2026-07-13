package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.CultEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

/** Writes [text] into [fieldSize] bytes, null-padding the remainder. */
private fun Buffer.writePaddedField(text: String, fieldSize: Int) {
    val start = size
    writeString(text, Charsets.US_ASCII)
    write(ByteArray((fieldSize - (size - start)).toInt()))
}

/** Builds a well-formed 88-byte CULT item body (length prefix excluded, as with prior sections). */
private fun cultItemBinary(
    name: String = "Legendary",
    chanceOfSuccessfulPropaganda: Int = 10,
    cultureRatioPercentage: Int = 300,
    cultureRatioDenominator: Int = 1,
    cultureRatioNumerator: Int = 3,
    initialResistanceChance: Int = 50,
    continuedResistanceChance: Int = 25,
): Buffer = Buffer().apply {
    writePaddedField(name, 64)
    writeIntLe(chanceOfSuccessfulPropaganda)
    writeIntLe(cultureRatioPercentage)
    writeIntLe(cultureRatioDenominator)
    writeIntLe(cultureRatioNumerator)
    writeIntLe(initialResistanceChance)
    writeIntLe(continuedResistanceChance)
}

class CultEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields") {
        val entry = CultEntryParser.parse(cultItemBinary())
        entry shouldBe CultEntry(
            name = "Legendary",
            chanceOfSuccessfulPropaganda = 10,
            cultureRatioPercentage = 300,
            cultureRatioDenominator = 1,
            cultureRatioNumerator = 3,
            initialResistanceChance = 50,
            continuedResistanceChance = 25,
        )
    }
})
