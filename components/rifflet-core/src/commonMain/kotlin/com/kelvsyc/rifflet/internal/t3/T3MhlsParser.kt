package com.kelvsyc.rifflet.internal.t3

import com.kelvsyc.rifflet.t3.MhlsBlock

internal object T3MhlsParser {
    fun parse(raw: T3RawBufferedBlock): MhlsBlock {
        val count = raw.data.readIntLe()
        val addresses = buildList {
            repeat(count) {
                add(raw.data.readIntLe().toUInt())
            }
        }
        return MhlsBlock(addresses)
    }
}
