package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.GovtEntry
import com.kelvsyc.rifflet.civ3.GovtRulerTitles
import com.kelvsyc.rifflet.civ3.GovtUnitSupportCosts
import okio.Buffer

/**
 * Parses one `GOVT` item, per existing reverse-engineering documentation of the BIX/BIQ format. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop. The embedded `numberOfGovernments`-sized relationship array is read as
 * a loop resuming the same cursor; `numberOfGovernments` itself is not stored on [GovtEntry] —
 * `relationships.size` is already that count. [GovtEntry.rulerTitles]'s 8 fields are always read
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
        val male1 = item.readByteString(32L).truncateAtFirstNull()
        val female1 = item.readByteString(32L).truncateAtFirstNull()
        val male2 = item.readByteString(32L).truncateAtFirstNull()
        val female2 = item.readByteString(32L).truncateAtFirstNull()
        val male3 = item.readByteString(32L).truncateAtFirstNull()
        val female3 = item.readByteString(32L).truncateAtFirstNull()
        val male4 = item.readByteString(32L).truncateAtFirstNull()
        val female4 = item.readByteString(32L).truncateAtFirstNull()
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
            defaultType = defaultType,
            transitionType = transitionType,
            requiresMaintenance = requiresMaintenance,
            toggle1 = toggle1,
            tilePenalty = tilePenalty,
            tradeBonus = tradeBonus,
            name = name,
            civilopediaEntry = civilopediaEntry,
            rulerTitles = GovtRulerTitles(male1, female1, male2, female2, male3, female3, male4, female4),
            corruption = corruption,
            immuneTo = immuneTo,
            diplomatsAre = diplomatsAre,
            spiesAre = spiesAre,
            relationships = relationships,
            hurrying = hurrying,
            assimilationChance = assimilationChance,
            draftLimit = draftLimit,
            militaryPoliceLimit = militaryPoliceLimit,
            rulerTitlePairsUsed = rulerTitlePairsUsed,
            prerequisiteTechnology = prerequisiteTechnology,
            scienceRateCap = scienceRateCap,
            workerRate = workerRate,
            toggle2 = toggle2,
            toggle3 = toggle3,
            unknown = unknown,
            unitSupportCosts = GovtUnitSupportCosts(
                freeUnits, freeUnitsPerTown, freeUnitsPerCity, freeUnitsPerMetropolis, unitCost,
            ),
            warWeariness = warWeariness,
            xenophobic = xenophobic,
            forceResettle = forceResettle,
        )
    }
}
