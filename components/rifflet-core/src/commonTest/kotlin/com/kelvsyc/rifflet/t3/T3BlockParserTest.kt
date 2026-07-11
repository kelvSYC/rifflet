package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.internal.t3.T3BlockParser
import com.kelvsyc.rifflet.internal.t3.T3RawBufferedBlock
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer

private fun rawBlock(type: ChunkId, flags: Int, body: Buffer): T3RawBufferedBlock =
    T3RawBufferedBlock(type, flags, body, body.size.toUInt())

class T3BlockParserTest : FunSpec({

    context("ENTP block") {
        test("v2 (18-byte) body decodes all seven fields") {
            val body = Buffer().apply {
                writeIntLe(0x1000)
                writeShortLe(10) // method header size
                writeShortLe(12) // exception table entry size
                writeShortLe(14) // debugger line table entry size
                writeShortLe(16) // debug table header size
                writeShortLe(18) // local symbol record header size
                writeShortLe(2)  // debug records version
                writeShortLe(20) // debug table frame header size (v2 only)
            }
            val block = T3BlockParser.parse(rawBlock(T3BlockIds.ENTP, 0x0001, body)) as EntryPointBlock
            block.codePoolEntryPointOffset shouldBe 0x1000u
            block.methodHeaderSize shouldBe 10
            block.exceptionTableEntrySize shouldBe 12
            block.debuggerLineTableEntrySize shouldBe 14
            block.debugTableHeaderSize shouldBe 16
            block.localSymbolRecordHeaderSize shouldBe 18
            block.debugRecordsVersion shouldBe 2
            block.debugTableFrameHeaderSize shouldBe 20
        }

        test("v1 (16-byte) body leaves the trailing field null") {
            val body = Buffer().apply {
                writeIntLe(0x1000)
                writeShortLe(10)
                writeShortLe(12)
                writeShortLe(14)
                writeShortLe(16)
                writeShortLe(18)
                writeShortLe(1)
            }
            val block = T3BlockParser.parse(rawBlock(T3BlockIds.ENTP, 0x0001, body)) as EntryPointBlock
            block.debugRecordsVersion shouldBe 1
            block.debugTableFrameHeaderSize shouldBe null
        }

        test("unexpected body size throws RiffletParseException") {
            val body = Buffer().apply { writeIntLe(0x1000) } // way too short
            shouldThrow<RiffletParseException> { T3BlockParser.parse(rawBlock(T3BlockIds.ENTP, 0x0001, body)) }
        }
    }

    context("EOF block") {
        test("empty body produces EndBlock") {
            T3BlockParser.parse(rawBlock(T3BlockIds.EOF, 0x0001, Buffer())) shouldBe EndBlock
        }

        test("non-empty body throws RiffletParseException") {
            val body = Buffer().apply { writeByte(0x00) }
            shouldThrow<RiffletParseException> { T3BlockParser.parse(rawBlock(T3BlockIds.EOF, 0x0001, body)) }
        }
    }

    context("unmodeled block type") {
        test("non-mandatory block is preserved as T3RawBlock") {
            val body = Buffer().apply { writeString("payload", Charsets.ISO_8859_1) }
            val block = T3BlockParser.parse(rawBlock(T3BlockIds.MCLD, 0x0000, body)) as T3RawBlock
            block.type shouldBe T3BlockIds.MCLD
            block.flags shouldBe 0x0000
            block.data.utf8() shouldBe "payload"
        }

        test("mandatory-but-unrecognized block throws RiffletParseException") {
            val body = Buffer().apply { writeString("payload", Charsets.ISO_8859_1) }
            shouldThrow<RiffletParseException> { T3BlockParser.parse(rawBlock(T3BlockIds.MCLD, 0x0001, body)) }
        }
    }
})
