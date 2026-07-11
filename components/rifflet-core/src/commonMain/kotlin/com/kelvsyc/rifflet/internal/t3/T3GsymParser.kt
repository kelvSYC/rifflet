package com.kelvsyc.rifflet.internal.t3

import com.kelvsyc.rifflet.t3.GsymBlock
import com.kelvsyc.rifflet.t3.GsymEntry
import okio.Buffer
import okio.ByteString

internal object T3GsymParser {
    fun parse(raw: T3RawBufferedBlock): GsymBlock {
        val count = raw.data.readIntLe()
        val entries = buildList {
            repeat(count) {
                val nameLen = raw.data.readShortLe().toInt() and 0xFFFF
                val extraDataLen = raw.data.readShortLe().toInt() and 0xFFFF
                val typeCode = raw.data.readShortLe().toInt() and 0xFFFF
                val name = raw.data.readString(nameLen.toLong(), Charsets.UTF_8)
                val extraBytes = raw.data.readByteArray(extraDataLen.toLong())
                add(parseEntry(typeCode, name, extraBytes))
            }
        }
        return GsymBlock(entries)
    }

    private fun parseEntry(typeCode: Int, name: String, extraBytes: ByteArray): GsymEntry {
        val extra = Buffer().write(extraBytes)
        return when (typeCode) {
            1 -> GsymEntry.Function(
                name = name,
                codeOffset = extra.readIntLe().toUInt(),
                argCount = extra.readShortLe().toInt() and 0xFFFF,
                isVarArgs = extra.readByte().toInt() != 0,
                hasReturn = extra.readByte().toInt() != 0,
                optionalArgCount = extra.readShortLe().toInt() and 0xFFFF,
            )
            2 -> GsymEntry.Object(
                name = name,
                objectId = extra.readIntLe().toUInt(),
                modifyingObjectId = extra.readIntLe().toUInt(),
            )
            3 -> GsymEntry.Property(
                name = name,
                propertyId = extra.readShortLe().toUShort(),
                flags = extra.readByte().toInt() and 0xFF,
            )
            6 -> GsymEntry.IntrinsicFunction(
                name = name,
                functionIndex = extra.readShortLe().toInt() and 0xFFFF,
                functionSetIndex = extra.readShortLe().toInt() and 0xFFFF,
                hasReturn = extra.readByte().toInt() != 0,
                minArgCount = extra.readShortLe().toInt() and 0xFFFF,
                maxArgCount = extra.readShortLe().toInt() and 0xFFFF,
                isVarArgs = extra.readByte().toInt() != 0,
            )
            9 -> GsymEntry.IntrinsicClass(
                name = name,
                metaclassIndex = extra.readShortLe().toInt() and 0xFFFF,
                intrinsicClassObjectId = extra.readIntLe().toUInt(),
            )
            10 -> GsymEntry.EnumeratorValue(
                name = name,
                enumeratorId = extra.readIntLe().toUInt(),
                flags = extra.readByte().toInt() and 0xFF,
            )
            else -> GsymEntry.Unknown(name, typeCode, ByteString.of(*extraBytes))
        }
    }
}
