package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.internal.t3.T3MacrParser
import com.kelvsyc.rifflet.internal.t3.T3RawBufferedBlock
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

private fun rawBlock(body: Buffer): T3RawBufferedBlock =
    T3RawBufferedBlock(T3BlockIds.MACR, 0x0000, body, body.size.toUInt())

private fun macrEntry(
    name: String,
    flags: Int,
    params: List<String>,
    expansion: String,
): ByteArray {
    val nameBytes = name.toByteArray(Charsets.UTF_8)
    val expansionBytes = expansion.toByteArray(Charsets.UTF_8)
    return Buffer().apply {
        writeShortLe(nameBytes.size)
        write(nameBytes)
        writeShortLe(flags)
        writeShortLe(params.size)
        for (p in params) {
            val pBytes = p.toByteArray(Charsets.UTF_8)
            writeShortLe(pBytes.size)
            write(pBytes)
        }
        writeIntLe(expansionBytes.size)
        write(expansionBytes)
    }.readByteArray()
}

private fun macrBinary(vararg entries: ByteArray): Buffer = Buffer().apply {
    writeIntLe(entries.size)
    for (entry in entries) write(entry)
}

class T3MacrParserTest : FunSpec({
    test("zero entries produces empty list") {
        T3MacrParser.parse(rawBlock(macrBinary())).entries shouldBe emptyList()
    }

    test("non-function-like macro: isFunctionLike=false, empty params") {
        val body = macrBinary(macrEntry("MAX_ITEMS", flags = 0x0000, params = emptyList(), expansion = "100"))
        val entry = T3MacrParser.parse(rawBlock(body)).entries[0]
        entry.name shouldBe "MAX_ITEMS"
        entry.isFunctionLike shouldBe false
        entry.isVarArgs shouldBe false
        entry.params shouldBe emptyList()
        entry.expansion shouldBe "100"
    }

    test("function-like macro with multiple parameters") {
        val body = macrBinary(macrEntry("MIN", flags = 0x0001, params = listOf("a", "b"), expansion = "((a)<(b)?(a):(b))"))
        val entry = T3MacrParser.parse(rawBlock(body)).entries[0]
        entry.name shouldBe "MIN"
        entry.isFunctionLike shouldBe true
        entry.isVarArgs shouldBe false
        entry.params.map { it.name } shouldBe listOf("a", "b")
        entry.expansion shouldBe "((a)<(b)?(a):(b))"
    }

    test("varargs flag is parsed correctly") {
        val body = macrBinary(macrEntry("DBG", flags = 0x0003, params = listOf("fmt"), expansion = ""))
        val entry = T3MacrParser.parse(rawBlock(body)).entries[0]
        entry.isFunctionLike shouldBe true
        entry.isVarArgs shouldBe true
    }

    test("empty expansion string is preserved as empty string") {
        val body = macrBinary(macrEntry("NOOP", flags = 0x0000, params = emptyList(), expansion = ""))
        T3MacrParser.parse(rawBlock(body)).entries[0].expansion shouldBe ""
    }
})
