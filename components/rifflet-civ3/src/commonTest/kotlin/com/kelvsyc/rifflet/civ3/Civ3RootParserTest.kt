package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletParseException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.decodeHex
import com.kelvsyc.rifflet.civ3.ClnyEntry
import com.kelvsyc.rifflet.civ3.ClnySection
import com.kelvsyc.rifflet.civ3.ContEntry
import com.kelvsyc.rifflet.civ3.ContSection
import com.kelvsyc.rifflet.civ3.CtznEntry
import com.kelvsyc.rifflet.civ3.CtznSection
import com.kelvsyc.rifflet.civ3.CultEntry
import com.kelvsyc.rifflet.civ3.CultSection
import com.kelvsyc.rifflet.civ3.DiffEntry
import com.kelvsyc.rifflet.civ3.DiffSection
import com.kelvsyc.rifflet.civ3.ErasEntry
import com.kelvsyc.rifflet.civ3.ErasSection
import com.kelvsyc.rifflet.civ3.EspnEntry
import com.kelvsyc.rifflet.civ3.EspnSection
import com.kelvsyc.rifflet.civ3.ExprEntry
import com.kelvsyc.rifflet.civ3.ExprSection
import com.kelvsyc.rifflet.civ3.FlavEntry
import com.kelvsyc.rifflet.civ3.FlavSection
import com.kelvsyc.rifflet.civ3.GoodEntry
import com.kelvsyc.rifflet.civ3.GoodSection
import com.kelvsyc.rifflet.civ3.SlocEntry
import com.kelvsyc.rifflet.civ3.SlocSection
import com.kelvsyc.rifflet.civ3.TfrmEntry
import com.kelvsyc.rifflet.civ3.TfrmSection
import com.kelvsyc.rifflet.civ3.WchrEntry
import com.kelvsyc.rifflet.civ3.WchrSection
import com.kelvsyc.rifflet.civ3.WsizEntry
import com.kelvsyc.rifflet.civ3.WsizSection
import com.kelvsyc.rifflet.civ3.GovtEntry
import com.kelvsyc.rifflet.civ3.GovtRelationship
import com.kelvsyc.rifflet.civ3.GovtSection
import com.kelvsyc.rifflet.civ3.RaceEntry
import com.kelvsyc.rifflet.civ3.RaceEraFilenames
import com.kelvsyc.rifflet.civ3.RaceSection

/** Builds a valid 732-byte VER# section: marker, header count/length, version, description, title. */
private fun verSectionBytes(major: Int = 12, minor: Int = 7): Buffer = Buffer().apply {
    writeString("VER#", Charsets.US_ASCII)
    writeIntLe(1)
    writeIntLe(720)
    writeIntLe(0)
    writeIntLe(0)
    writeIntLe(major)
    writeIntLe(minor)
    write(ByteArray(640))
    write(ByteArray(64))
}

/** Builds a raw section: marker, little-endian item count, then that many length-prefixed items. */
private fun rawSectionBytes(marker: String, items: List<ByteArray>): Buffer = Buffer().apply {
    writeString(marker, Charsets.US_ASCII)
    writeIntLe(items.size)
    items.forEach {
        writeIntLe(it.size)
        write(it)
    }
}

/** Builds a well-formed 80-byte WSIZ item body (no length prefix; the caller adds it via [oneItemSectionBytes]). */
private fun wsizItemBody(): Buffer = Buffer().apply {
    writeIntLe(12) // optimalNumberOfCities
    writeIntLe(4) // techRate
    write(ByteArray(24)) // reserved
    writeString("Standard", Charsets.US_ASCII)
    write(ByteArray(32 - 8)) // pad "Standard" (8 bytes) to 32
    writeIntLe(60) // height
    writeIntLe(6) // distanceBetweenCivs
    writeIntLe(7) // numberOfCivs
    writeIntLe(80) // width
}

/** Builds a well-formed 120-byte DIFF item body. */
private fun diffItemBody(): Buffer = Buffer().apply {
    writeString("Chieftain", Charsets.US_ASCII)
    write(ByteArray(64 - 9)) // pad "Chieftain" (9 bytes) to 64
    repeat(14) { writeIntLe(it) }
}

