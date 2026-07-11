package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.internal.t3.T3CppgParser
import com.kelvsyc.rifflet.internal.t3.T3RawBufferedBlock
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString

private fun rawBlock(body: Buffer): T3RawBufferedBlock =
    T3RawBufferedBlock(T3BlockIds.CPPG, 0x0000, body, body.size.toUInt())

private fun cppgBinary(
    poolId: Int,
    pageIndex: UInt,
    xorMask: Int,
    pageData: ByteArray,
): Buffer = Buffer().apply {
    writeShortLe(poolId)
    writeIntLe(pageIndex.toInt())
    writeByte(xorMask)
    write(pageData)
}

class T3CppgParserTest : FunSpec({
    test("xorMask zero: pageData is stored bytes verbatim") {
        val data = byteArrayOf(0x01, 0x02, 0x03)
        val block = T3CppgParser.parse(rawBlock(cppgBinary(1, 0u, 0, data)))
        block.poolId shouldBe 1
        block.pageIndex shouldBe 0u
        block.xorMask shouldBe 0
        block.pageData shouldBe ByteString.of(*data)
    }

    test("xorMask zero: rawPageData() returns pageData unchanged") {
        val data = byteArrayOf(0x01, 0x02, 0x03)
        val block = T3CppgParser.parse(rawBlock(cppgBinary(1, 0u, 0, data)))
        block.rawPageData() shouldBe block.pageData
    }

    test("xorMask non-zero: pageData is de-masked, rawPageData() reconstructs original bytes") {
        val maskedData = byteArrayOf(0x41, 0x42, 0x43)
        val xorMask = 0x0F
        val block = T3CppgParser.parse(rawBlock(cppgBinary(1, 0u, xorMask, maskedData)))
        block.pageData shouldBe ByteString.of(
            (0x41 xor 0x0F).toByte(),
            (0x42 xor 0x0F).toByte(),
            (0x43 xor 0x0F).toByte(),
        )
        block.rawPageData() shouldBe ByteString.of(*maskedData)
    }

    test("poolId and pageIndex are parsed correctly") {
        val block = T3CppgParser.parse(rawBlock(cppgBinary(2, 5u, 0, byteArrayOf(0x00))))
        block.poolId shouldBe 2
        block.pageIndex shouldBe 5u
    }

    test("page spanning full block body: all bytes captured") {
        val data = ByteArray(256) { it.toByte() }
        val block = T3CppgParser.parse(rawBlock(cppgBinary(1, 0u, 0, data)))
        block.pageData.size shouldBe 256
        block.pageData[0] shouldBe 0.toByte()
        block.pageData[255] shouldBe 255.toByte()
    }
})
