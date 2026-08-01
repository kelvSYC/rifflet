package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3EnumDecodeException
import com.kelvsyc.rifflet.civ3.GoodEntry
import com.kelvsyc.rifflet.civ3.GoodResourceType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

/** Writes [text] into [fieldSize] bytes, null-padding the remainder. */
private fun Buffer.writePaddedField(text: String, fieldSize: Int) {
    val start = size
    writeString(text, Charsets.US_ASCII)
    write(ByteArray((fieldSize - (size - start)).toInt()))
}

/** Builds a well-formed 88-byte GOOD item body (length prefix excluded, as with prior sections). */
private fun goodItemBinary(
    name: String = "Wine",
    civilopediaEntry: String = "",
    type: Int = 1,
    appearanceRatio: Int = 50,
    disappearanceProbability: Int = 0,
    icon: Int = 12,
    prerequisite: Int = -1,
    foodBonus: Int = 0,
    shieldsBonus: Int = 0,
    commerceBonus: Int = 3,
): Buffer = Buffer().apply {
    writePaddedField(name, 24)
    writePaddedField(civilopediaEntry, 32)
    writeIntLe(type)  // Note: accepts Int for direct test control; parsed to GoodResourceType in the real parser
    writeIntLe(appearanceRatio)
    writeIntLe(disappearanceProbability)
    writeIntLe(icon)
    writeIntLe(prerequisite)
    writeIntLe(foodBonus)
    writeIntLe(shieldsBonus)
    writeIntLe(commerceBonus)
}

class GoodEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields") {
        val entry = GoodEntryParser.parse(goodItemBinary())
        entry shouldBe GoodEntry(
            name = "Wine",
            civilopediaEntry = "",
            type = GoodResourceType.LUXURY,
            appearanceRatio = 50,
            disappearanceProbability = 0,
            icon = 12,
            prerequisite = -1,
            foodBonus = 0,
            shieldsBonus = 0,
            commerceBonus = 3,
        )
    }

    test("type decodes each raw value to the matching GoodResourceType") {
        GoodResourceType.entries.forEachIndexed { raw, expected ->
            val item = goodItemBinary(type = raw)
            GoodEntryParser.parse(item).type shouldBe expected
        }
    }

    test("an out-of-range type throws Civ3EnumDecodeException") {
        val item = goodItemBinary(type = 3)
        val e = shouldThrow<Civ3EnumDecodeException> { GoodEntryParser.parse(item) }
        e.field shouldBe "GoodEntry.type"
        e.rawValue shouldBe 3
    }
})
