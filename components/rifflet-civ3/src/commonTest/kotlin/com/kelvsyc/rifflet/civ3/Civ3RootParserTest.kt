package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletParseException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.decodeHex

/** Builds a valid 732-byte VER# section: marker, header count/length, version, description, title. */
private fun verSectionBytes(major: Int = 12, minor: Int = 7): Buffer = Buffer().apply {
    writeString("VER#", Charsets.US_ASCII)
    writeIntLe(1)
    writeIntLe(720)
    writeIntLe(0)
    writeIntLe(0)
    writeIntLe(major)
    writeIntLe(minor)
    write(ByteArray(640))
    write(ByteArray(64))
}

/** Builds a raw section: marker, little-endian item count, then that many length-prefixed items. */
private fun rawSectionBytes(marker: String, items: List<ByteArray>): Buffer = Buffer().apply {
    writeString(marker, Charsets.US_ASCII)
    writeIntLe(items.size)
    items.forEach {
        writeIntLe(it.size)
        write(it)
    }
}

class Civ3RootParserTest : FunSpec({

    test("uncompressed BIC file with no trailing sections is parsed") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
        }
        val file = Civ3RootParser.parse(source)
        file.header.major shouldBe 12
        file.header.minor shouldBe 7
        file.sections shouldBe emptyList()
    }

    test("uncompressed BICX file with an unmodeled trailing section is parsed as Civ3RawSection") {
        val source = Buffer().apply {
            writeString("BICX", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(rawSectionBytes("TECH", listOf(byteArrayOf(1, 2, 3))))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(Civ3RawSection(ChunkId("TECH"), 1, listOf(ByteString.of(1, 2, 3))))
    }

    test("bad leading magic that also fails to decompress into a valid Civ3 file throws") {
        // "00048224258f807f" is the reference PKWare Implode test vector from blast.c, which
        // decompresses to the ASCII text "AIAIAIAIAIAIA" -- valid Implode data, but not a Civ3
        // file. This exercises the real (non-mocked) decompression routing path.
        val source = Buffer().write("00048224258f807f".decodeHex())
        shouldThrow<RiffletParseException> { Civ3RootParser.parse(source) }
    }

    test("truncated VER# section throws RiffletParseException") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            write(ByteArray(10))
        }
        shouldThrow<RiffletParseException> { Civ3RootParser.parse(source) }
    }
})
