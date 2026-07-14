package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.WchrEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

/** Builds a well-formed 52-byte WCHR item body (length prefix excluded, as with prior sections). */
private fun wchrItemBinary(
    selectedClimate: Int = 1,
    actualClimate: Int = 1,
    selectedBarbarianActivity: Int = 2,
    actualBarbarianActivity: Int = 2,
    selectedLandform: Int = 1,
    actualLandform: Int = 1,
    selectedOceanCoverage: Int = 0,
    actualOceanCoverage: Int = 0,
    selectedTemperature: Int = 1,
    actualTemperature: Int = 1,
    selectedAge: Int = 1,
    actualAge: Int = 1,
    worldSize: Int = 3,
): Buffer = Buffer().apply {
    writeIntLe(selectedClimate)
    writeIntLe(actualClimate)
    writeIntLe(selectedBarbarianActivity)
    writeIntLe(actualBarbarianActivity)
    writeIntLe(selectedLandform)
    writeIntLe(actualLandform)
    writeIntLe(selectedOceanCoverage)
    writeIntLe(actualOceanCoverage)
    writeIntLe(selectedTemperature)
    writeIntLe(actualTemperature)
    writeIntLe(selectedAge)
    writeIntLe(actualAge)
    writeIntLe(worldSize)
}

class WchrEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields") {
        val entry = WchrEntryParser.parse(wchrItemBinary())
        entry shouldBe WchrEntry(
            selectedClimate = 1,
            actualClimate = 1,
            selectedBarbarianActivity = 2,
            actualBarbarianActivity = 2,
            selectedLandform = 1,
            actualLandform = 1,
            selectedOceanCoverage = 0,
            actualOceanCoverage = 0,
            selectedTemperature = 1,
            actualTemperature = 1,
            selectedAge = 1,
            actualAge = 1,
            worldSize = 3,
        )
    }
})
