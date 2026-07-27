package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.RaceEntry
import com.kelvsyc.rifflet.civ3.RaceEraFilenames
import com.kelvsyc.rifflet.civ3.RaceGovernor
import com.kelvsyc.rifflet.civ3.RaceLeader
import com.kelvsyc.rifflet.civ3.RacePersonality
import com.kelvsyc.rifflet.core.RiffletParseException
import io.kotest.assertions.throwables.shouldThrow
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

/** Builds a well-formed RACE item body (length prefix excluded, as with prior sections). */
private fun raceItemBinary(
    cityNames: List<String> = listOf("Roma", "Neapolis"),
    greatLeaderNames: List<String> = listOf("Caesar"),
    eraFilenames: List<Pair<String, String>> = listOf("anc_fwd" to "anc_rev", "mid_fwd" to "mid_rev"),
    scientificLeaderNames: List<String> = listOf("Archimedes"),
    unknown: ByteArray = ByteArray(4),
    includeUnitTypeForKing: Boolean = true,
    includeTrailingFields: Boolean = true,
): Buffer = Buffer().apply {
    writeIntLe(cityNames.size)
    cityNames.forEach { writePaddedField(it, 24) }
    writeIntLe(greatLeaderNames.size)
    greatLeaderNames.forEach { writePaddedField(it, 32) }
    writePaddedField("Caesar Augustus", 32) // leaderName
    writePaddedField("Emperor", 24) // leaderTitle
    writePaddedField("", 32) // civilopediaEntry
    writePaddedField("Roman", 40) // adjective
    writePaddedField("Rome", 40) // name
    writePaddedField("Romans", 40) // noun
    eraFilenames.forEach { (forward, reverse) ->
        writePaddedField(forward, 260)
        writePaddedField(reverse, 260)
    }
    writeIntLe(0) // cultureGroup
    writeIntLe(0) // leaderGender
    writeIntLe(0) // civilizationGender
    writeIntLe(0) // aggressionLevel
    writeIntLe(0) // uniqueCivilizationCounter
    writeIntLe(0) // shunnedGovernment
    writeIntLe(0) // favoriteGovernment
    writeIntLe(0) // defaultColor
    writeIntLe(0) // uniqueColor
    writeIntLe(0) // freeTech1
    writeIntLe(0) // freeTech2
    writeIntLe(0) // freeTech3
    writeIntLe(0) // freeTech4
    writeIntLe(0) // bonuses
    writeIntLe(0) // governorSettings
    writeIntLe(0) // buildNever
    writeIntLe(0) // buildOften
    writeIntLe(0) // plurality
    if (includeUnitTypeForKing) {
        writeIntLe(0) // unitTypeForKing
        if (includeTrailingFields) {
            writeIntLe(0) // flavors
            write(unknown)
            writeIntLe(0) // diplomacyTextIndex
            writeIntLe(scientificLeaderNames.size)
            scientificLeaderNames.forEach { writePaddedField(it, 32) }
        }
    }
}

