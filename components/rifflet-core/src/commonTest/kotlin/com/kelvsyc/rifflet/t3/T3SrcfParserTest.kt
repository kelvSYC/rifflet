package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.internal.t3.T3SrcfParser
import com.kelvsyc.rifflet.internal.t3.T3RawBufferedBlock
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

private fun rawBlock(body: Buffer): T3RawBufferedBlock =
    T3RawBufferedBlock(T3BlockIds.SRCF, 0x0000, body, body.size.toUInt())

/** Builds an SRCF block body. Each file is (masterFileIndex, filename, listOf(lineNumber to codeOffset, ...)). */
private fun srcfBinary(
    vararg files: Triple<Int, String, List<Pair<UInt, UInt>>>,
    lineRecordSize: Int = 8,
): Buffer = Buffer().apply {
    writeShortLe(files.size)
    writeShortLe(lineRecordSize)
    for ((masterIndex, filename, lines) in files) {
        val filenameBytes = filename.toByteArray(Charsets.UTF_8)
        // entrySize includes the 4-byte entrySize field itself
        val entrySize = 4 + 2 + 2 + filenameBytes.size + 4 + lines.size * 8
        writeIntLe(entrySize)
        writeShortLe(masterIndex)
        writeShortLe(filenameBytes.size)
        write(filenameBytes)
        writeIntLe(lines.size)
        for ((lineNum, codeOfs) in lines) {
            writeIntLe(lineNum.toInt())
            writeIntLe(codeOfs.toInt())
        }
    }
}

class T3SrcfParserTest : FunSpec({
    test("zero files produces empty list") {
        val block = T3SrcfParser.parse(rawBlock(srcfBinary()))
        block.fileRecords shouldBe emptyList()
    }

    test("single file with line records is parsed correctly") {
        val block = T3SrcfParser.parse(rawBlock(srcfBinary(
            Triple(0, "game.t", listOf(1u to 0x0100u, 5u to 0x0200u)),
        )))
        block.fileRecords.size shouldBe 1
        val record = block.fileRecords[0]
        record.masterFileIndex shouldBe 0
        record.filename shouldBe "game.t"
        record.lineRecords.size shouldBe 2
        record.lineRecords[0].lineNumber shouldBe 1u
        record.lineRecords[0].codeOffset shouldBe 0x0100u
        record.lineRecords[1].lineNumber shouldBe 5u
        record.lineRecords[1].codeOffset shouldBe 0x0200u
    }

    test("multiple files are all parsed correctly") {
        val block = T3SrcfParser.parse(rawBlock(srcfBinary(
            Triple(0, "main.t", listOf(1u to 0x100u)),
            Triple(0, "lib.t", listOf(10u to 0x200u)),
        )))
        block.fileRecords.size shouldBe 2
        block.fileRecords[0].filename shouldBe "main.t"
        block.fileRecords[1].filename shouldBe "lib.t"
    }

    test("lineRecordSize not 8 throws RiffletParseException") {
        shouldThrow<RiffletParseException> {
            T3SrcfParser.parse(rawBlock(srcfBinary(lineRecordSize = 12)))
        }
    }

    test("entrySize mismatch throws RiffletParseException") {
        val body = Buffer().apply {
            writeShortLe(1)   // 1 file
            writeShortLe(8)   // lineRecordSize = 8
            val filenameBytes = "x.t".toByteArray(Charsets.UTF_8)
            writeIntLe(999)   // wrong entrySize — actual consumed bytes = 4+2+2+3+4 = 15
            writeShortLe(0)   // masterFileIndex
            writeShortLe(filenameBytes.size)
            write(filenameBytes)
            writeIntLe(0)     // 0 line records
        }
        shouldThrow<RiffletParseException> {
            T3SrcfParser.parse(rawBlock(body))
        }
    }
})
