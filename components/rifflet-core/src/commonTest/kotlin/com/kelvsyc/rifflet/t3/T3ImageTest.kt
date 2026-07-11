package com.kelvsyc.rifflet.t3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun header(): T3Header = T3Header(2, ByteString.of(0, 0, 0, 0), "Sun Aug 01 17:05:20 1999")

private fun entry(name: String, data: ByteArray): MresEntry =
    MresEntry(name, 0u, data.size.toUInt(), ByteString.of(*data))

private fun poolImage(): T3Image {
    val cpdf = CpdfBlock(listOf(CpdfPoolEntry(pageCount = 2u, pageSize = 4u)))
    val page0 = CppgBlock(poolId = 1, pageIndex = 0u, xorMask = 0, pageData = ByteString.of(0x01, 0x02, 0x03, 0x04))
    val page1 = CppgBlock(poolId = 1, pageIndex = 1u, xorMask = 0, pageData = ByteString.of(0x05, 0x06, 0x07, 0x08))
    return T3Image(header(), listOf(cpdf, page0, page1, EndBlock))
}

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

    // --- findObject ---

    test("findObject: finds object in first ObjsBlock") {
        val obj = ObjsObject(42u, ByteString.of(0x01))
        val image = T3Image(header(), listOf(ObjsBlock(0, false, listOf(obj)), EndBlock))
        image.findObject(42u)?.objectId shouldBe 42u
    }

    test("findObject: finds object in second ObjsBlock when absent from first") {
        val obj = ObjsObject(99u, ByteString.of(0x02))
        val image = T3Image(
            header(),
            listOf(
                ObjsBlock(0, false, listOf(ObjsObject(1u, ByteString.of(0x01)))),
                ObjsBlock(0, false, listOf(obj)),
                EndBlock,
            ),
        )
        image.findObject(99u)?.objectId shouldBe 99u
    }

    test("findObject: returns null for unknown object ID") {
        val image = T3Image(header(), listOf(ObjsBlock(0, false, listOf(ObjsObject(1u, ByteString.of(0x01)))), EndBlock))
        image.findObject(999u) shouldBe null
    }

    test("findObject: returns null when no ObjsBlock is present") {
        val image = T3Image(header(), listOf(EndBlock))
        image.findObject(1u) shouldBe null
    }

    // --- readFromPool ---
    // Setup: pool 1, pageSize=4; page 0 = [01 02 03 04], page 1 = [05 06 07 08]

    test("readFromPool: size 0 returns empty ByteString") {
        poolImage().readFromPool(1, 0u, 0) shouldBe ByteString.EMPTY
    }

    test("readFromPool: reads within a single page") {
        poolImage().readFromPool(1, 0u, 4) shouldBe ByteString.of(0x01, 0x02, 0x03, 0x04)
    }

    test("readFromPool: reads a subset within a single page") {
        poolImage().readFromPool(1, 1u, 2) shouldBe ByteString.of(0x02, 0x03)
    }

    test("readFromPool: read spanning a page boundary returns bytes from both pages") {
        // offset=2, size=4 → [03 04] from page 0, [05 06] from page 1
        poolImage().readFromPool(1, 2u, 4) shouldBe ByteString.of(0x03, 0x04, 0x05, 0x06)
    }

    test("readFromPool: returns null for unknown poolId") {
        poolImage().readFromPool(2, 0u, 4) shouldBe null
    }

    test("readFromPool: returns null when required CPPG page is absent") {
        val cpdf = CpdfBlock(listOf(CpdfPoolEntry(pageCount = 2u, pageSize = 4u)))
        val page0 = CppgBlock(poolId = 1, pageIndex = 0u, xorMask = 0, pageData = ByteString.of(0x01, 0x02, 0x03, 0x04))
        // page 1 intentionally missing
        val image = T3Image(header(), listOf(cpdf, page0, EndBlock))
        image.readFromPool(1, 2u, 4) shouldBe null   // crosses into missing page 1
    }

    test("readFromPool: returns null when no CpdfBlock is present") {
        val image = T3Image(header(), listOf(EndBlock))
        image.readFromPool(1, 0u, 4) shouldBe null
    }
})
