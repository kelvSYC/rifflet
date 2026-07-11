package com.kelvsyc.rifflet.t3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun header(): T3Header = T3Header(2, ByteString.of(0, 0, 0, 0), "Sun Aug 01 17:05:20 1999")

private fun entry(name: String, data: ByteArray): MresEntry =
    MresEntry(name, 0u, data.size.toUInt(), ByteString.of(*data))

class T3ImageTest : FunSpec({

    test("findResource finds a resource in a single MresBlock") {
        val image = T3Image(header(), listOf(MresBlock(listOf(entry("A.WAV", byteArrayOf(0x01)))), EndBlock))
        image.findResource("A.WAV")?.name shouldBe "A.WAV"
    }

    test("findResource finds a resource only present in a second block") {
        val image = T3Image(
            header(),
            listOf(
                MresBlock(listOf(entry("A.WAV", byteArrayOf(0x01)))),
                MresBlock(listOf(entry("B.WAV", byteArrayOf(0x02)))),
                EndBlock,
            ),
        )
        image.findResource("B.WAV")?.name shouldBe "B.WAV"
    }

    test("findResource resolves a duplicate name across blocks to the first block") {
        val image = T3Image(
            header(),
            listOf(
                MresBlock(listOf(entry("DUP.WAV", byteArrayOf(0x01)))),
                MresBlock(listOf(entry("DUP.WAV", byteArrayOf(0x02)))),
                EndBlock,
            ),
        )
        (image.findResource("DUP.WAV") as MresEntry).data() shouldBe ByteString.of(0x01)
    }

    test("findResource returns null when no MRES blocks are present") {
        val image = T3Image(header(), listOf(EndBlock))
        image.findResource("ANYTHING") shouldBe null
    }

    test("findResource is case-insensitive across blocks") {
        val image = T3Image(
            header(),
            listOf(
                MresBlock(listOf(entry("A.WAV", byteArrayOf(0x01)))),
                MresBlock(listOf(entry("Sound.WAV", byteArrayOf(0x02)))),
                EndBlock,
            ),
        )
        image.findResource("sound.wav")?.name shouldBe "Sound.WAV"
    }

    test("findResource finds a resource only present in an MrelBlock") {
        val image = T3Image(header(), listOf(MrelBlock(listOf(MrelEntry("A.WAV", "sound/a.wav"))), EndBlock))
        (image.findResource("A.WAV") as MrelEntry).filename shouldBe "sound/a.wav"
    }

    test("findResource prefers an MrelBlock over a later MresBlock with the same name") {
        val image = T3Image(
            header(),
            listOf(
                MrelBlock(listOf(MrelEntry("DUP.WAV", "sound/linked.wav"))),
                MresBlock(listOf(entry("DUP.WAV", byteArrayOf(0x01)))),
                EndBlock,
            ),
        )
        (image.findResource("DUP.WAV") as MrelEntry).filename shouldBe "sound/linked.wav"
    }

    test("findResource prefers an MresBlock over a later MrelBlock with the same name") {
        val image = T3Image(
            header(),
            listOf(
                MresBlock(listOf(entry("DUP.WAV", byteArrayOf(0x01)))),
                MrelBlock(listOf(MrelEntry("DUP.WAV", "sound/linked.wav"))),
                EndBlock,
            ),
        )
        (image.findResource("DUP.WAV") as MresEntry).data() shouldBe ByteString.of(0x01)
    }
})
