package com.kelvsyc.rifflet.internal.t3

import com.kelvsyc.rifflet.t3.MrelBlock
import com.kelvsyc.rifflet.t3.MrelEntry

/**
 * Parses an already-framed `MREL` block: an entry count, then per-entry name/filename mappings.
 *
 * Unlike `MRES`, there are no offset/size fields (no embedded data to bounds-check), and names
 * are stored plainly, not XOR'd.
 */
internal object T3MrelParser {
    fun parse(raw: T3RawBufferedBlock): MrelBlock {
        val entryCount = raw.data.readShortLe().toInt() and 0xFFFF
        val entries = buildList {
            repeat(entryCount) {
                val name = readLengthPrefixedString(raw)
                val filename = readLengthPrefixedString(raw)
                add(MrelEntry(name, filename))
            }
        }
        return MrelBlock(entries)
    }

    private fun readLengthPrefixedString(raw: T3RawBufferedBlock): String {
        val len = raw.data.readByte().toInt() and 0xFF
        val bytes = raw.data.readByteArray(len.toLong())
        return buildString {
            for (b in bytes) append((b.toInt() and 0xFF).toChar())
        }
    }
}
