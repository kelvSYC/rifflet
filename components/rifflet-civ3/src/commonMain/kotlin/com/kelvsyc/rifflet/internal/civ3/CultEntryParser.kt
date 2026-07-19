package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.CultEntry
import okio.Buffer

/**
 * Parses one `CULT` item, per the Apolyton BIX/BIQ format documentation. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop.
 *
 * Every field in this section is confirmed present in every real sample regardless of
 * [Civ3FormatEra].
 */
internal object CultEntryParser {
    fun parse(item: Buffer): CultEntry {
        val name = item.readByteString(64L).truncateAtFirstNull()
        val chanceOfSuccessfulPropaganda = item.readIntLe()
        val cultureRatioPercentage = item.readIntLe()
        val cultureRatioDenominator = item.readIntLe()
        val cultureRatioNumerator = item.readIntLe()
        val initialResistanceChance = item.readIntLe()
        val continuedResistanceChance = item.readIntLe()
        return CultEntry(
            name,
            chanceOfSuccessfulPropaganda,
            cultureRatioPercentage,
            cultureRatioDenominator,
            cultureRatioNumerator,
            initialResistanceChance,
            continuedResistanceChance,
        )
    }
}
