package com.kelvsyc.rifflet.internal.t3

import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.t3.SrcfBlock
import com.kelvsyc.rifflet.t3.SrcfFileRecord
import com.kelvsyc.rifflet.t3.SrcfLineRecord

internal object T3SrcfParser {
    private const val EXPECTED_LINE_RECORD_SIZE = 8

    fun parse(raw: T3RawBufferedBlock): SrcfBlock {
        val fileCount = raw.data.readShortLe().toInt() and 0xFFFF
        val lineRecordSize = raw.data.readShortLe().toInt() and 0xFFFF
        if (lineRecordSize != EXPECTED_LINE_RECORD_SIZE)
            throw RiffletParseException("SRCF lineRecordSize $lineRecordSize is not the expected $EXPECTED_LINE_RECORD_SIZE")
        val fileRecords = buildList {
            repeat(fileCount) {
                val sizeBeforeEntry = raw.data.size
                val entrySize = raw.data.readIntLe()
                val masterFileIndex = raw.data.readShortLe().toInt() and 0xFFFF
                val filenameLen = raw.data.readShortLe().toInt() and 0xFFFF
                val filename = raw.data.readString(filenameLen.toLong(), Charsets.UTF_8)
                val lineCount = raw.data.readIntLe()
                val lineRecords = buildList {
                    repeat(lineCount) {
                        val lineNumber = raw.data.readIntLe().toUInt()
                        val codeOffset = raw.data.readIntLe().toUInt()
                        add(SrcfLineRecord(lineNumber, codeOffset))
                    }
                }
                val bytesConsumed = (sizeBeforeEntry - raw.data.size).toInt()
                if (bytesConsumed != entrySize)
                    throw RiffletParseException("SRCF file record declared size $entrySize but $bytesConsumed bytes were consumed")
                add(SrcfFileRecord(masterFileIndex, filename, lineRecords))
            }
        }
        return SrcfBlock(fileRecords)
    }
}
