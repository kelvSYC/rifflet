package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.internal.t3.T3SymdParser
import com.kelvsyc.rifflet.internal.t3.T3RawBufferedBlock
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

private fun rawBlock(body: Buffer): T3RawBufferedBlock =
    T3RawBufferedBlock(T3BlockIds.SYMD, 0x0000, body, body.size.toUInt())

/** Builds one SYMD entry: 1-byte typeTag + 4-byte valueBytes + UBYTE nameLen + name bytes. */
private fun symdEntry(typeTag: Int, valueBytes: ByteArray, name: String): ByteArray {
    val nameBytes = name.toByteArray(Charsets.ISO_8859_1)
    return Buffer().apply {
        writeByte(typeTag)
        write(valueBytes)
        writeByte(nameBytes.size)
        write(nameBytes)
    }.readByteArray()
}

private fun int4Le(v: Int): ByteArray = Buffer().apply { writeIntLe(v) }.readByteArray()
private fun uint2Le(v: Int): ByteArray = Buffer().apply { writeShortLe(v) }.readByteArray()
private val zeroPad2 = byteArrayOf(0, 0)

private fun symdBinary(vararg entries: ByteArray): Buffer = Buffer().apply {
    writeShortLe(entries.size)
    for (entry in entries) write(entry)
}

class T3SymdParserTest : FunSpec({
    test("zero entries produces empty list") {
        T3SymdParser.parse(rawBlock(symdBinary())).entries shouldBe emptyList()
    }

    test("Nil (type 1): value bytes are discarded") {
        val body = symdBinary(symdEntry(1, int4Le(0x12345678), "sym"))
        val block = T3SymdParser.parse(rawBlock(body))
        block.entries[0].value shouldBe T3DataHolder.Nil
        block.entries[0].name shouldBe "sym"
    }

    test("True (type 2): value bytes are discarded") {
        val body = symdBinary(symdEntry(2, int4Le(0x12345678), "sym"))
        T3SymdParser.parse(rawBlock(body)).entries[0].value shouldBe T3DataHolder.True
    }

    test("Empty (type 13): value bytes are discarded") {
        val body = symdBinary(symdEntry(13, int4Le(0x12345678), "sym"))
        T3SymdParser.parse(rawBlock(body)).entries[0].value shouldBe T3DataHolder.Empty
    }

    test("ObjectRef (type 5): UINT4 objectId") {
        val body = symdBinary(symdEntry(5, int4Le(0x1234), "obj"))
        val entry = T3SymdParser.parse(rawBlock(body)).entries[0]
        entry.value shouldBe T3DataHolder.ObjectRef(0x1234u)
        entry.name shouldBe "obj"
    }

    test("PropertyRef (type 6): UINT2 in low 2 bytes, high 2 bytes discarded") {
        val body = symdBinary(symdEntry(6, uint2Le(0x0042) + zeroPad2, "prop"))
        T3SymdParser.parse(rawBlock(body)).entries[0].value shouldBe T3DataHolder.PropertyRef(0x0042u.toUShort())
    }

    test("IntValue (type 7): signed INT4") {
        val body = symdBinary(symdEntry(7, int4Le(-1), "neg"))
        T3SymdParser.parse(rawBlock(body)).entries[0].value shouldBe T3DataHolder.IntValue(-1)
    }

    test("SingleQuotedStringRef (type 8): UINT4 offset") {
        val body = symdBinary(symdEntry(8, int4Le(0x0500), "s"))
        T3SymdParser.parse(rawBlock(body)).entries[0].value shouldBe T3DataHolder.SingleQuotedStringRef(0x0500u)
    }

    test("DoubleQuotedStringRef (type 9): UINT4 offset") {
        val body = symdBinary(symdEntry(9, int4Le(0x0600), "d"))
        T3SymdParser.parse(rawBlock(body)).entries[0].value shouldBe T3DataHolder.DoubleQuotedStringRef(0x0600u)
    }

    test("ListRef (type 10): UINT4 offset") {
        val body = symdBinary(symdEntry(10, int4Le(0x0700), "l"))
        T3SymdParser.parse(rawBlock(body)).entries[0].value shouldBe T3DataHolder.ListRef(0x0700u)
    }

    test("CodeOffset (type 11): UINT4 offset") {
        val body = symdBinary(symdEntry(11, int4Le(0x0800), "c"))
        T3SymdParser.parse(rawBlock(body)).entries[0].value shouldBe T3DataHolder.CodeOffset(0x0800u)
    }

    test("FuncPtr (type 12): UINT4 offset") {
        val body = symdBinary(symdEntry(12, int4Le(0x0900), "f"))
        T3SymdParser.parse(rawBlock(body)).entries[0].value shouldBe T3DataHolder.FuncPtr(0x0900u)
    }

    test("EnumValue (type 15): UINT4 value") {
        val body = symdBinary(symdEntry(15, int4Le(42), "e"))
        T3SymdParser.parse(rawBlock(body)).entries[0].value shouldBe T3DataHolder.EnumValue(42u)
    }

    test("BuiltinFuncPtr (type 16): UINT4 value") {
        val body = symdBinary(symdEntry(16, int4Le(0x0A00), "b"))
        T3SymdParser.parse(rawBlock(body)).entries[0].value shouldBe T3DataHolder.BuiltinFuncPtr(0x0A00u)
    }

    test("unknown type tag throws RiffletParseException") {
        val body = symdBinary(symdEntry(99, int4Le(0), "bad"))
        shouldThrow<RiffletParseException> { T3SymdParser.parse(rawBlock(body)) }
    }
})
