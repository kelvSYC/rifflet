package com.kelvsyc.rifflet.internal.t3

import com.kelvsyc.rifflet.t3.MacrBlock
import com.kelvsyc.rifflet.t3.MacrEntry
import com.kelvsyc.rifflet.t3.MacrParam

internal object T3MacrParser {
    fun parse(raw: T3RawBufferedBlock): MacrBlock {
        val count = raw.data.readIntLe()
        val entries = buildList {
            repeat(count) {
                val nameLen = raw.data.readShortLe().toInt() and 0xFFFF
                val name = raw.data.readString(nameLen.toLong(), Charsets.UTF_8)
                val flags = raw.data.readShortLe().toInt() and 0xFFFF
                val isFunctionLike = flags and 0x0001 != 0
                val isVarArgs = flags and 0x0002 != 0
                val paramCount = raw.data.readShortLe().toInt() and 0xFFFF
                val params = buildList {
                    repeat(paramCount) {
                        val paramNameLen = raw.data.readShortLe().toInt() and 0xFFFF
                        add(MacrParam(raw.data.readString(paramNameLen.toLong(), Charsets.UTF_8)))
                    }
                }
                val expansionLen = raw.data.readIntLe()
                val expansion = raw.data.readString(expansionLen.toLong(), Charsets.UTF_8)
                add(MacrEntry(name, isFunctionLike, isVarArgs, params, expansion))
            }
        }
        return MacrBlock(entries)
    }
}