class RaceEntryParserTest : FunSpec({

    test("well-formed item with non-trivial dynamic lists is parsed into all fields") {
        val item = raceItemBinary()
        val entry = RaceEntryParser.parse(item, erasCount = 2)
        entry shouldBe RaceEntry(
            cityNames = listOf("Roma", "Neapolis"),
            greatLeaderNames = listOf("Caesar"),
            leader = RaceLeader(name = "Caesar Augustus", title = "Emperor", gender = 0),
            civilopediaEntry = "",
            adjective = "Roman",
            name = "Rome",
            noun = "Romans",
            eras = listOf(RaceEraFilenames("anc_fwd", "anc_rev"), RaceEraFilenames("mid_fwd", "mid_rev")),
            cultureGroup = 0,
            civilizationGender = 0,
            personality = RacePersonality(favoriteGovernment = 0, shunnedGovernment = 0, aggressionLevel = 0),
            uniqueCivilizationCounter = 0,
            defaultColor = 0,
            uniqueColor = 0,
            freeTechs = listOf(0, 0, 0, 0),
            bonuses = 0,
            governor = RaceGovernor(settings = 0, buildNever = 0, buildOften = 0),
            plurality = 0,
            unitTypeForKing = 0,
            flavors = 0,
            unknown = ByteString.of(0, 0, 0, 0),
            diplomacyTextIndex = 0,
            scientificLeaderNames = listOf("Archimedes"),
        )
        item.exhausted() shouldBe true
    }

    test("all four dynamic lists empty (zero counts) is parsed correctly, source fully exhausted") {
        val item = raceItemBinary(
            cityNames = emptyList(),
            greatLeaderNames = emptyList(),
            eraFilenames = emptyList(),
            scientificLeaderNames = emptyList(),
        )
        val entry = RaceEntryParser.parse(item, erasCount = 0)
        entry.cityNames shouldBe emptyList()
        entry.greatLeaderNames shouldBe emptyList()
        entry.eras shouldBe emptyList()
        entry.scientificLeaderNames shouldBe emptyList()
        item.exhausted() shouldBe true
    }

    test("unknown field is preserved raw, not validated") {
        val entry = RaceEntryParser.parse(raceItemBinary(unknown = byteArrayOf(9, 9, 9, 9)), erasCount = 2)
        entry.unknown shouldBe ByteString.of(9, 9, 9, 9)
    }

    test("item missing unitTypeForKing onward defaults them to zero/empty (vanilla shape)") {
        val entry = RaceEntryParser.parse(
            raceItemBinary(includeUnitTypeForKing = false),
            erasCount = 2,
        )
        entry.unitTypeForKing shouldBe 0
        entry.flavors shouldBe 0
        entry.unknown shouldBe ByteString.of(0, 0, 0, 0)
        entry.diplomacyTextIndex shouldBe 0
        entry.scientificLeaderNames shouldBe emptyList()
    }

    test("item with unitTypeForKing but missing flavors onward defaults the rest (PTW shape)") {
        val entry = RaceEntryParser.parse(
            raceItemBinary(includeTrailingFields = false),
            erasCount = 2,
        )
        entry.unitTypeForKing shouldBe 0
        entry.flavors shouldBe 0
        entry.unknown shouldBe ByteString.of(0, 0, 0, 0)
        entry.diplomacyTextIndex shouldBe 0
        entry.scientificLeaderNames shouldBe emptyList()
    }

    test("an implausibly large numberOfCities throws RiffletParseException before attempting to allocate") {
        val buffer = Buffer().apply {
            writeIntLe(Int.MAX_VALUE)
        }
        shouldThrow<RiffletParseException> { RaceEntryParser.parse(buffer, erasCount = 0) }
    }

    test("an implausibly large numberOfGreatLeaders throws RiffletParseException before attempting to allocate") {
        val buffer = Buffer().apply {
            writeIntLe(0) // numberOfCities
            writeIntLe(Int.MAX_VALUE) // numberOfGreatLeaders
        }
        shouldThrow<RiffletParseException> { RaceEntryParser.parse(buffer, erasCount = 0) }
    }

    test("an implausibly large erasCount throws RiffletParseException before attempting to allocate") {
        val buffer = Buffer().apply {
            writeIntLe(0) // numberOfCities
            writeIntLe(0) // numberOfGreatLeaders
            write(ByteArray(32)) // leaderName
            write(ByteArray(24)) // leaderTitle
            write(ByteArray(32)) // civilopediaEntry
            write(ByteArray(40)) // adjective
            write(ByteArray(40)) // name
            write(ByteArray(40)) // noun
        }
        shouldThrow<RiffletParseException> { RaceEntryParser.parse(buffer, erasCount = Int.MAX_VALUE) }
    }

    test("an implausibly large numberOfScientificLeaders throws RiffletParseException before attempting to allocate") {
        val buffer = Buffer().apply {
            writeIntLe(0) // numberOfCities
            writeIntLe(0) // numberOfGreatLeaders
            write(ByteArray(32)) // leaderName
            write(ByteArray(24)) // leaderTitle
            write(ByteArray(32)) // civilopediaEntry
            write(ByteArray(40)) // adjective
            write(ByteArray(40)) // name
            write(ByteArray(40)) // noun
            // erasCount = 0, so no era entries to write
            // cultureGroup..flavors: 20 ints (80B) + unknown (4B) + diplomacyTextIndex (4B) = 88B
            write(ByteArray(88))
            writeIntLe(Int.MAX_VALUE) // numberOfScientificLeaders
        }
        shouldThrow<RiffletParseException> { RaceEntryParser.parse(buffer, erasCount = 0) }
    }
})
