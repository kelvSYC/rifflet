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

/** Builds an MREL block body (entry count + name/filename mappings, both plain, no XOR). */
private fun mrelBinary(vararg mappings: Pair<String, String>): ByteArray {
    val body = Buffer()
    body.writeShortLe(mappings.size)
    for ((name, filename) in mappings) {
        val nameBytes = Buffer().apply { writeString(name, Charsets.ISO_8859_1) }.readByteArray()
        val filenameBytes = Buffer().apply { writeString(filename, Charsets.ISO_8859_1) }.readByteArray()
        body.writeByte(nameBytes.size)
        body.write(nameBytes)
        body.writeByte(filenameBytes.size)
        body.write(filenameBytes)
    }
    return body.readByteArray()
}

private fun fnsdBinary(vararg names: String): ByteArray = Buffer().apply {
    writeShortLe(names.size)
    for (name in names) {
        val bytes = name.toByteArray(Charsets.ISO_8859_1)
        writeByte(bytes.size)
        write(bytes)
    }
}.readByteArray()

private fun mhlsBinary(vararg addresses: UInt): ByteArray = Buffer().apply {
    writeIntLe(addresses.size)
    for (addr in addresses) writeIntLe(addr.toInt())
}.readByteArray()

private fun siniBinary(staticCodePoolOffset: UInt, vararg entries: Pair<UInt, UShort>): ByteArray = Buffer().apply {
    writeIntLe(12) // headerSize
    writeIntLe(staticCodePoolOffset.toInt())
    writeIntLe(entries.size)
    for ((objectId, propertyId) in entries) {
        writeIntLe(objectId.toInt())
        writeShortLe(propertyId.toInt())
    }
}.readByteArray()

private fun srcfBinary(vararg files: Triple<Int, String, List<Pair<UInt, UInt>>>): ByteArray = Buffer().apply {
    writeShortLe(files.size)
    writeShortLe(8) // lineRecordSize
    for ((masterIndex, filename, lines) in files) {
        val filenameBytes = filename.toByteArray(Charsets.UTF_8)
        writeIntLe(4 + 2 + 2 + filenameBytes.size + 4 + lines.size * 8)
        writeShortLe(masterIndex)
        writeShortLe(filenameBytes.size)
        write(filenameBytes)
        writeIntLe(lines.size)
        for ((lineNum, codeOfs) in lines) {
            writeIntLe(lineNum.toInt())
            writeIntLe(codeOfs.toInt())
        }
    }
}.readByteArray()

private fun symdObjectBinary(name: String, objectId: UInt): ByteArray = Buffer().apply {
    writeShortLe(1) // 1 entry
    writeByte(5)    // typeTag = ObjectRef
    writeIntLe(objectId.toInt())
    val nameBytes = name.toByteArray(Charsets.ISO_8859_1)
    writeByte(nameBytes.size)
    write(nameBytes)
}.readByteArray()

private fun gsymFunctionBinary(name: String, codeOffset: UInt): ByteArray {
    val nameBytes = name.toByteArray(Charsets.UTF_8)
    val extra = Buffer().apply {
        writeIntLe(codeOffset.toInt()) // codeOffset
        writeShortLe(0)                // argCount
        writeByte(0)                   // isVarArgs
        writeByte(1)                   // hasReturn
        writeShortLe(0)                // optionalArgCount
    }.readByteArray()
    return Buffer().apply {
        writeIntLe(1) // 1 entry
        writeShortLe(nameBytes.size)
        writeShortLe(extra.size)
        writeShortLe(1) // typeCode = Function
        write(nameBytes)
        write(extra)
    }.readByteArray()
}

