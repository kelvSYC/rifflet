package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.internal.t3.T3SiniParser
import com.kelvsyc.rifflet.internal.t3.T3RawBufferedBlock
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

private fun rawBlock(body: Buffer): T3RawBufferedBlock =
    T3RawBufferedBlock(T3BlockIds.SINI, 0x0000, body, body.size.toUInt())

private fun siniBinary(
    vararg entries: Pair<UInt, UShort>,
    staticCodePoolOffset: UInt = 0u,
    headerSize: Int = 12,
): Buffer = Buffer().apply {
    writeIntLe(headerSize)
    writeIntLe(staticCodePoolOffset.toInt())
    writeIntLe(entries.size)
    for ((objectId, propertyId) in entries) {
        writeIntLe(objectId.toInt())
        writeShortLe(propertyId.toInt())
    }
}

class T3SiniParserTest : FunSpec({
    test("zero entries parses header fields correctly") {
        val block = T3SiniParser.parse(rawBlock(siniBinary(staticCodePoolOffset = 0xABCDu)))
        block.staticCodePoolOffset shouldBe 0xABCDu
        block.entries shouldBe emptyList()
    }

    test("multiple entries are all parsed correctly") {
        val block = T3SiniParser.parse(rawBlock(siniBinary(
            0x0001u to 0x0010u.toUShort(),
            0x0002u to 0x0020u.toUShort(),
            staticCodePoolOffset = 0x1000u,
        )))
        block.staticCodePoolOffset shouldBe 0x1000u
        block.entries.size shouldBe 2
        block.entries[0].objectId shouldBe 0x0001u
        block.entries[0].propertyId shouldBe 0x0010u.toUShort()
        block.entries[1].objectId shouldBe 0x0002u
        block.entries[1].propertyId shouldBe 0x0020u.toUShort()
    }

    test("unexpected header size throws RiffletParseException") {
        shouldThrow<RiffletParseException> {
            T3SiniParser.parse(rawBlock(siniBinary(headerSize = 16)))
        }
    }
})
