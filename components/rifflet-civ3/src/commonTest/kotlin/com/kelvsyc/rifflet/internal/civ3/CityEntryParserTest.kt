package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.CityEntry
import com.kelvsyc.rifflet.core.RiffletParseException
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

/**
 * Builds a well-formed CITY item body (length prefix excluded, as with prior sections). Uses
 * 2 building IDs (not a hardcoded larger count) to prove the dynamic read is genuine, not
 * hardcoded.
 */
private fun cityItemBinary(
    hasWalls: Byte = 1,
    hasPalace: Byte = 0,
    name: String = "Rome",
    ownerType: Int = 2,
    buildingIds: List<Int> = listOf(3, 7),
    culture: Int = 150,
    owner: Int = 0,
    size: Int = 4,
    x: Int = 10,
    y: Int = 20,
    cityLevel: Int = 2,
    borderLevel: Int = 1,
    useAutoName: Int = 1,
): Buffer = Buffer().apply {
    writeByte(hasWalls.toInt())
    writeByte(hasPalace.toInt())
    writePaddedField(name, 24)
    writeIntLe(ownerType)
    writeIntLe(buildingIds.size)
    buildingIds.forEach { writeIntLe(it) }
    writeIntLe(culture)
    writeIntLe(owner)
    writeIntLe(size)
    writeIntLe(x)
    writeIntLe(y)
    writeIntLe(cityLevel)
    writeIntLe(borderLevel)
    writeIntLe(useAutoName)
}

class CityEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields, including a dynamic-length building ID list") {
        val entry = CityEntryParser.parse(cityItemBinary())
        entry shouldBe CityEntry(
            hasWalls = 1,
            hasPalace = 0,
            name = "Rome",
            ownerType = 2,
            buildingIds = listOf(3, 7),
            culture = 150,
            owner = 0,
            size = 4,
            x = 10,
            y = 20,
            cityLevel = 2,
            borderLevel = 1,
            useAutoName = 1,
        )
    }

    test("an implausibly large numberOfBuildings throws RiffletParseException before attempting to allocate") {
        val buffer = Buffer().apply {
            writeByte(1) // hasWalls
            writeByte(0) // hasPalace
            write(ByteArray(24)) // name
            writeIntLe(2) // ownerType
            writeIntLe(Int.MAX_VALUE) // numberOfBuildings
        }
        shouldThrow<RiffletParseException> { CityEntryParser.parse(buffer) }
    }
})