private fun macrBinary(name: String, expansion: String): ByteArray {
    val nameBytes = name.toByteArray(Charsets.UTF_8)
    val expansionBytes = expansion.toByteArray(Charsets.UTF_8)
    val entry = Buffer().apply {
        writeShortLe(nameBytes.size)
        write(nameBytes)
        writeShortLe(0) // flags (not function-like)
        writeShortLe(0) // 0 params
        writeIntLe(expansionBytes.size)
        write(expansionBytes)
    }.readByteArray()
    return Buffer().apply {
        writeIntLe(1) // 1 entry
        write(entry)
    }.readByteArray()
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

    test("well-formed image containing an MREL block dispatches it correctly") {
        val source = Buffer().apply {
            writeAll(preambleBinary())
            writeAll(blockBinary("MREL", flags = 0x0000, data = mrelBinary("SPLASH.PNG" to "art/splash.png")))
            writeAll(blockBinary("EOF ", flags = 0x0001))
        }
        val image = T3RootParser.parse(source)
        image.blocks.size shouldBe 2
        val mrel = image.blocks[0] as MrelBlock
        mrel.entries.size shouldBe 1
        mrel.entries[0].name shouldBe "SPLASH.PNG"
        (image.findResource("SPLASH.PNG") as MrelEntry).filename shouldBe "art/splash.png"
    }

    test("well-formed image containing an FNSD block dispatches it correctly") {
        val source = Buffer().apply {
            writeAll(preambleBinary())
            writeAll(blockBinary("FNSD", flags = 0x0001, data = fnsdBinary("T3Object", "t3General")))
            writeAll(blockBinary("EOF ", flags = 0x0001))
        }
        val image = T3RootParser.parse(source)
        val fnsd = image.blocks[0] as FnsdBlock
        fnsd.functionSets shouldBe listOf("T3Object", "t3General")
    }

    test("well-formed image containing an MHLS block dispatches it correctly") {
        val source = Buffer().apply {
            writeAll(preambleBinary())
            writeAll(blockBinary("MHLS", flags = 0x0000, data = mhlsBinary(0x1000u, 0x2000u)))
            writeAll(blockBinary("EOF ", flags = 0x0001))
        }
        val image = T3RootParser.parse(source)
        val mhls = image.blocks[0] as MhlsBlock
        mhls.methodAddresses shouldBe listOf(0x1000u, 0x2000u)
    }

    test("well-formed image containing a SINI block dispatches it correctly") {
        val source = Buffer().apply {
            writeAll(preambleBinary())
            writeAll(blockBinary("SINI", flags = 0x0000, data = siniBinary(0x500u, 0x0001u to 0x0010u.toUShort())))
            writeAll(blockBinary("EOF ", flags = 0x0001))
        }
        val image = T3RootParser.parse(source)
        val sini = image.blocks[0] as SiniBlock
        sini.staticCodePoolOffset shouldBe 0x500u
        sini.entries[0].objectId shouldBe 0x0001u
        sini.entries[0].propertyId shouldBe 0x0010u.toUShort()
    }

    test("well-formed image containing a SRCF block dispatches it correctly") {
        val source = Buffer().apply {
            writeAll(preambleBinary())
            writeAll(blockBinary("SRCF", flags = 0x0000, data = srcfBinary(Triple(0, "game.t", listOf(1u to 0x100u)))))
            writeAll(blockBinary("EOF ", flags = 0x0001))
        }
        val image = T3RootParser.parse(source)
        val srcf = image.blocks[0] as SrcfBlock
        srcf.fileRecords[0].filename shouldBe "game.t"
        srcf.fileRecords[0].lineRecords[0].lineNumber shouldBe 1u
    }

    test("well-formed image containing a SYMD block dispatches it correctly") {
        val source = Buffer().apply {
            writeAll(preambleBinary())
            writeAll(blockBinary("SYMD", flags = 0x0000, data = symdObjectBinary("myObj", 0x0042u)))
            writeAll(blockBinary("EOF ", flags = 0x0001))
        }
        val image = T3RootParser.parse(source)
        val symd = image.blocks[0] as SymdBlock
        symd.entries[0].name shouldBe "myObj"
        (symd.entries[0].value as T3DataHolder.ObjectRef).objectId shouldBe 0x0042u
    }

    test("well-formed image containing a GSYM block dispatches it correctly") {
        val source = Buffer().apply {
            writeAll(preambleBinary())
            writeAll(blockBinary("GSYM", flags = 0x0000, data = gsymFunctionBinary("myFunc", 0x1000u)))
            writeAll(blockBinary("EOF ", flags = 0x0001))
        }
        val image = T3RootParser.parse(source)
        val gsym = image.blocks[0] as GsymBlock
        (gsym.entries[0] as GsymEntry.Function).name shouldBe "myFunc"
        (gsym.entries[0] as GsymEntry.Function).codeOffset shouldBe 0x1000u
    }

    test("well-formed image containing a MACR block dispatches it correctly") {
        val source = Buffer().apply {
            writeAll(preambleBinary())
            writeAll(blockBinary("MACR", flags = 0x0000, data = macrBinary("VERSION", "3")))
            writeAll(blockBinary("EOF ", flags = 0x0001))
        }
        val image = T3RootParser.parse(source)
        val macr = image.blocks[0] as MacrBlock
        macr.entries[0].name shouldBe "VERSION"
        macr.entries[0].expansion shouldBe "3"
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

        test("source ending mid-MREL-block-body throws RiffletParseException") {
            val source = Buffer().apply {
                writeAll(preambleBinary())
                writeString("MREL", Charsets.ISO_8859_1)
                writeIntLe(100)
                writeShortLe(0x0000)
                write(byteArrayOf(0x01, 0x02))
            }
            shouldThrow<RiffletParseException> { T3RootParser.parse(source) }
        }

        test("source ending mid-FNSD-block-body throws RiffletParseException") {
            val source = Buffer().apply {
                writeAll(preambleBinary())
                writeString("FNSD", Charsets.ISO_8859_1)
                writeIntLe(100)
                writeShortLe(0x0001)
                write(byteArrayOf(0x01, 0x02))
            }
            shouldThrow<RiffletParseException> { T3RootParser.parse(source) }
        }

        test("source ending mid-MHLS-block-body throws RiffletParseException") {
            val source = Buffer().apply {
                writeAll(preambleBinary())
                writeString("MHLS", Charsets.ISO_8859_1)
                writeIntLe(100)
                writeShortLe(0x0000)
                write(byteArrayOf(0x01, 0x02))
            }
            shouldThrow<RiffletParseException> { T3RootParser.parse(source) }
        }

        test("source ending mid-SINI-block-body throws RiffletParseException") {
            val source = Buffer().apply {
                writeAll(preambleBinary())
                writeString("SINI", Charsets.ISO_8859_1)
                writeIntLe(100)
                writeShortLe(0x0000)
                write(byteArrayOf(0x01, 0x02))
            }
            shouldThrow<RiffletParseException> { T3RootParser.parse(source) }
        }

        test("source ending mid-SRCF-block-body throws RiffletParseException") {
            val source = Buffer().apply {
                writeAll(preambleBinary())
                writeString("SRCF", Charsets.ISO_8859_1)
                writeIntLe(100)
                writeShortLe(0x0000)
                write(byteArrayOf(0x01, 0x02))
            }
            shouldThrow<RiffletParseException> { T3RootParser.parse(source) }
        }

        test("source ending mid-SYMD-block-body throws RiffletParseException") {
            val source = Buffer().apply {
                writeAll(preambleBinary())
                writeString("SYMD", Charsets.ISO_8859_1)
                writeIntLe(100)
                writeShortLe(0x0000)
                write(byteArrayOf(0x01, 0x02))
            }
            shouldThrow<RiffletParseException> { T3RootParser.parse(source) }
        }

        test("source ending mid-GSYM-block-body throws RiffletParseException") {
            val source = Buffer().apply {
                writeAll(preambleBinary())
                writeString("GSYM", Charsets.ISO_8859_1)
                writeIntLe(100)
                writeShortLe(0x0000)
                write(byteArrayOf(0x01, 0x02))
            }
            shouldThrow<RiffletParseException> { T3RootParser.parse(source) }
        }

        test("source ending mid-MACR-block-body throws RiffletParseException") {
            val source = Buffer().apply {
                writeAll(preambleBinary())
                writeString("MACR", Charsets.ISO_8859_1)
                writeIntLe(100)
                writeShortLe(0x0000)
                write(byteArrayOf(0x01, 0x02))
            }
            shouldThrow<RiffletParseException> { T3RootParser.parse(source) }
        }
    }
})
