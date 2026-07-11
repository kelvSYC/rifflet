package com.kelvsyc.rifflet.internal.t3

import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.t3.SymdBlock
import com.kelvsyc.rifflet.t3.SymdEntry
import com.kelvsyc.rifflet.t3.T3DataHolder

internal object T3SymdParser {
    fun parse(raw: T3RawBufferedBlock): SymdBlock {
        val count = raw.data.readShortLe().toInt() and 0xFFFF
        val entries = buildList {
            repeat(count) {
                val typeTag = raw.data.readByte().toInt() and 0xFF
                val value = parseDataHolder(typeTag, raw)
                val nameLen = raw.data.readByte().toInt() and 0xFF
                val nameBytes = raw.data.readByteArray(nameLen.toLong())
                val name = buildString { for (b in nameBytes) append((b.toInt() and 0xFF).toChar()) }
                add(SymdEntry(value, name))
            }
        }
        return SymdBlock(entries)
    }

    private fun parseDataHolder(typeTag: Int, raw: T3RawBufferedBlock): T3DataHolder = when (typeTag) {
        1 -> { raw.data.skip(4); T3DataHolder.Nil }
        2 -> { raw.data.skip(4); T3DataHolder.True }
        13 -> { raw.data.skip(4); T3DataHolder.Empty }
        5 -> T3DataHolder.ObjectRef(raw.data.readIntLe().toUInt())
        6 -> {
            val propertyId = raw.data.readShortLe().toUShort()
            raw.data.skip(2)  // high 2 bytes of the 4-byte value field are unused for UINT2
            T3DataHolder.PropertyRef(propertyId)
        }
        7 -> T3DataHolder.IntValue(raw.data.readIntLe())
        8 -> T3DataHolder.SingleQuotedStringRef(raw.data.readIntLe().toUInt())
        9 -> T3DataHolder.DoubleQuotedStringRef(raw.data.readIntLe().toUInt())
        10 -> T3DataHolder.ListRef(raw.data.readIntLe().toUInt())
        11 -> T3DataHolder.CodeOffset(raw.data.readIntLe().toUInt())
        12 -> T3DataHolder.FuncPtr(raw.data.readIntLe().toUInt())
        15 -> T3DataHolder.EnumValue(raw.data.readIntLe().toUInt())
        16 -> T3DataHolder.BuiltinFuncPtr(raw.data.readIntLe().toUInt())
        else -> throw RiffletParseException("Unknown T3DataHolder type tag: $typeTag")
    }
}
