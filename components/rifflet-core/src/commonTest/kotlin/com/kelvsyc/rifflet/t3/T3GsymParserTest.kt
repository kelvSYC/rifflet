package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.internal.t3.T3GsymParser
import com.kelvsyc.rifflet.internal.t3.T3RawBufferedBlock
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString.Companion.decodeHex

private fun rawBlock(body: Buffer): T3RawBufferedBlock =
    T3RawBufferedBlock(T3BlockIds.GSYM, 0x0000, body, body.size.toUInt())

/** Builds a single GSYM entry: UINT2 nameLen, UINT2 extraDataLen, UINT2 typeCode, name bytes, extra bytes. */
private fun gsymEntry(typeCode: Int, name: String, extraData: ByteArray): ByteArray {
    val nameBytes = name.toByteArray(Charsets.UTF_8)
    return Buffer().apply {
        writeShortLe(nameBytes.size)
        writeShortLe(extraData.size)
        writeShortLe(typeCode)
        write(nameBytes)
        write(extraData)
    }.readByteArray()
}

private fun gsymBinary(vararg entries: ByteArray): Buffer = Buffer().apply {
    writeIntLe(entries.size)
    for (entry in entries) write(entry)
}

/** Concatenates byte arrays into one. */
private fun concat(vararg arrays: ByteArray): ByteArray {
    val buf = Buffer()
    for (a in arrays) buf.write(a)
    return buf.readByteArray()
}

private fun int4Le(v: Int) = Buffer().apply { writeIntLe(v) }.readByteArray()
private fun uint2Le(v: Int) = Buffer().apply { writeShortLe(v) }.readByteArray()
private fun byte1(v: Int) = byteArrayOf(v.toByte())

class T3GsymParserTest : FunSpec({
    test("zero entries produces empty list") {
        T3GsymParser.parse(rawBlock(gsymBinary())).entries shouldBe emptyList()
    }

    test("Function entry (type 1) is parsed correctly") {
        // extra: UINT4 codeOffset, UINT2 argCount, BYTE varargs, BYTE hasReturn, UINT2 optArgCount
        val extra = concat(int4Le(0x1000), uint2Le(2), byte1(0), byte1(1), uint2Le(1))
        val body = gsymBinary(gsymEntry(1, "myFunc", extra))
        val entry = T3GsymParser.parse(rawBlock(body)).entries[0] as GsymEntry.Function
        entry.name shouldBe "myFunc"
        entry.codeOffset shouldBe 0x1000u
        entry.argCount shouldBe 2
        entry.isVarArgs shouldBe false
        entry.hasReturn shouldBe true
        entry.optionalArgCount shouldBe 1
    }

    test("Object entry (type 2) is parsed correctly") {
        // extra: UINT4 objectId, UINT4 modifyingObjectId
        val extra = concat(int4Le(0x0A), int4Le(0x00))
        val body = gsymBinary(gsymEntry(2, "MyObj", extra))
        val entry = T3GsymParser.parse(rawBlock(body)).entries[0] as GsymEntry.Object
        entry.name shouldBe "MyObj"
        entry.objectId shouldBe 10u
        entry.modifyingObjectId shouldBe 0u
    }

    test("Property entry (type 3) is parsed correctly") {
        // extra: UINT2 propertyId, BYTE flags
        val extra = concat(uint2Le(0x0042), byte1(0x01))
        val body = gsymBinary(gsymEntry(3, "myProp", extra))
        val entry = T3GsymParser.parse(rawBlock(body)).entries[0] as GsymEntry.Property
        entry.name shouldBe "myProp"
        entry.propertyId shouldBe 0x0042u.toUShort()
        entry.flags shouldBe 1
    }

    test("IntrinsicFunction entry (type 6) is parsed correctly") {
        // extra: UINT2 funcIndex, UINT2 funcSetIndex, BYTE hasReturn, UINT2 minArgs, UINT2 maxArgs, BYTE varargs
        val extra = concat(uint2Le(3), uint2Le(1), byte1(1), uint2Le(0), uint2Le(2), byte1(0))
        val body = gsymBinary(gsymEntry(6, "t3GetVMVsn", extra))
        val entry = T3GsymParser.parse(rawBlock(body)).entries[0] as GsymEntry.IntrinsicFunction
        entry.name shouldBe "t3GetVMVsn"
        entry.functionIndex shouldBe 3
        entry.functionSetIndex shouldBe 1
        entry.hasReturn shouldBe true
        entry.minArgCount shouldBe 0
        entry.maxArgCount shouldBe 2
        entry.isVarArgs shouldBe false
    }

    test("IntrinsicClass entry (type 9) is parsed correctly") {
        // extra: UINT2 metaclassIndex, UINT4 intrinsicClassObjectId
        val extra = concat(uint2Le(2), int4Le(0x0B))
        val body = gsymBinary(gsymEntry(9, "TadsObject", extra))
        val entry = T3GsymParser.parse(rawBlock(body)).entries[0] as GsymEntry.IntrinsicClass
        entry.name shouldBe "TadsObject"
        entry.metaclassIndex shouldBe 2
        entry.intrinsicClassObjectId shouldBe 11u
    }

    test("EnumeratorValue entry (type 10) is parsed correctly") {
        // extra: UINT4 enumeratorId, BYTE flags
        val extra = concat(int4Le(7), byte1(0))
        val body = gsymBinary(gsymEntry(10, "Colors.Red", extra))
        val entry = T3GsymParser.parse(rawBlock(body)).entries[0] as GsymEntry.EnumeratorValue
        entry.name shouldBe "Colors.Red"
        entry.enumeratorId shouldBe 7u
        entry.flags shouldBe 0
    }

    test("unknown type code produces GsymEntry.Unknown with raw bytes preserved") {
        val extra = byteArrayOf(0x01, 0x02, 0x03)
        val body = gsymBinary(gsymEntry(42, "future", extra))
        val entry = T3GsymParser.parse(rawBlock(body)).entries[0] as GsymEntry.Unknown
        entry.name shouldBe "future"
        entry.typeCode shouldBe 42
        entry.extraData shouldBe "010203".decodeHex()
    }
})
