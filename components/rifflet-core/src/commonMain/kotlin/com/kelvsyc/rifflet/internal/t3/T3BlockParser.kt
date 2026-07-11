package com.kelvsyc.rifflet.internal.t3

import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.t3.EndBlock
import com.kelvsyc.rifflet.t3.EntryPointBlock
import com.kelvsyc.rifflet.t3.T3Block
import com.kelvsyc.rifflet.t3.T3BlockIds
import com.kelvsyc.rifflet.t3.T3RawBlock

/**
 * Dispatches an already-framed [T3RawBufferedBlock] to its typed [T3Block] representation.
 *
 * T3's block-type vocabulary is closed and fully enumerated by the spec, so dispatch is a fixed
 * `when` rather than a caller-supplied registry.
 */
internal object T3BlockParser {
    private const val ENTP_V1_SIZE = 16u
    private const val ENTP_V2_SIZE = 18u

    fun parse(raw: T3RawBufferedBlock): T3Block = when (raw.type) {
        T3BlockIds.ENTP -> parseEntryPoint(raw)
        T3BlockIds.EOF -> parseEnd(raw)
        T3BlockIds.CPDF -> T3CpdfParser.parse(raw)
        T3BlockIds.CPPG -> T3CppgParser.parse(raw)
        T3BlockIds.FNSD -> T3FnsdParser.parse(raw)
        T3BlockIds.GSYM -> T3GsymParser.parse(raw)
        T3BlockIds.MACR -> T3MacrParser.parse(raw)
        T3BlockIds.MHLS -> T3MhlsParser.parse(raw)
        T3BlockIds.MRES -> T3MresParser.parse(raw)
        T3BlockIds.MREL -> T3MrelParser.parse(raw)
        T3BlockIds.OBJS -> T3ObjsParser.parse(raw)
        T3BlockIds.SINI -> T3SiniParser.parse(raw)
        T3BlockIds.SRCF -> T3SrcfParser.parse(raw)
        T3BlockIds.SYMD -> T3SymdParser.parse(raw)
        else -> parseRaw(raw)
    }

    private fun parseRaw(raw: T3RawBufferedBlock): T3RawBlock {
        if (raw.flags and T3BlockIds.MANDATORY_FLAG != 0)
            throw RiffletParseException("Mandatory block type '${raw.type.name}' is not recognized")
        return T3RawBlock(raw.type, raw.flags, raw.data.readByteString())
    }

    private fun parseEnd(raw: T3RawBufferedBlock): EndBlock {
        if (raw.declaredSize != 0u)
            throw RiffletParseException("EOF block must be empty but declared size was ${raw.declaredSize}")
        return EndBlock
    }

    private fun parseEntryPoint(raw: T3RawBufferedBlock): EntryPointBlock {
        if (raw.declaredSize != ENTP_V1_SIZE && raw.declaredSize != ENTP_V2_SIZE)
            throw RiffletParseException("ENTP block has unexpected size ${raw.declaredSize}")
        val data = raw.data
        val codePoolEntryPointOffset = data.readIntLe().toUInt()
        val methodHeaderSize = data.readShortLe().toInt() and 0xFFFF
        val exceptionTableEntrySize = data.readShortLe().toInt() and 0xFFFF
        val debuggerLineTableEntrySize = data.readShortLe().toInt() and 0xFFFF
        val debugTableHeaderSize = data.readShortLe().toInt() and 0xFFFF
        val localSymbolRecordHeaderSize = data.readShortLe().toInt() and 0xFFFF
        val debugRecordsVersion = data.readShortLe().toInt() and 0xFFFF
        val debugTableFrameHeaderSize =
            if (raw.declaredSize == ENTP_V2_SIZE) data.readShortLe().toInt() and 0xFFFF else null
        return EntryPointBlock(
            codePoolEntryPointOffset,
            methodHeaderSize,
            exceptionTableEntrySize,
            debuggerLineTableEntrySize,
            debugTableHeaderSize,
            localSymbolRecordHeaderSize,
            debugRecordsVersion,
            debugTableFrameHeaderSize,
        )
    }
}
