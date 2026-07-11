package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.internal.t3.T3McldParser
import com.kelvsyc.rifflet.internal.t3.T3RawBufferedBlock
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

private fun rawBlock(body: Buffer): T3RawBufferedBlock =
    T3RawBufferedBlock(T3BlockIds.MCLD, 0x0001, body, body.size.toUInt())

/**
 * Builds an MCLD block body. Each entry: 2-byte offset-to-next (set to 0 — we discard it),
 * 1-byte name length, name bytes, 2-byte property count, 2-byte property record size (2),
 * then property count × 2-byte property IDs.
 */
private fun mcldBinary(vararg entries: Pair<String, List<Int>>): Buffer = Buffer().apply {
    writeShortLe(entries.size)
    for ((name, props) in entries) {
        writeShortLe(0)                         // offset_to_next — discarded by parser
        writeByte(name.length)
        writeString(name, Charsets.US_ASCII)
        writeShortLe(props.size)
        writeShortLe(2)                         // property_record_size — always 2
        for (p in props) writeShortLe(p)
    }
}

class T3McldParserTest : FunSpec({

    test("zero entries produces empty list") {
        val block = T3McldParser.parse(rawBlock(mcldBinary()))
        block.entries shouldBe emptyList()
    }

    test("single entry with no properties") {
        val block = T3McldParser.parse(rawBlock(mcldBinary("TadsObject" to emptyList())))
        block.entries.size shouldBe 1
        block.entries[0].name shouldBe "TadsObject"
        block.entries[0].properties shouldBe emptyList()
    }

    test("single entry with properties: name and IDs round-trip") {
        val block = T3McldParser.parse(rawBlock(mcldBinary("TadsObject" to listOf(10, 20, 30))))
        block.entries[0].name shouldBe "TadsObject"
        block.entries[0].properties shouldBe listOf(10, 20, 30)
    }

    test("multiple entries: all names and properties parsed in order") {
        val block = T3McldParser.parse(rawBlock(
            mcldBinary("TadsObject" to listOf(10), "List" to listOf(20, 30)),
        ))
        block.entries.size shouldBe 2
        block.entries[0].name shouldBe "TadsObject"
        block.entries[0].properties shouldBe listOf(10)
        block.entries[1].name shouldBe "List"
        block.entries[1].properties shouldBe listOf(20, 30)
    }

    test("entryForIndex: in-range returns entry, out-of-range returns null") {
        val block = McldBlock(listOf(McldEntry("TadsObject", listOf(10)), McldEntry("List", emptyList())))
        block.entryForIndex(0)?.name shouldBe "TadsObject"
        block.entryForIndex(1)?.name shouldBe "List"
        block.entryForIndex(2) shouldBe null
        block.entryForIndex(-1) shouldBe null
    }

    test("indexOf: found returns correct index, not found returns null") {
        val block = McldBlock(listOf(McldEntry("TadsObject", emptyList()), McldEntry("List", emptyList())))
        block.indexOf("TadsObject") shouldBe 0
        block.indexOf("List") shouldBe 1
        block.indexOf("Vector") shouldBe null
    }
})
