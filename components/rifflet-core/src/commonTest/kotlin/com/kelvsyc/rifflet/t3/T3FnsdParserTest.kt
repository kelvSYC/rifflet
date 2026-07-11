package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.internal.t3.T3FnsdParser
import com.kelvsyc.rifflet.internal.t3.T3RawBufferedBlock
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

private fun rawBlock(body: Buffer): T3RawBufferedBlock =
    T3RawBufferedBlock(T3BlockIds.FNSD, 0x0001, body, body.size.toUInt())

private fun fnsdBinary(vararg names: String): Buffer = Buffer().apply {
    writeShortLe(names.size)
    for (name in names) {
        val bytes = name.toByteArray(Charsets.ISO_8859_1)
        writeByte(bytes.size)
        write(bytes)
    }
}

class T3FnsdParserTest : FunSpec({
    test("zero entries produces empty list") {
        val block = T3FnsdParser.parse(rawBlock(fnsdBinary()))
        block.functionSets shouldBe emptyList()
    }

    test("single entry is parsed correctly") {
        val block = T3FnsdParser.parse(rawBlock(fnsdBinary("T3Object")))
        block.functionSets shouldBe listOf("T3Object")
    }

    test("multiple entries are all parsed in order") {
        val block = T3FnsdParser.parse(rawBlock(fnsdBinary("T3Object", "t3General", "t3IO")))
        block.functionSets shouldBe listOf("T3Object", "t3General", "t3IO")
    }
})