/** Builds a well-formed 264-byte ERAS item body. */
private fun erasItemBody(): Buffer = Buffer().apply {
    writeString("Ancient", Charsets.US_ASCII)
    write(ByteArray(64 - 7)) // pad "Ancient" (7 bytes) to 64
    write(ByteArray(32)) // civilopediaEntry: empty
    repeat(5) { write(ByteArray(32)) } // researcher1..5: empty
    writeIntLe(0) // numberOfUsedResearcherNames
    write(ByteArray(4)) // unknown
}

/** Wraps a single item body into a full section: marker, count=1, length prefix, item body. */
private fun oneItemSectionBytes(marker: String, itemBody: Buffer): Buffer = Buffer().apply {
    writeString(marker, Charsets.US_ASCII)
    writeIntLe(1)
    writeIntLe(itemBody.size.toInt())
    writeAll(itemBody)
}

/** Builds a well-formed GOVT item body with one relationship entry. */
private fun govtItemBody(): Buffer = Buffer().apply {
    writeIntLe(0) // defaultType
    writeIntLe(0) // transitionType
    writeIntLe(1) // requiresMaintenance
    writeIntLe(0) // toggle1
    writeIntLe(0) // tilePenalty
    writeIntLe(0) // tradeBonus
    writeString("Despotism", Charsets.US_ASCII)
    write(ByteArray(64 - 9)) // pad "Despotism" (9 bytes) to 64
    write(ByteArray(32)) // civilopediaEntry
    repeat(8) { write(ByteArray(32)) } // 4 male/female ruler title pairs
    writeIntLe(0) // corruption
    writeIntLe(0) // immuneTo
    writeIntLe(0) // diplomatsAre
    writeIntLe(0) // spiesAre
    writeIntLe(1) // numberOfGovernments
    writeIntLe(1) // canBribe
    writeIntLe(20) // briberyModifier
    writeIntLe(30) // resistanceModifier
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
    write(ByteArray(4)) // unknown
    writeIntLe(0) // freeUnits
    writeIntLe(0) // freeUnitsPerTown
    writeIntLe(0) // freeUnitsPerCity
    writeIntLe(0) // freeUnitsPerMetropolis
    writeIntLe(0) // unitCost
    writeIntLe(0) // warWeariness
    writeIntLe(0) // xenophobic
    writeIntLe(0) // forceResettle
}

/** Builds a well-formed RACE item body with one city, one great leader, one era, one scientific leader. */
private fun raceItemBody(): Buffer = Buffer().apply {
    writeIntLe(1) // numberOfCities
    writeString("Roma", Charsets.US_ASCII)
    write(ByteArray(24 - 4)) // pad "Roma" (4 bytes) to 24
    writeIntLe(1) // numberOfGreatLeaders
    writeString("Caesar", Charsets.US_ASCII)
    write(ByteArray(32 - 6)) // pad "Caesar" (6 bytes) to 32
    writeString("Caesar Augustus", Charsets.US_ASCII)
    write(ByteArray(32 - 15)) // pad "Caesar Augustus" (15 bytes) to 32, leaderName
    writeString("Emperor", Charsets.US_ASCII)
    write(ByteArray(24 - 7)) // pad "Emperor" (7 bytes) to 24, leaderTitle
    write(ByteArray(32)) // civilopediaEntry
    writeString("Roman", Charsets.US_ASCII)
    write(ByteArray(40 - 5)) // pad "Roman" (5 bytes) to 40, adjective
    writeString("Rome", Charsets.US_ASCII)
    write(ByteArray(40 - 4)) // pad "Rome" (4 bytes) to 40, name
    writeString("Romans", Charsets.US_ASCII)
    write(ByteArray(40 - 6)) // pad "Romans" (6 bytes) to 40, noun
    // one era entry, matching the single ERAS section entry parsed before this RACE section
    writeString("anc_fwd", Charsets.US_ASCII)
    write(ByteArray(260 - 7))
    writeString("anc_rev", Charsets.US_ASCII)
    write(ByteArray(260 - 7))
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
    writeIntLe(0) // unitTypeForKing
    writeIntLe(0) // flavors
    write(ByteArray(4)) // unknown
    writeIntLe(0) // diplomacyTextIndex
    writeIntLe(1) // numberOfScientificLeaders
    writeString("Archimedes", Charsets.US_ASCII)
    write(ByteArray(32 - 10)) // pad "Archimedes" (10 bytes) to 32
}

