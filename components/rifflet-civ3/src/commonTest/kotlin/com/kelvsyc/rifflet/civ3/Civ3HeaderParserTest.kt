package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.internal.civ3.Civ3HeaderParser
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

/** Builds a valid VER# section: 4-byte marker + 728-byte header record (732 bytes total). */
private fun verSectionBinary(
    marker: String = "VER#",
    headerCount: Int = 1,
    headerLength: Int = 720,
    major: Int = 12,
    minor: Int = 7,
    description: String = "A test scenario",
    title: String = "Test Scenario",
): Buffer = Buffer().apply {
    writeString(marker, Charsets.US_ASCII)
    writeIntLe(headerCount)
    writeIntLe(headerLength)
    writeIntLe(0)
    writeIntLe(0)
    writeIntLe(major)
    writeIntLe(minor)
    writePaddedField(description, 640)
    writePaddedField(title, 64)
}

class Civ3HeaderParserTest : FunSpec({

    test("well-formed VER# section is parsed into major, minor, description, and title") {
        val source = verSectionBinary()
        val header = Civ3HeaderParser.parse(source)
        header.major shouldBe 12
        header.minor shouldBe 7
        header.description shouldBe "A test scenario"
        header.title shouldBe "Test Scenario"
        source.exhausted() shouldBe true
    }

    test("wrong marker throws RiffletParseException") {
        val source = verSectionBinary(marker = "XXXX")
        shouldThrow<RiffletParseException> { Civ3HeaderParser.parse(source) }
    }

    test("header count other than one throws RiffletParseException") {
        val source = verSectionBinary(headerCount = 2)
        shouldThrow<RiffletParseException> { Civ3HeaderParser.parse(source) }
    }

    test("header length other than 720 throws RiffletParseException") {
        val source = verSectionBinary(headerLength = 100)
        shouldThrow<RiffletParseException> { Civ3HeaderParser.parse(source) }
    }
})
