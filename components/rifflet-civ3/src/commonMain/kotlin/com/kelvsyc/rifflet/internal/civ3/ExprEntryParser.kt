package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.ExprEntry
import okio.Buffer

/**
 * Parses one `EXPR` item, per the Apolyton BIX/BIQ format documentation. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop.
 *
 * Every field in this section is confirmed present in every real sample regardless of
 * [Civ3FormatEra].
 */
internal object ExprEntryParser {
    fun parse(item: Buffer): ExprEntry {
        val name = item.readByteString(32L).truncateAtFirstNull()
        val baseHitPoints = item.readIntLe()
        val retreatBonus = item.readIntLe()
        return ExprEntry(name, baseHitPoints, retreatBonus)
    }
}
