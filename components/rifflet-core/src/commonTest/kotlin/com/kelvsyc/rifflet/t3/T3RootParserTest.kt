package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.RiffletParseException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.decodeHex

private val VALID_MAGIC = "54332d696d6167650d0a1a".decodeHex()

/** Builds a valid 69-byte T3 preamble. */
private fun preambleBinary(version: Int = 2): Buffer = Buffer().apply {
    write(VALID_MAGIC)
    writeShortLe(version)
    write(ByteArray(28))
    write(byteArrayOf(0x0A, 0x0B, 0x0C, 0x0D))
    writeString("Sun Aug 01 17:05:20 1999", Charsets.US_ASCII)
}

/** Encodes a bare T3 block: type + LE size + LE flags + body, no padding. */
private fun blockBinary(type: String, flags: Int, data: ByteArray = byteArrayOf()): Buffer = Buffer().apply {
    writeString(type, Charsets.ISO_8859_1)
    writeIntLe(data.size)
    writeShortLe(flags)
    write(data)
}

private fun entpBinary(): Buffer = blockBinary(
    "ENTP",
    flags = 0x0001,
    data = Buffer().apply {
        writeIntLe(0x1000)
        repeat(6) { writeShortLe(it + 1) }
        writeShortLe(20)
    }.readByteArray(),
)

private fun xorBytes(bytes: ByteArray): ByteArray = ByteArray(bytes.size) { i -> (bytes[i].toInt() xor 0xFF).toByte() }

/** Builds an MRES block body (TOC + contiguous resource data) for the given name-to-data pairs. */
private fun mresBinary(vararg resources: Pair<String, ByteArray>): ByteArray {
    val tocEntries = resources.map { (name, data) ->
        Triple(name, xorBytes(Buffer().apply { writeString(name, Charsets.ISO_8859_1) }.readByteArray()), data)
    }
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
    return body.readByteArray()
}

class T3RootParserTest : FunSpec({

    test("well-formed image parses header and all blocks, ending with EndBlock") {
        val source = Buffer().apply {
            writeAll(preambleBinary())
            writeAll(entpBinary())
            writeAll(blockBinary("MCLD", flags = 0x0000, data = byteArrayOf(0x01, 0x02, 0x03)))
            writeAll(blockBinary("EOF ", flags = 0x0001))
        }
        val image = T3RootParser.parse(source)
        image.header.version shouldBe 2
        image.header.timestamp shouldBe "Sun Aug 01 17:05:20 1999"
        image.blocks.size shouldBe 3
        (image.blocks[0] as EntryPointBlock).debugTableFrameHeaderSize shouldBe 20
        (image.blocks[1] as T3RawBlock).type shouldBe T3BlockIds.MCLD
        image.blocks[2] shouldBe EndBlock
    }

    test("trailing bytes after EOF are present but never read") {
        val source = Buffer().apply {
            writeAll(preambleBinary())
            writeAll(blockBinary("EOF ", flags = 0x0001))
            writeString("trailing host-executable bytes", Charsets.ISO_8859_1)
        }
        val image = T3RootParser.parse(source)
        image.blocks shouldBe listOf(EndBlock)
        source.exhausted() shouldBe false
    }

    test("bad magic surfaces as RiffletParseException through the public API") {
        val source = Buffer().apply {
            write("00000000000000000000".decodeHex())
            write(ByteArray(58))
        }
        shouldThrow<RiffletParseException> { T3RootParser.parse(source) }
    }

    test("well-formed image containing an MRES block dispatches it correctly") {
        val source = Buffer().apply {
            writeAll(preambleBinary())
            writeAll(blockBinary("MRES", flags = 0x0000, data = mresBinary("A.WAV" to byteArrayOf(0x01, 0x02))))
            writeAll(blockBinary("EOF ", flags = 0x0001))
        }
        val image = T3RootParser.parse(source)
        image.blocks.size shouldBe 2
        val mres = image.blocks[0] as MresBlock
        mres.entries.size shouldBe 1
        mres.entries[0].name shouldBe "A.WAV"
        (image.findResource("A.WAV") as MresEntry).data() shouldBe ByteString.of(0x01, 0x02)
    }

    context("truncated input") {
        test("source ending mid-preamble throws RiffletParseException") {
            val source = Buffer().apply { write(VALID_MAGIC) }
            shouldThrow<RiffletParseException> { T3RootParser.parse(source) }
        }

        test("source ending mid-block-header throws RiffletParseException") {
            val source = Buffer().apply {
                writeAll(preambleBinary())
                writeString("MCLD", Charsets.ISO_8859_1)
            }
            shouldThrow<RiffletParseException> { T3RootParser.parse(source) }
        }

        test("source ending mid-block-body throws RiffletParseException") {
            val source = Buffer().apply {
                writeAll(preambleBinary())
                writeString("MCLD", Charsets.ISO_8859_1)
                writeIntLe(100)
                writeShortLe(0x0000)
                write(byteArrayOf(0x01, 0x02))
            }
            shouldThrow<RiffletParseException> { T3RootParser.parse(source) }
        }

        test("source ending with no EOF block ever seen throws RiffletParseException") {
            val source = Buffer().apply {
                writeAll(preambleBinary())
                writeAll(blockBinary("MCLD", flags = 0x0000, data = byteArrayOf(0x01)))
            }
            shouldThrow<RiffletParseException> { T3RootParser.parse(source) }
        }

        test("source ending mid-MRES-block-body throws RiffletParseException") {
            val source = Buffer().apply {
                writeAll(preambleBinary())
                writeString("MRES", Charsets.ISO_8859_1)
                writeIntLe(100)
                writeShortLe(0x0000)
                write(byteArrayOf(0x01, 0x02))
            }
            shouldThrow<RiffletParseException> { T3RootParser.parse(source) }
        }
    }
})
