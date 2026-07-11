package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.internal.t3.T3MresParser
import com.kelvsyc.rifflet.internal.t3.T3RawBufferedBlock
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString

private fun rawBlock(body: Buffer): T3RawBufferedBlock =
    T3RawBufferedBlock(T3BlockIds.MRES, 0x0000, body, body.size.toUInt())

private fun asciiBytes(s: String): ByteArray = Buffer().apply { writeString(s, Charsets.ISO_8859_1) }.readByteArray()

private fun xorBytes(bytes: ByteArray): ByteArray = ByteArray(bytes.size) { i -> (bytes[i].toInt() xor 0xFF).toByte() }

/** Builds an MRES block body (TOC + contiguous resource data) for the given name-to-data pairs. */
private fun mresBinary(vararg resources: Pair<String, ByteArray>): Buffer {
    val tocEntries = resources.map { (name, data) -> Triple(name, xorBytes(asciiBytes(name)), data) }
    val tocSize = 2 + tocEntries.sumOf { (_, nameBytes, _) -> 9 + nameBytes.size }
    var dataOffset = tocSize
    val body = Buffer()
    body.writeShortLe(tocEntries.size)
    for ((_, nameBytes, data) in tocEntries) {
        body.writeIntLe(dataOffset)
        body.writeIntLe(data.size)
        body.writeByte(nameBytes.size)
        body.write(nameBytes)
        dataOffset += data.size
    }
    for ((_, _, data) in tocEntries) {
        body.write(data)
    }
    return body
}

class T3MresParserTest : FunSpec({

    test("single entry: name is XOR-decoded and data() returns the correct slice") {
        val body = mresBinary("ITEM.PNG" to byteArrayOf(0x01, 0x02, 0x03, 0x04))
        val block = T3MresParser.parse(rawBlock(body))
        block.entries.size shouldBe 1
        val entry = block.entries[0]
        entry.name shouldBe "ITEM.PNG"
        entry.offset shouldBe 19u // entryCount(2) + one TOC entry header(9) + name "ITEM.PNG"(8)
        entry.size shouldBe 4u
        entry.data() shouldBe ByteString.of(0x01, 0x02, 0x03, 0x04)
    }

    test("multiple entries: each is parsed correctly and data() returns its own slice") {
        val body = mresBinary(
            "A.WAV" to byteArrayOf(0x0A, 0x0B),
            "B.WAV" to byteArrayOf(0x0C, 0x0D, 0x0E),
        )
        val block = T3MresParser.parse(rawBlock(body))
        block.entries.size shouldBe 2
        block.entries[0].name shouldBe "A.WAV"
        block.entries[0].data() shouldBe ByteString.of(0x0A, 0x0B)
        block.entries[1].name shouldBe "B.WAV"
        block.entries[1].data() shouldBe ByteString.of(0x0C, 0x0D, 0x0E)
    }

    test("find() is case-insensitive") {
        val body = mresBinary("Sound.WAV" to byteArrayOf(0x01))
        val block = T3MresParser.parse(rawBlock(body))
        block.find("sound.wav")?.name shouldBe "Sound.WAV"
        block.find("SOUND.WAV")?.name shouldBe "Sound.WAV"
    }

    test("duplicate names: find() returns the first one in TOC order") {
        val body = mresBinary(
            "DUP.WAV" to byteArrayOf(0x01),
            "DUP.WAV" to byteArrayOf(0x02),
        )
        val block = T3MresParser.parse(rawBlock(body))
        block.find("DUP.WAV")?.data() shouldBe ByteString.of(0x01)
    }

    test("zero-entry block has no entries and find() returns null") {
        val body = mresBinary()
        val block = T3MresParser.parse(rawBlock(body))
        block.entries shouldBe emptyList()
        block.find("anything") shouldBe null
    }

    test("entry offset+size beyond the block throws RiffletParseException") {
        val body = Buffer().apply {
            writeShortLe(1)
            writeIntLe(1000) // offset far beyond this tiny block
            writeIntLe(10)
            val name = xorBytes(asciiBytes("NAME"))
            writeByte(name.size)
            write(name)
        }
        shouldThrow<RiffletParseException> { T3MresParser.parse(rawBlock(body)) }
    }

    test("entry offset+size that would overflow UInt still throws RiffletParseException") {
        val body = Buffer().apply {
            writeShortLe(1)
            writeIntLe(UInt.MAX_VALUE.toInt()) // offset = UInt.MAX_VALUE
            writeIntLe(2) // size = 2; offset + size wraps past UInt.MAX_VALUE back to 1u as plain UInt arithmetic
            val name = xorBytes(asciiBytes("NAME"))
            writeByte(name.size)
            write(name)
        }
        shouldThrow<RiffletParseException> { T3MresParser.parse(rawBlock(body)) }
    }
})
