package com.kelvsyc.rifflet.internal.t3

import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.t3.SiniBlock
import com.kelvsyc.rifflet.t3.SiniEntry

internal object T3SiniParser {
    private const val EXPECTED_HEADER_SIZE = 12

    fun parse(raw: T3RawBufferedBlock): SiniBlock {
        val headerSize = raw.data.readIntLe()
        if (headerSize != EXPECTED_HEADER_SIZE)
            throw RiffletParseException("SINI header size $headerSize is not the expected $EXPECTED_HEADER_SIZE")
        val staticCodePoolOffset = raw.data.readIntLe().toUInt()
        val count = raw.data.readIntLe()
        val entries = buildList {
            repeat(count) {
                val objectId = raw.data.readIntLe().toUInt()
                val propertyId = raw.data.readShortLe().toUShort()
                add(SiniEntry(objectId, propertyId))
            }
        }
        return SiniBlock(staticCodePoolOffset, entries)
    }
}
