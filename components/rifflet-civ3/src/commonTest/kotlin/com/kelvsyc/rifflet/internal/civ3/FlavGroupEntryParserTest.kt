package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.FlavGroupEntry
import com.kelvsyc.rifflet.civ3.FlavorEntry
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
 * Builds a well-formed FLAV group body (no length prefix, matching the file format): a 4-byte
 * numberOfFlavors count followed by that many inner flavor records, each shaped as
 * `flavorItemBinary` from `FlavorEntryParserTest`. Uses 2 flavors (not the real file's typical
 * 7) to prove the OUTER dynamic read is genuine, not hardcoded — the inner flavors each get a
 * small (2-element) relations list too, to prove BOTH nesting levels are real.
 */
private fun flavGroupBinary(
    flavors: List<Pair<String, List<Int>>> = listOf(
        "Barbarian" to listOf(80, 20),
        "Eastern Civ" to listOf(10, 100),
    ),
): Buffer = Buffer().apply {
    writeIntLe(flavors.size)
    flavors.forEach { (name, relations) ->
        write(ByteString.of(*ByteArray(4))) // unknown
        writePaddedField(name, 256)
        writeIntLe(relations.size)
        relations.forEach { writeIntLe(it) }
    }
}

class FlavGroupEntryParserTest : FunSpec({

    test("well-formed group is parsed into a genuinely dynamic list of flavors") {
        val group = FlavGroupEntryParser.parse(flavGroupBinary())
        group shouldBe FlavGroupEntry(
            listOf(
                FlavorEntry(ByteString.of(*ByteArray(4)), "Barbarian", listOf(80, 20)),
                FlavorEntry(ByteString.of(*ByteArray(4)), "Eastern Civ", listOf(10, 100)),
            ),
        )
    }

    test("an implausibly large flavor count throws RiffletParseException before attempting to allocate") {
        val source = Buffer().apply {
            writeIntLe(1_000_000)
            write(ByteArray(10))
        }
        shouldThrow<RiffletParseException> { FlavGroupEntryParser.parse(source) }
    }
})
