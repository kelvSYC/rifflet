package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.WsizEntry
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

/** Builds a well-formed 80-byte WSIZ item body (the 4-byte length prefix is not included; the
 * generic section loop reads and strips it before handing the item to this parser). */
private fun wsizItemBinary(
    optimalNumberOfCities: Int = 12,
    techRate: Int = 4,
    reserved: ByteArray = ByteArray(24),
    name: String = "Standard",
    height: Int = 60,
    distanceBetweenCivs: Int = 6,
    numberOfCivs: Int = 7,
    width: Int = 80,
): Buffer = Buffer().apply {
    writeIntLe(optimalNumberOfCities)
    writeIntLe(techRate)
    write(reserved)
    writePaddedField(name, 32)
    writeIntLe(height)
    writeIntLe(distanceBetweenCivs)
    writeIntLe(numberOfCivs)
    writeIntLe(width)
}

class WsizEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields") {
        val entry = WsizEntryParser.parse(wsizItemBinary())
        entry shouldBe WsizEntry(
            optimalNumberOfCities = 12,
            techRate = 4,
            reserved = ByteString.of(*ByteArray(24)),
            name = "Standard",
            height = 60,
            distanceBetweenCivs = 6,
            numberOfCivs = 7,
            width = 80,
        )
    }

    test("reserved gap is preserved raw, not validated") {
        val garbage = ByteArray(24) { (it + 1).toByte() }
        val entry = WsizEntryParser.parse(wsizItemBinary(reserved = garbage))
        entry.reserved shouldBe ByteString.of(*garbage)
    }
})