/** Builds a well-formed 40-byte EXPR item body. */
private fun exprItemBody(): Buffer = Buffer().apply {
    writeString("Veteran", Charsets.US_ASCII)
    write(ByteArray(32 - 7)) // pad "Veteran" (7 bytes) to 32
    writeIntLe(10) // baseHitPoints
    writeIntLe(20) // retreatBonus
}

/** Builds a well-formed 88-byte CULT item body. */
private fun cultItemBody(): Buffer = Buffer().apply {
    writeString("Legendary", Charsets.US_ASCII)
    write(ByteArray(64 - 9)) // pad "Legendary" (9 bytes) to 64
    writeIntLe(10) // chanceOfSuccessfulPropaganda
    writeIntLe(300) // cultureRatioPercentage
    writeIntLe(1) // cultureRatioDenominator
    writeIntLe(3) // cultureRatioNumerator
    writeIntLe(50) // initialResistanceChance
    writeIntLe(25) // continuedResistanceChance
}

/** Builds a well-formed 124-byte CTZN item body. */
private fun ctznItemBody(): Buffer = Buffer().apply {
    writeIntLe(1) // defaultCitizen
    writeString("Entertainer", Charsets.US_ASCII)
    write(ByteArray(32 - 11)) // pad "Entertainer" (11 bytes) to 32
    write(ByteArray(32)) // civilopediaEntry
    writeString("Entertainers", Charsets.US_ASCII)
    write(ByteArray(32 - 12)) // pad "Entertainers" (12 bytes) to 32
    writeIntLe(-1) // prerequisite
    writeIntLe(3) // luxuries
    writeIntLe(0) // research
    writeIntLe(0) // taxes
    writeIntLe(0) // corruption
    writeIntLe(0) // construction
}

/** Builds a well-formed 88-byte GOOD item body. */
private fun goodItemBody(): Buffer = Buffer().apply {
    writeString("Wine", Charsets.US_ASCII)
    write(ByteArray(24 - 4)) // pad "Wine" (4 bytes) to 24
    write(ByteArray(32)) // civilopediaEntry
    writeIntLe(1) // type
    writeIntLe(50) // appearanceRatio
    writeIntLe(0) // disappearanceProbability
    writeIntLe(12) // icon
    writeIntLe(-1) // prerequisite
    writeIntLe(0) // foodBonus
    writeIntLe(0) // shieldsBonus
    writeIntLe(3) // commerceBonus
}

/** Builds a well-formed 232-byte ESPN item body. */
private fun espnItemBody(): Buffer = Buffer().apply {
    writeString("Steal Technology", Charsets.US_ASCII)
    write(ByteArray(128 - 16)) // pad "Steal Technology" (16 bytes) to 128
    writeString("Steal Tech", Charsets.US_ASCII)
    write(ByteArray(64 - 10)) // pad "Steal Tech" (10 bytes) to 64
    write(ByteArray(32)) // civilopediaEntry
    writeIntLe(0b10) // missionFlags: spy-only
    writeIntLe(100) // baseCost
}

/** Builds a well-formed 16-byte SLOC item body. */
private fun slocItemBody(): Buffer = Buffer().apply {
    writeIntLe(2) // ownerType: Civ
    writeIntLe(0) // owner: RACE index 0
    writeIntLe(10) // x
    writeIntLe(20) // y
}

/** Builds a well-formed 8-byte CONT item body. */
private fun contItemBody(): Buffer = Buffer().apply {
    writeIntLe(1) // type: Land
    writeIntLe(42) // numberOfTiles
}

/** Builds a well-formed FLAV item body with 2 flavor relationships (no length prefix — see Global Constraints). */
private fun flavItemBody(): Buffer = Buffer().apply {
    write(ByteArray(4)) // unknown
    writeString("Military", Charsets.US_ASCII)
    write(ByteArray(256 - 8)) // pad "Military" (8 bytes) to 256
    writeIntLe(2) // numberOfFlavors
    writeIntLe(5) // flavorRelationships[0]
    writeIntLe(-3) // flavorRelationships[1]
}

