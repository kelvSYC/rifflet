package com.kelvsyc.rifflet.internal.t3

import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.t3.MresBlock
import com.kelvsyc.rifflet.t3.MresEntry

/**
 * Parses an already-framed `MRES` block: a table of contents (entry count, then per-entry
 * offset/size/name), followed immediately by the resources' binary data, laid out contiguously
 * in TOC order.
 *
 * Resource names are 7-bit ASCII, each byte XOR'd with 0xFF to obscure them from casual
 * inspection.
 */
internal object T3MresParser {
    private const val NAME_XOR_MASK = 0xFF

    fun parse(raw: T3RawBufferedBlock): MresBlock {
        // Snapshot the whole block body once, non-destructively, so each entry can slice its own
        // data out of it lazily later. `.copy()` duplicates the Buffer without consuming
        // `raw.data`, which is read sequentially below to walk the TOC.
        val body = raw.data.copy().readByteString()
        val entryCount = raw.data.readShortLe().toInt() and 0xFFFF
        val entries = buildList {
            repeat(entryCount) {
                val offset = raw.data.readIntLe().toUInt()
                val size = raw.data.readIntLe().toUInt()
                val nameLen = raw.data.readByte().toInt() and 0xFF
                val nameBytes = raw.data.readByteArray(nameLen.toLong())
                val name = buildString {
                    for (b in nameBytes) append(((b.toInt() xor NAME_XOR_MASK) and 0xFF).toChar())
                }
                if (offset.toULong() + size.toULong() > body.size.toULong())
                    throw RiffletParseException("MRES entry '$name' data range exceeds block size")
                add(MresEntry(name, offset, size, body))
            }
        }
        return MresBlock(entries)
    }
}
