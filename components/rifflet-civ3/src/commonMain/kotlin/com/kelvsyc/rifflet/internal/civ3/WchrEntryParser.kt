package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Age
import com.kelvsyc.rifflet.civ3.BarbarianActivity
import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.Climate
import com.kelvsyc.rifflet.civ3.Landform
import com.kelvsyc.rifflet.civ3.OceanCoverage
import com.kelvsyc.rifflet.civ3.Temperature
import com.kelvsyc.rifflet.civ3.WchrEntry
import okio.Buffer

/**
 * Parses one `WCHR` item, per existing reverse-engineering documentation of the BIX/BIQ format. Reads directly off
 * `item`, a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop.
 *
 * Every field in this section is present regardless of [Civ3FormatEra].
 */
internal object WchrEntryParser {
    fun parse(item: Buffer): WchrEntry {
        val selectedClimate = decodeEnum("WchrEntry.selectedClimate", item.readIntLe(), Climate.entries)
        val actualClimate = decodeEnum("WchrEntry.actualClimate", item.readIntLe(), Climate.entries)
        val selectedBarbarianActivity =
            decodeEnum("WchrEntry.selectedBarbarianActivity", item.readIntLe(), BarbarianActivity.entries) { it + 1 }
        val actualBarbarianActivity =
            decodeEnum("WchrEntry.actualBarbarianActivity", item.readIntLe(), BarbarianActivity.entries) { it + 1 }
        val selectedLandform = decodeEnum("WchrEntry.selectedLandform", item.readIntLe(), Landform.entries)
        val actualLandform = decodeEnum("WchrEntry.actualLandform", item.readIntLe(), Landform.entries)
        val selectedOceanCoverage = decodeEnum("WchrEntry.selectedOceanCoverage", item.readIntLe(), OceanCoverage.entries)
        val actualOceanCoverage = decodeEnum("WchrEntry.actualOceanCoverage", item.readIntLe(), OceanCoverage.entries)
        val selectedTemperature = decodeEnum("WchrEntry.selectedTemperature", item.readIntLe(), Temperature.entries)
        val actualTemperature = decodeEnum("WchrEntry.actualTemperature", item.readIntLe(), Temperature.entries)
        val selectedAge = decodeEnum("WchrEntry.selectedAge", item.readIntLe(), Age.entries)
        val actualAge = decodeEnum("WchrEntry.actualAge", item.readIntLe(), Age.entries)
        val worldSize = item.readIntLe()
        return WchrEntry(
            selectedClimate,
            actualClimate,
            selectedBarbarianActivity,
            actualBarbarianActivity,
            selectedLandform,
            actualLandform,
            selectedOceanCoverage,
            actualOceanCoverage,
            selectedTemperature,
            actualTemperature,
            selectedAge,
            actualAge,
            worldSize,
        )
    }
}