/** Builds a well-formed 52-byte WCHR item body. */
private fun wchrItemBody(): Buffer = Buffer().apply {
    writeIntLe(1) // selectedClimate
    writeIntLe(1) // actualClimate
    writeIntLe(2) // selectedBarbarianActivity
    writeIntLe(2) // actualBarbarianActivity
    writeIntLe(1) // selectedLandform
    writeIntLe(1) // actualLandform
    writeIntLe(0) // selectedOceanCoverage
    writeIntLe(0) // actualOceanCoverage
    writeIntLe(1) // selectedTemperature
    writeIntLe(1) // actualTemperature
    writeIntLe(1) // selectedAge
    writeIntLe(1) // actualAge
    writeIntLe(3) // worldSize
}

/** Builds a well-formed 20-byte CLNY item body. */
private fun clnyItemBody(): Buffer = Buffer().apply {
    writeIntLe(2) // ownerType: Civ
    writeIntLe(0) // owner: RACE index 0
    writeIntLe(5) // x
    writeIntLe(15) // y
    writeIntLe(3) // improvementType
}

/** Builds a well-formed 112-byte TFRM item body. */
private fun tfrmItemBody(): Buffer = Buffer().apply {
    writeString("Build Road", Charsets.US_ASCII)
    write(ByteArray(32 - 10)) // pad "Build Road" (10 bytes) to 32
    write(ByteArray(32)) // civilopediaEntry
    writeIntLe(2) // turnsToComplete
    writeIntLe(-1) // required
    writeIntLe(-1) // requiredResource1
    writeIntLe(-1) // requiredResource2
    write(ByteArray(32)) // order
}

/** Wraps a single FLAV item body into a full section: marker, count=1, item body — no length
 * prefix, unlike [oneItemSectionBytes] (see Global Constraints). */
private fun oneFlavItemSectionBytes(itemBody: Buffer): Buffer = Buffer().apply {
    writeString("FLAV", Charsets.US_ASCII)
    writeIntLe(1)
    writeAll(itemBody)
}

