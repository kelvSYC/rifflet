package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.internal.t3.T3ObjsParser
import com.kelvsyc.rifflet.internal.t3.T3RawBufferedBlock
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString

private fun rawBlock(body: Buffer): T3RawBufferedBlock =
    T3RawBufferedBlock(T3BlockIds.OBJS, 0x0000, body, body.size.toUInt())

private fun objsBinary(
    metaclassIndex: Int,
    flags: Int = 0,
    vararg objects: Pair<UInt, ByteArray>,
): Buffer = Buffer().apply {
    val isLarge = flags and 0x01 != 0
    writeShortLe(objects.size)
    writeShortLe(metaclassIndex)
    writeShortLe(flags)
    for ((objectId, data) in objects) {
        writeIntLe(objectId.toInt())
        if (isLarge) writeIntLe(data.size) else writeShortLe(data.size)
        write(data)
    }
}

class T3ObjsParserTest : FunSpec({
    test("zero objects: metaclass index and flags parsed correctly") {
        val block = T3ObjsParser.parse(rawBlock(objsBinary(metaclassIndex = 3)))
        block.metaclassIndex shouldBe 3
        block.isTransient shouldBe false
        block.objects shouldBe emptyList()
    }

    test("single object: objectId and data round-trip") {
        val data = byteArrayOf(0xAA.toByte(), 0xBB.toByte())
        val block = T3ObjsParser.parse(rawBlock(objsBinary(0, objects = arrayOf(0x0001u to data))))
        block.objects.size shouldBe 1
        block.objects[0].objectId shouldBe 0x0001u
        block.objects[0].data shouldBe ByteString.of(*data)
    }

    test("multiple objects: all parsed in order") {
        val data1 = byteArrayOf(0x01)
        val data2 = byteArrayOf(0x02, 0x03)
        val block = T3ObjsParser.parse(rawBlock(objsBinary(1, objects = arrayOf(0x0001u to data1, 0x0002u to data2))))
        block.objects.size shouldBe 2
        block.objects[0].objectId shouldBe 0x0001u
        block.objects[1].objectId shouldBe 0x0002u
        block.objects[1].data shouldBe ByteString.of(*data2)
    }

    test("large flag: data_size field is UINT4, not UINT2") {
        // Wire format with large flag: size field is 4 bytes wide.
        // If the parser mistakenly reads UINT2, it would consume only 2 of the 4 size bytes and
        // misread the remaining bytes as data — the assertion below would then fail.
        val data = byteArrayOf(0xAA.toByte(), 0xBB.toByte(), 0xCC.toByte(), 0xDD.toByte())
        val block = T3ObjsParser.parse(rawBlock(objsBinary(0, flags = 0x01, 0x0042u to data)))
        block.objects[0].objectId shouldBe 0x0042u
        block.objects[0].data shouldBe ByteString.of(*data)
    }

    test("transient flag is surfaced on block") {
        val block = T3ObjsParser.parse(rawBlock(objsBinary(0, flags = 0x02)))
        block.isTransient shouldBe true
    }

    test("metaclass index is preserved") {
        val block = T3ObjsParser.parse(rawBlock(objsBinary(metaclassIndex = 7)))
        block.metaclassIndex shouldBe 7
    }
})
