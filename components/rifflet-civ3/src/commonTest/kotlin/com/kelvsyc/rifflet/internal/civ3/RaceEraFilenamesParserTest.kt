package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.RaceEraFilenames
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

/** Writes [text] into [fieldSize] bytes, null-padding the remainder. */
private fun Buffer.writePaddedField(text: String, fieldSize: Int) {
    val start = size
    writeString(text, Charsets.US_ASCII)
    write(ByteArray((fieldSize - (size - start)).toInt()))
}

private fun eraFilenamesBinary(
    forwardFilename: String = "ancient_fwd.flc",
    reverseFilename: String = "ancient_rev.flc",
): Buffer = Buffer().apply {
    writePaddedField(forwardFilename, 260)
    writePaddedField(reverseFilename, 260)
}

class RaceEraFilenamesParserTest : FunSpec({

    test("well-formed 520-byte entry is parsed into both fields") {
        val filenames = RaceEraFilenamesParser.parse(eraFilenamesBinary())
        filenames shouldBe RaceEraFilenames(
            forwardFilename = "ancient_fwd.flc",
            reverseFilename = "ancient_rev.flc",
        )
    }
})
