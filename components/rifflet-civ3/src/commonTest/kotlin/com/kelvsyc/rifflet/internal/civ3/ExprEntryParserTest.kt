package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.ExprEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

/** Writes [text] into [fieldSize] bytes, null-padding the remainder. */
private fun Buffer.writePaddedField(text: String, fieldSize: Int) {
    val start = size
    writeString(text, Charsets.US_ASCII)
    write(ByteArray((fieldSize - (size - start)).toInt()))
}

/** Builds a well-formed 40-byte EXPR item body (length prefix excluded, as with prior sections). */
private fun exprItemBinary(
    name: String = "Veteran",
    baseHitPoints: Int = 10,
    retreatBonus: Int = 20,
): Buffer = Buffer().apply {
    writePaddedField(name, 32)
    writeIntLe(baseHitPoints)
    writeIntLe(retreatBonus)
}

class ExprEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields") {
        val entry = ExprEntryParser.parse(exprItemBinary())
        entry shouldBe ExprEntry(name = "Veteran", baseHitPoints = 10, retreatBonus = 20)
    }
})
