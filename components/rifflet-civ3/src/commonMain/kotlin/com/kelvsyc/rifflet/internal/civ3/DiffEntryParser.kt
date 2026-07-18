package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.DiffEntry
import okio.Buffer

/**
 * Parses one `DIFF` item, per the Apolyton BIX/BIQ format documentation. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop.
 *
 * The trailing field (`militaryLaw`) is read defensively: real vanilla files (major=4, confirmed
 * against a genuine default-rules `.bic` file) omit it entirely — the 116-byte vanilla record is
 * an exact prefix of the 120-byte PTW/Conquests record — so the read checks `item.size` first and
 * defaults when absent, matching `BldgEntryParser`/`CtznEntryParser`/`TileEntryParser`'s
 * established length-aware defensive parsing pattern.
 */
internal object DiffEntryParser {
    fun parse(item: Buffer): DiffEntry {
        val name = item.readByteString(64L).truncateAtFirstNull()
        val numberOfCitizensBornContent = item.readIntLe()
        val maxGovernmentTransitionTime = item.readIntLe()
        val numberOfAiDefensiveStartingUnits = item.readIntLe()
        val numberOfAiOffensiveStartingUnits = item.readIntLe()
        val extraStartUnit1 = item.readIntLe()
        val extraStartUnit2 = item.readIntLe()
        val additionalFreeSupport = item.readIntLe()
        val unitSupportBonusForEachSettlement = item.readIntLe()
        val attackBonusAgainstBarbarians = item.readIntLe()
        val costFactor = item.readIntLe()
        val percentageOfOptimalCities = item.readIntLe()
        val aiToAiTradeRate = item.readIntLe()
        val corruptionPercentage = item.readIntLe()
        val militaryLaw = if (item.size >= 4L) item.readIntLe() else 0
        return DiffEntry(
            name,
            numberOfCitizensBornContent,
            maxGovernmentTransitionTime,
            numberOfAiDefensiveStartingUnits,
            numberOfAiOffensiveStartingUnits,
            extraStartUnit1,
            extraStartUnit2,
            additionalFreeSupport,
            unitSupportBonusForEachSettlement,
            attackBonusAgainstBarbarians,
            costFactor,
            percentageOfOptimalCities,
            aiToAiTradeRate,
            corruptionPercentage,
            militaryLaw,
        )
    }
}
