package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.GovtEntry
import com.kelvsyc.rifflet.civ3.GovtRelationship
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString

/** Writes [text] into [fieldSize] bytes, null-padding the remainder. */
private fun Buffer.writePaddedField(text: String, fieldSize: Int) {
    val start = size
    writeString(text, Charsets.US_ASCII)
    write(ByteArray((fieldSize - (size - start)).toInt()))
}

/** Builds a well-formed GOVT item body (length prefix excluded, as with prior sections).
 * [includeTrailingFields] controls whether the last 2 fields (`xenophobic`, `forceResettle`) are
 * written, matching the real vanilla/PTW (536 bytes) vs Conquests (568 bytes) split. */
private fun govtItemBinary(
    name: String = "Despotism",
    relationships: List<Triple<Int, Int, Int>> = listOf(Triple(1, 20, 30), Triple(0, 5, 10)),
    unknown: ByteArray = ByteArray(4),
    includeTrailingFields: Boolean = true,
): Buffer = Buffer().apply {
    writeIntLe(0) // defaultType
    writeIntLe(0) // transitionType
    writeIntLe(1) // requiresMaintenance
    writeIntLe(0) // toggle1
    writeIntLe(0) // tilePenalty
    writeIntLe(0) // tradeBonus
    writePaddedField(name, 64)
    writePaddedField("", 32) // civilopediaEntry
    repeat(8) { writePaddedField("", 32) } // 4 male/female ruler title pairs, always present
    writeIntLe(0) // corruption
    writeIntLe(0) // immuneTo
    writeIntLe(0) // diplomatsAre
    writeIntLe(0) // spiesAre
    writeIntLe(relationships.size) // numberOfGovernments
    relationships.forEach { (canBribe, briberyModifier, resistanceModifier) ->
        writeIntLe(canBribe)
        writeIntLe(briberyModifier)
        writeIntLe(resistanceModifier)
    }
    writeIntLe(0) // hurrying
    writeIntLe(0) // assimilationChance
    writeIntLe(0) // draftLimit
    writeIntLe(0) // militaryPoliceLimit
    writeIntLe(0) // rulerTitlePairsUsed
    writeIntLe(0) // prerequisiteTechnology
    writeIntLe(0) // scienceRateCap
    writeIntLe(0) // workerRate
    writeIntLe(-1) // toggle2
    writeIntLe(0) // toggle3
    write(unknown)
    writeIntLe(0) // freeUnits
    writeIntLe(0) // freeUnitsPerTown
    writeIntLe(0) // freeUnitsPerCity
    writeIntLe(0) // freeUnitsPerMetropolis
    writeIntLe(0) // unitCost
    writeIntLe(0) // warWeariness
    if (includeTrailingFields) {
        writeIntLe(0) // xenophobic
        writeIntLe(0) // forceResettle
    }
}

class GovtEntryParserTest : FunSpec({

    test("well-formed item with a multi-entry relationships list is parsed into all fields") {
        val entry = GovtEntryParser.parse(govtItemBinary())
        entry shouldBe GovtEntry(
            defaultType = 0,
            transitionType = 0,
            requiresMaintenance = 1,
            toggle1 = 0,
            tilePenalty = 0,
            tradeBonus = 0,
            name = "Despotism",
            civilopediaEntry = "",
            maleRulerTitle1 = "",
            femaleRulerTitle1 = "",
            maleRulerTitle2 = "",
            femaleRulerTitle2 = "",
            maleRulerTitle3 = "",
            femaleRulerTitle3 = "",
            maleRulerTitle4 = "",
            femaleRulerTitle4 = "",
            corruption = 0,
            immuneTo = 0,
            diplomatsAre = 0,
            spiesAre = 0,
            relationships = listOf(GovtRelationship(1, 20, 30), GovtRelationship(0, 5, 10)),
            hurrying = 0,
            assimilationChance = 0,
            draftLimit = 0,
            militaryPoliceLimit = 0,
            rulerTitlePairsUsed = 0,
            prerequisiteTechnology = 0,
            scienceRateCap = 0,
            workerRate = 0,
            toggle2 = -1,
            toggle3 = 0,
            unknown = ByteString.of(0, 0, 0, 0),
            freeUnits = 0,
            freeUnitsPerTown = 0,
            freeUnitsPerCity = 0,
            freeUnitsPerMetropolis = 0,
            unitCost = 0,
            warWeariness = 0,
            xenophobic = 0,
            forceResettle = 0,
        )
    }

    test("an empty relationships list (zero governments) is parsed correctly, resuming the fixed suffix") {
        val entry = GovtEntryParser.parse(govtItemBinary(relationships = emptyList()))
        entry.relationships shouldBe emptyList()
        entry.freeUnits shouldBe 0
    }

    test("unknown trailing field is preserved raw, not validated") {
        val entry = GovtEntryParser.parse(govtItemBinary(unknown = byteArrayOf(9, 9, 9, 9)))
        entry.unknown shouldBe ByteString.of(9, 9, 9, 9)
    }

    test("vanilla/PTW-length item (536 bytes, xenophobic/forceResettle absent) defaults them to zero") {
        val entry = GovtEntryParser.parse(govtItemBinary(includeTrailingFields = false))
        entry.xenophobic shouldBe 0
        entry.forceResettle shouldBe 0
    }
})
