package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletParseException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.decodeHex
import com.kelvsyc.rifflet.civ3.DiffEntry
import com.kelvsyc.rifflet.civ3.DiffSection
import com.kelvsyc.rifflet.civ3.ErasEntry
import com.kelvsyc.rifflet.civ3.ErasSection
import com.kelvsyc.rifflet.civ3.WsizEntry
import com.kelvsyc.rifflet.civ3.WsizSection

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

/** Builds a well-formed 80-byte WSIZ item body (no length prefix; the caller adds it via [oneItemSectionBytes]). */
private fun wsizItemBody(): Buffer = Buffer().apply {
    writeIntLe(12) // optimalNumberOfCities
    writeIntLe(4) // techRate
    write(ByteArray(24)) // reserved
    writeString("Standard", Charsets.US_ASCII)
    write(ByteArray(32 - 8)) // pad "Standard" (8 bytes) to 32
    writeIntLe(60) // height
    writeIntLe(6) // distanceBetweenCivs
    writeIntLe(7) // numberOfCivs
    writeIntLe(80) // width
}

/** Builds a well-formed 120-byte DIFF item body. */
private fun diffItemBody(): Buffer = Buffer().apply {
    writeString("Chieftain", Charsets.US_ASCII)
    write(ByteArray(64 - 9)) // pad "Chieftain" (9 bytes) to 64
    repeat(14) { writeIntLe(it) }
}

/** Builds a well-formed 264-byte ERAS item body. */
private fun erasItemBody(): Buffer = Buffer().apply {
    writeString("Ancient", Charsets.US_ASCII)
    write(ByteArray(64 - 7)) // pad "Ancient" (7 bytes) to 64
    write(ByteArray(32)) // civilopediaEntry: empty
    repeat(5) { write(ByteArray(32)) } // researcher1..5: empty
    writeIntLe(0) // numberOfUsedResearcherNames
    write(ByteArray(4)) // unknown
}

/** Wraps a single item body into a full section: marker, count=1, length prefix, item body. */
private fun oneItemSectionBytes(marker: String, itemBody: Buffer): Buffer = Buffer().apply {
    writeString(marker, Charsets.US_ASCII)
    writeIntLe(1)
    writeIntLe(itemBody.size.toInt())
    writeAll(itemBody)
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

    test("WSIZ section produces a typed WsizSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("WSIZ", wsizItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            WsizSection(
                listOf(WsizEntry(12, 4, ByteString.of(*ByteArray(24)), "Standard", 60, 6, 7, 80)),
            ),
        )
    }

    test("DIFF section produces a typed DiffSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("DIFF", diffItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            DiffSection(listOf(DiffEntry("Chieftain", 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13))),
        )
    }

    test("ERAS section produces a typed ErasSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("ERAS", erasItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            ErasSection(
                listOf(ErasEntry("Ancient", "", "", "", "", "", "", 0, ByteString.of(*ByteArray(4)))),
            ),
        )
    }
})
