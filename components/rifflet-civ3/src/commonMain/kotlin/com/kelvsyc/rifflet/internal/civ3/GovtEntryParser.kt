package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.GovtEntry
import okio.Buffer

/**
 * Parses one `GOVT` item, per existing reverse-engineering documentation of the BIX/BIQ format. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop. The embedded `numberOfGovernments`-sized relationship array is read as
 * a loop resuming the same cursor; `numberOfGovernments` itself is not stored on [GovtEntry] —
 * `relationships.size` is already that count. The 8 ruler-title fields are always read
 * unconditionally — they do not vary with any other section's entry count.
 *
 * The trailing 2 fields (`xenophobic`, `forceResettle`) are read defensively:
 * [Civ3FormatEra.VANILLA] (`major=4`) and [Civ3FormatEra.PTW] (`major=11`) files omit them
 * entirely, so each read checks `item.size` first and defaults when absent, matching
 * `BldgEntryParser`/`CtznEntryParser`/`DiffEntryParser`/`ErasEntryParser`'s established
 * length-aware defensive parsing pattern. No per-minor breakdown within [Civ3FormatEra.PTW] is
 * recorded — the available samples don't distinguish one.
 *
 * `numberOfGovernments` is validated via [requireSaneCount] before sizing
 * [GovtEntry.relationships] — see that function's KDoc for why.
 */
internal object GovtEntryParser {
    fun parse(item: Buffer): GovtEntry {
        val defaultType = item.readIntLe()
        val transitionType = item.readIntLe()
        val requiresMaintenance = item.readIntLe()
        val toggle1 = item.readIntLe()
        val tilePenalty = item.readIntLe()
        val tradeBonus = item.readIntLe()
        val name = item.readByteString(64L).truncateAtFirstNull()
        val civilopediaEntry = item.readByteString(32L).truncateAtFirstNull()
        val maleRulerTitle1 = item.readByteString(32L).truncateAtFirstNull()
        val femaleRulerTitle1 = item.readByteString(32L).truncateAtFirstNull()
        val maleRulerTitle2 = item.readByteString(32L).truncateAtFirstNull()
        val femaleRulerTitle2 = item.readByteString(32L).truncateAtFirstNull()
        val maleRulerTitle3 = item.readByteString(32L).truncateAtFirstNull()
        val femaleRulerTitle3 = item.readByteString(32L).truncateAtFirstNull()
        val maleRulerTitle4 = item.readByteString(32L).truncateAtFirstNull()
        val femaleRulerTitle4 = item.readByteString(32L).truncateAtFirstNull()
        val corruption = item.readIntLe()
        val immuneTo = item.readIntLe()
        val diplomatsAre = item.readIntLe()
        val spiesAre = item.readIntLe()
        val numberOfGovernments = item.requireSaneCount(item.readIntLe(), 12L, "GovtEntry.relationships")
        val relationships = List(numberOfGovernments) { GovtRelationshipParser.parse(item) }
        val hurrying = item.readIntLe()
        val assimilationChance = item.readIntLe()
        val draftLimit = item.readIntLe()
        val militaryPoliceLimit = item.readIntLe()
        val rulerTitlePairsUsed = item.readIntLe()
        val prerequisiteTechnology = item.readIntLe()
        val scienceRateCap = item.readIntLe()
        val workerRate = item.readIntLe()
        val toggle2 = item.readIntLe()
        val toggle3 = item.readIntLe()
        val unknown = item.readByteString(4L)
        val freeUnits = item.readIntLe()
        val freeUnitsPerTown = item.readIntLe()
        val freeUnitsPerCity = item.readIntLe()
        val freeUnitsPerMetropolis = item.readIntLe()
        val unitCost = item.readIntLe()
        val warWeariness = item.readIntLe()
        val xenophobic = if (item.size >= 4L) item.readIntLe() else 0
        val forceResettle = if (item.size >= 4L) item.readIntLe() else 0
        return GovtEntry(
            defaultType,
            transitionType,
            requiresMaintenance,
            toggle1,
            tilePenalty,
            tradeBonus,
            name,
            civilopediaEntry,
            maleRulerTitle1,
            femaleRulerTitle1,
            maleRulerTitle2,
            femaleRulerTitle2,
            maleRulerTitle3,
            femaleRulerTitle3,
            maleRulerTitle4,
            femaleRulerTitle4,
            corruption,
            immuneTo,
            diplomatsAre,
            spiesAre,
            relationships,
            hurrying,
            assimilationChance,
            draftLimit,
            militaryPoliceLimit,
            rulerTitlePairsUsed,
            prerequisiteTechnology,
            scienceRateCap,
            workerRate,
            toggle2,
            toggle3,
            unknown,
            freeUnits,
            freeUnitsPerTown,
            freeUnitsPerCity,
            freeUnitsPerMetropolis,
            unitCost,
            warWeariness,
            xenophobic,
            forceResettle,
        )
    }
}