class Civ3RootParserTest : FunSpec({

    test("uncompressed BIC file with no trailing sections is parsed") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
        }
        val file = Civ3RootParser.parse(source)
        file.header.major shouldBe 12
        file.header.minor shouldBe 7
        file.sections shouldBe emptyList()
    }

    test("uncompressed BICX file with an unmodeled trailing section is parsed as Civ3RawSection") {
        val source = Buffer().apply {
            writeString("BICX", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(rawSectionBytes("TECH", listOf(byteArrayOf(1, 2, 3))))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(Civ3RawSection(ChunkId("TECH"), 1, listOf(ByteString.of(1, 2, 3))))
    }

    test("bad leading magic that also fails to decompress into a valid Civ3 file throws") {
        // "00048224258f807f" is the reference PKWare Implode test vector from blast.c, which
        // decompresses to the ASCII text "AIAIAIAIAIAIA" -- valid Implode data, but not a Civ3
        // file. This exercises the real (non-mocked) decompression routing path.
        val source = Buffer().write("00048224258f807f".decodeHex())
        shouldThrow<RiffletParseException> { Civ3RootParser.parse(source) }
    }

    test("truncated VER# section throws RiffletParseException") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            write(ByteArray(10))
        }
        shouldThrow<RiffletParseException> { Civ3RootParser.parse(source) }
    }

    test("WSIZ section produces a typed WsizSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("WSIZ", wsizItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            WsizSection(
                listOf(WsizEntry(12, 4, ByteString.of(*ByteArray(24)), "Standard", 60, 6, 7, 80)),
            ),
        )
    }

    test("DIFF section produces a typed DiffSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("DIFF", diffItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            DiffSection(listOf(DiffEntry("Chieftain", 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13))),
        )
    }

    test("ERAS section produces a typed ErasSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("ERAS", erasItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            ErasSection(
                listOf(ErasEntry("Ancient", "", "", "", "", "", "", 0, ByteString.of(*ByteArray(4)))),
            ),
        )
    }

    test("GOVT section produces a typed GovtSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("GOVT", govtItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            GovtSection(
                listOf(
                    GovtEntry(
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
                        relationships = listOf(GovtRelationship(1, 20, 30)),
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
                    ),
                ),
            ),
        )
    }

    test("RACE section after an ERAS section produces a typed RaceSection sized from ERAS's entry count") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("ERAS", erasItemBody()))
            writeAll(oneItemSectionBytes("RACE", raceItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        val raceSection = file.sections.filterIsInstance<RaceSection>().single()
        raceSection.entries shouldBe listOf(
            RaceEntry(
                cityNames = listOf("Roma"),
                greatLeaderNames = listOf("Caesar"),
                leaderName = "Caesar Augustus",
                leaderTitle = "Emperor",
                civilopediaEntry = "",
                adjective = "Roman",
                name = "Rome",
                noun = "Romans",
                eras = listOf(RaceEraFilenames("anc_fwd", "anc_rev")),
                cultureGroup = 0,
                leaderGender = 0,
                civilizationGender = 0,
                aggressionLevel = 0,
                uniqueCivilizationCounter = 0,
                shunnedGovernment = 0,
                favoriteGovernment = 0,
                defaultColor = 0,
                uniqueColor = 0,
                freeTech1 = 0,
                freeTech2 = 0,
                freeTech3 = 0,
                freeTech4 = 0,
                bonuses = 0,
                governorSettings = 0,
                buildNever = 0,
                buildOften = 0,
                plurality = 0,
                unitTypeForKing = 0,
                flavors = 0,
                unknown = ByteString.of(0, 0, 0, 0),
                diplomacyTextIndex = 0,
                scientificLeaderNames = listOf("Archimedes"),
            ),
        )
    }

    test("RACE section with no preceding ERAS section throws RiffletParseException") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("RACE", raceItemBody()))
        }
        shouldThrow<RiffletParseException> { Civ3RootParser.parse(source) }
    }

    test("EXPR section produces a typed ExprSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("EXPR", exprItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(ExprSection(listOf(ExprEntry("Veteran", 10, 20))))
    }

    test("CULT section produces a typed CultSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("CULT", cultItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            CultSection(listOf(CultEntry("Legendary", 10, 300, 1, 3, 50, 25))),
        )
    }

    test("CTZN section produces a typed CtznSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("CTZN", ctznItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            CtznSection(listOf(CtznEntry(1, "Entertainer", "", "Entertainers", -1, 3, 0, 0, 0, 0))),
        )
    }

    test("GOOD section produces a typed GoodSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("GOOD", goodItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            GoodSection(listOf(GoodEntry("Wine", "", 1, 50, 0, 12, -1, 0, 0, 3))),
        )
    }

    test("ESPN section produces a typed EspnSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("ESPN", espnItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            EspnSection(listOf(EspnEntry("Steal Technology", "Steal Tech", "", 0b10, 100))),
        )
    }

    test("SLOC section produces a typed SlocSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("SLOC", slocItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            SlocSection(listOf(SlocEntry(2, 0, 10, 20))),
        )
    }

    test("CONT section produces a typed ContSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("CONT", contItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            ContSection(listOf(ContEntry(1, 42))),
        )
    }

    test("FLAV section produces a typed FlavSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneFlavItemSectionBytes(flavItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            FlavSection(listOf(FlavEntry(ByteString.of(0, 0, 0, 0), "Military", listOf(5, -3)))),
        )
    }

    test("WCHR section produces a typed WchrSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("WCHR", wchrItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            WchrSection(listOf(WchrEntry(1, 1, 2, 2, 1, 1, 0, 0, 1, 1, 1, 1, 3))),
        )
    }

    test("CLNY section produces a typed ClnySection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("CLNY", clnyItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            ClnySection(listOf(ClnyEntry(2, 0, 5, 15, 3))),
        )
    }

    test("TFRM section produces a typed TfrmSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("TFRM", tfrmItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            TfrmSection(listOf(TfrmEntry("Build Road", "", 2, -1, -1, -1, ""))),
        )
    }
})
