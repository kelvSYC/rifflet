package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.ExprEntry
import okio.Buffer

/**
 * Parses one `EXPR` item, per the Apolyton BIX/BIQ format documentation. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop.
 */
internal object ExprEntryParser {
    fun parse(item: Buffer): ExprEntry {
        val name = item.readByteString(32L).truncateAtFirstNull()
        val baseHitPoints = item.readIntLe()
        val retreatBonus = item.readIntLe()
        return ExprEntry(name, baseHitPoints, retreatBonus)
    }
}
