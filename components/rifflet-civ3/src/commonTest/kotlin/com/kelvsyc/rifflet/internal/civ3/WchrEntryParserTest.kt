package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Age
import com.kelvsyc.rifflet.civ3.BarbarianActivity
import com.kelvsyc.rifflet.civ3.Civ3EnumDecodeException
import com.kelvsyc.rifflet.civ3.Climate
import com.kelvsyc.rifflet.civ3.Landform
import com.kelvsyc.rifflet.civ3.OceanCoverage
import com.kelvsyc.rifflet.civ3.Temperature
import com.kelvsyc.rifflet.civ3.WchrEntry
import io.kotest.assertions.throwables.shouldThrow
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
            selectedClimate = Climate.NORMAL,
            actualClimate = Climate.NORMAL,
            selectedBarbarianActivity = BarbarianActivity.ROAMING,
            actualBarbarianActivity = BarbarianActivity.ROAMING,
            selectedLandform = Landform.CONTINENTS,
            actualLandform = Landform.CONTINENTS,
            selectedOceanCoverage = OceanCoverage.EIGHTY_PERCENT,
            actualOceanCoverage = OceanCoverage.EIGHTY_PERCENT,
            selectedTemperature = Temperature.TEMPERATE,
            actualTemperature = Temperature.TEMPERATE,
            selectedAge = Age.FOUR_BILLION_YEARS,
            actualAge = Age.FOUR_BILLION_YEARS,
            worldSize = 3,
        )
    }

    test("selectedBarbarianActivity decodes each raw value (-1..5) to the matching BarbarianActivity") {
        BarbarianActivity.entries.forEachIndexed { ordinal, expected ->
            val item = wchrItemBinary(selectedBarbarianActivity = ordinal - 1)
            WchrEntryParser.parse(item).selectedBarbarianActivity shouldBe expected
        }
    }

    test("actualBarbarianActivity decodes each raw value (-1..5) to the matching BarbarianActivity") {
        BarbarianActivity.entries.forEachIndexed { ordinal, expected ->
            val item = wchrItemBinary(actualBarbarianActivity = ordinal - 1)
            WchrEntryParser.parse(item).actualBarbarianActivity shouldBe expected
        }
    }

    test("an out-of-range selectedBarbarianActivity throws Civ3EnumDecodeException") {
        val item = wchrItemBinary(selectedBarbarianActivity = 6)
        val e = shouldThrow<Civ3EnumDecodeException> { WchrEntryParser.parse(item) }
        e.field shouldBe "WchrEntry.selectedBarbarianActivity"
        e.rawValue shouldBe 6
    }

    test("an out-of-range actualBarbarianActivity throws Civ3EnumDecodeException") {
        val item = wchrItemBinary(actualBarbarianActivity = 6)
        val e = shouldThrow<Civ3EnumDecodeException> { WchrEntryParser.parse(item) }
        e.field shouldBe "WchrEntry.actualBarbarianActivity"
        e.rawValue shouldBe 6
    }

    test("selectedClimate decodes each raw value to the matching Climate") {
        Climate.entries.forEachIndexed { raw, expected ->
            val item = wchrItemBinary(selectedClimate = raw)
            WchrEntryParser.parse(item).selectedClimate shouldBe expected
        }
    }

    test("actualClimate decodes each raw value to the matching Climate") {
        Climate.entries.forEachIndexed { raw, expected ->
            val item = wchrItemBinary(actualClimate = raw)
            WchrEntryParser.parse(item).actualClimate shouldBe expected
        }
    }

    test("an out-of-range selectedClimate throws Civ3EnumDecodeException") {
        val item = wchrItemBinary(selectedClimate = 4)
        val e = shouldThrow<Civ3EnumDecodeException> { WchrEntryParser.parse(item) }
        e.field shouldBe "WchrEntry.selectedClimate"
        e.rawValue shouldBe 4
    }

    test("an out-of-range actualClimate throws Civ3EnumDecodeException") {
        val item = wchrItemBinary(actualClimate = 4)
        val e = shouldThrow<Civ3EnumDecodeException> { WchrEntryParser.parse(item) }
        e.field shouldBe "WchrEntry.actualClimate"
        e.rawValue shouldBe 4
    }

    test("selectedLandform decodes each raw value to the matching Landform") {
        Landform.entries.forEachIndexed { raw, expected ->
            val item = wchrItemBinary(selectedLandform = raw)
            WchrEntryParser.parse(item).selectedLandform shouldBe expected
        }
    }

    test("actualLandform decodes each raw value to the matching Landform") {
        Landform.entries.forEachIndexed { raw, expected ->
            val item = wchrItemBinary(actualLandform = raw)
            WchrEntryParser.parse(item).actualLandform shouldBe expected
        }
    }

    test("an out-of-range selectedLandform throws Civ3EnumDecodeException") {
        val item = wchrItemBinary(selectedLandform = 4)
        val e = shouldThrow<Civ3EnumDecodeException> { WchrEntryParser.parse(item) }
        e.field shouldBe "WchrEntry.selectedLandform"
        e.rawValue shouldBe 4
    }

    test("an out-of-range actualLandform throws Civ3EnumDecodeException") {
        val item = wchrItemBinary(actualLandform = 4)
        val e = shouldThrow<Civ3EnumDecodeException> { WchrEntryParser.parse(item) }
        e.field shouldBe "WchrEntry.actualLandform"
        e.rawValue shouldBe 4
    }

    test("selectedOceanCoverage decodes each raw value to the matching OceanCoverage") {
        OceanCoverage.entries.forEachIndexed { raw, expected ->
            val item = wchrItemBinary(selectedOceanCoverage = raw)
            WchrEntryParser.parse(item).selectedOceanCoverage shouldBe expected
        }
    }

    test("actualOceanCoverage decodes each raw value to the matching OceanCoverage") {
        OceanCoverage.entries.forEachIndexed { raw, expected ->
            val item = wchrItemBinary(actualOceanCoverage = raw)
            WchrEntryParser.parse(item).actualOceanCoverage shouldBe expected
        }
    }

    test("an out-of-range selectedOceanCoverage throws Civ3EnumDecodeException") {
        val item = wchrItemBinary(selectedOceanCoverage = 4)
        val e = shouldThrow<Civ3EnumDecodeException> { WchrEntryParser.parse(item) }
        e.field shouldBe "WchrEntry.selectedOceanCoverage"
        e.rawValue shouldBe 4
    }

    test("an out-of-range actualOceanCoverage throws Civ3EnumDecodeException") {
        val item = wchrItemBinary(actualOceanCoverage = 4)
        val e = shouldThrow<Civ3EnumDecodeException> { WchrEntryParser.parse(item) }
        e.field shouldBe "WchrEntry.actualOceanCoverage"
        e.rawValue shouldBe 4
    }

    test("selectedTemperature decodes each raw value to the matching Temperature") {
        Temperature.entries.forEachIndexed { raw, expected ->
            val item = wchrItemBinary(selectedTemperature = raw)
            WchrEntryParser.parse(item).selectedTemperature shouldBe expected
        }
    }

    test("actualTemperature decodes each raw value to the matching Temperature") {
        Temperature.entries.forEachIndexed { raw, expected ->
            val item = wchrItemBinary(actualTemperature = raw)
            WchrEntryParser.parse(item).actualTemperature shouldBe expected
        }
    }

    test("an out-of-range selectedTemperature throws Civ3EnumDecodeException") {
        val item = wchrItemBinary(selectedTemperature = 4)
        val e = shouldThrow<Civ3EnumDecodeException> { WchrEntryParser.parse(item) }
        e.field shouldBe "WchrEntry.selectedTemperature"
        e.rawValue shouldBe 4
    }

    test("an out-of-range actualTemperature throws Civ3EnumDecodeException") {
        val item = wchrItemBinary(actualTemperature = 4)
        val e = shouldThrow<Civ3EnumDecodeException> { WchrEntryParser.parse(item) }
        e.field shouldBe "WchrEntry.actualTemperature"
        e.rawValue shouldBe 4
    }

    test("selectedAge decodes each raw value to the matching Age") {
        Age.entries.forEachIndexed { raw, expected ->
            val item = wchrItemBinary(selectedAge = raw)
            WchrEntryParser.parse(item).selectedAge shouldBe expected
        }
    }

    test("actualAge decodes each raw value to the matching Age") {
        Age.entries.forEachIndexed { raw, expected ->
            val item = wchrItemBinary(actualAge = raw)
            WchrEntryParser.parse(item).actualAge shouldBe expected
        }
    }

    test("an out-of-range selectedAge throws Civ3EnumDecodeException") {
        val item = wchrItemBinary(selectedAge = 4)
        val e = shouldThrow<Civ3EnumDecodeException> { WchrEntryParser.parse(item) }
        e.field shouldBe "WchrEntry.selectedAge"
        e.rawValue shouldBe 4
    }

    test("an out-of-range actualAge throws Civ3EnumDecodeException") {
        val item = wchrItemBinary(actualAge = 4)
        val e = shouldThrow<Civ3EnumDecodeException> { WchrEntryParser.parse(item) }
        e.field shouldBe "WchrEntry.actualAge"
        e.rawValue shouldBe 4
    }
})
