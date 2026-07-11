package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.internal.t3.T3MrelParser
import com.kelvsyc.rifflet.internal.t3.T3RawBufferedBlock
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

private fun rawBlock(body: Buffer): T3RawBufferedBlock =
    T3RawBufferedBlock(T3BlockIds.MREL, 0x0000, body, body.size.toUInt())

private fun asciiBytes(s: String): ByteArray = Buffer().apply { writeString(s, Charsets.ISO_8859_1) }.readByteArray()

/** Builds an MREL block body (entry count + name/filename mappings, both plain, no XOR). */
private fun mrelBinary(vararg mappings: Pair<String, String>): Buffer {
    val body = Buffer()
    body.writeShortLe(mappings.size)
    for ((name, filename) in mappings) {
        val nameBytes = asciiBytes(name)
        val filenameBytes = asciiBytes(filename)
        body.writeByte(nameBytes.size)
        body.write(nameBytes)
        body.writeByte(filenameBytes.size)
        body.write(filenameBytes)
    }
    return body
}

class T3MrelParserTest : FunSpec({

    test("single entry: name and filename are parsed plainly, not XOR'd") {
        val body = mrelBinary("SPLASH.PNG" to "art/splash.png")
        val block = T3MrelParser.parse(rawBlock(body))
        block.entries.size shouldBe 1
        block.entries[0].name shouldBe "SPLASH.PNG"
        block.entries[0].filename shouldBe "art/splash.png"
    }

    test("multiple entries: each is parsed correctly") {
        val body = mrelBinary(
            "A.WAV" to "sound/a.wav",
            "B.WAV" to "sound/b.wav",
        )
        val block = T3MrelParser.parse(rawBlock(body))
        block.entries.size shouldBe 2
        block.entries[0].name shouldBe "A.WAV"
        block.entries[0].filename shouldBe "sound/a.wav"
        block.entries[1].name shouldBe "B.WAV"
        block.entries[1].filename shouldBe "sound/b.wav"
    }

    test("find() is case-insensitive") {
        val body = mrelBinary("Sound.WAV" to "sound/s.wav")
        val block = T3MrelParser.parse(rawBlock(body))
        block.find("sound.wav")?.filename shouldBe "sound/s.wav"
        block.find("SOUND.WAV")?.filename shouldBe "sound/s.wav"
    }

    test("duplicate names: find() returns the first one in TOC order") {
        val body = mrelBinary(
            "DUP.WAV" to "sound/first.wav",
            "DUP.WAV" to "sound/second.wav",
        )
        val block = T3MrelParser.parse(rawBlock(body))
        block.find("DUP.WAV")?.filename shouldBe "sound/first.wav"
    }

    test("zero-entry block has no entries and find() returns null") {
        val body = mrelBinary()
        val block = T3MrelParser.parse(rawBlock(body))
        block.entries shouldBe emptyList()
        block.find("anything") shouldBe null
    }
})
