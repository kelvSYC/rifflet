package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.GoodEntry
import com.kelvsyc.rifflet.civ3.GoodResourceType
import okio.Buffer

/**
 * Parses one `GOOD` item, per existing reverse-engineering documentation of the BIX/BIQ format. Reads directly off
 * `item`, a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop.
 *
 * Every field in this section is present regardless of [Civ3FormatEra].
 */
internal object GoodEntryParser {
    fun parse(item: Buffer): GoodEntry {
        val name = item.readByteString(24L).truncateAtFirstNull()
        val civilopediaEntry = item.readByteString(32L).truncateAtFirstNull()
        val type = decodeEnum("GoodEntry.type", item.readIntLe(), GoodResourceType.entries)
        val appearanceRatio = item.readIntLe()
        val disappearanceProbability = item.readIntLe()
        val icon = item.readIntLe()
        val prerequisite = item.readIntLe()
        val foodBonus = item.readIntLe()
        val shieldsBonus = item.readIntLe()
        val commerceBonus = item.readIntLe()
        return GoodEntry(
            name,
            civilopediaEntry,
            type,
            appearanceRatio,
            disappearanceProbability,
            icon,
            prerequisite,
            foodBonus,
            shieldsBonus,
            commerceBonus,
        )
    }
}
