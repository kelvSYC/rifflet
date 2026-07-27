package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletParseException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.decodeHex
import com.kelvsyc.rifflet.civ3.BldgEntry
import com.kelvsyc.rifflet.civ3.BldgSection
import com.kelvsyc.rifflet.civ3.CityEntry
import com.kelvsyc.rifflet.civ3.CitySection
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
import com.kelvsyc.rifflet.civ3.FlavGroupEntry
import com.kelvsyc.rifflet.civ3.FlavSection
import com.kelvsyc.rifflet.civ3.FlavorEntry
import com.kelvsyc.rifflet.civ3.GameEntry
import com.kelvsyc.rifflet.civ3.GameSection
import com.kelvsyc.rifflet.civ3.GoodEntry
import com.kelvsyc.rifflet.civ3.GoodSection
import com.kelvsyc.rifflet.civ3.LeadEntry
import com.kelvsyc.rifflet.civ3.LeadSection
import com.kelvsyc.rifflet.civ3.LeadStartUnit
import com.kelvsyc.rifflet.civ3.PrtoEntry
import com.kelvsyc.rifflet.civ3.PrtoSection
import com.kelvsyc.rifflet.civ3.RuleEntry
import com.kelvsyc.rifflet.civ3.RuleSection
import com.kelvsyc.rifflet.civ3.SlocEntry
import com.kelvsyc.rifflet.civ3.SlocSection
import com.kelvsyc.rifflet.civ3.TechEntry
import com.kelvsyc.rifflet.civ3.TechSection
import com.kelvsyc.rifflet.civ3.TerrEntry
import com.kelvsyc.rifflet.civ3.TerrSection
import com.kelvsyc.rifflet.civ3.TfrmEntry
import com.kelvsyc.rifflet.civ3.TfrmSection
import com.kelvsyc.rifflet.civ3.TileEntry
import com.kelvsyc.rifflet.civ3.TileSection
import com.kelvsyc.rifflet.civ3.UnitEntry
import com.kelvsyc.rifflet.civ3.UnitSection
import com.kelvsyc.rifflet.civ3.WchrEntry
import com.kelvsyc.rifflet.civ3.WchrSection
import com.kelvsyc.rifflet.civ3.WmapEntry
import com.kelvsyc.rifflet.civ3.WmapSection
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
    writeIntLe(20) // propagandaModifier
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

/** Builds a well-formed FLAV group body with 2 flavors, each with 2 relations (no length
 * prefix — see Global Constraints). */
private fun flavItemBody(): Buffer = Buffer().apply {
    writeIntLe(2) // numberOfFlavors
    write(ByteArray(4)) // flavors[0].unknown
    writeString("Barbarian", Charsets.US_ASCII)
    write(ByteArray(256 - 9)) // pad "Barbarian" (9 bytes) to 256
    writeIntLe(2) // flavors[0].numberOfRelations
    writeIntLe(80) // flavors[0].relations[0]
    writeIntLe(20) // flavors[0].relations[1]
    write(ByteArray(4)) // flavors[1].unknown
    writeString("Eastern Civ", Charsets.US_ASCII)
    write(ByteArray(256 - 11)) // pad "Eastern Civ" (11 bytes) to 256
    writeIntLe(2) // flavors[1].numberOfRelations
    writeIntLe(10) // flavors[1].relations[0]
    writeIntLe(100) // flavors[1].relations[1]
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

/** Builds a well-formed WMAP item body with 2 resource IDs. */
private fun wmapItemBody(): Buffer = Buffer().apply {
    writeIntLe(2) // numberOfResources
    writeIntLe(3) // resourceIds[0]
    writeIntLe(7) // resourceIds[1]
    writeIntLe(4) // numberOfContinents
    writeIntLe(60) // height
    writeIntLe(6) // distanceBetweenCivs
    writeIntLe(7) // numberOfCivs
    write(ByteArray(8)) // unknown1
    writeIntLe(80) // width
    write(ByteArray(128)) // unknown2
    writeIntLe(12345) // mapSeed
    writeIntLe(0b101) // flags: x-wrapping + polar ice caps
}

/** Builds a well-formed 121-byte UNIT item body. */
private fun unitItemBody(): Buffer = Buffer().apply {
    writeString("Phalanx", Charsets.US_ASCII)
    write(ByteArray(32 - 7)) // pad "Phalanx" (7 bytes) to 32
    writeIntLe(2) // ownerType: Civ
    writeIntLe(0) // experienceLevel
    writeIntLe(0) // owner: RACE index 0
    writeIntLe(5) // unitType: PRTO index 5
    writeIntLe(0) // aiStrategy
    writeIntLe(10) // x
    writeIntLe(20) // y
    writeString("Legion", Charsets.US_ASCII)
    write(ByteArray(57 - 6)) // pad "Legion" (6 bytes) to 57
    writeIntLe(0) // useCivilizationKing
}

/** Wraps a single FLAV item body into a full section: marker, count=1, item body — no length
 * prefix, unlike [oneItemSectionBytes] (see Global Constraints). */
private fun oneFlavItemSectionBytes(itemBody: Buffer): Buffer = Buffer().apply {
    writeString("FLAV", Charsets.US_ASCII)
    writeIntLe(1)
    writeAll(itemBody)
}

/** Builds a well-formed CITY item body with 2 building IDs. */
private fun cityItemBody(): Buffer = Buffer().apply {
    writeByte(1) // hasWalls
    writeByte(0) // hasPalace
    writeString("Rome", Charsets.US_ASCII)
    write(ByteArray(24 - 4)) // pad "Rome" (4 bytes) to 24
    writeIntLe(2) // ownerType: Civ
    writeIntLe(2) // numberOfBuildings
    writeIntLe(3) // buildingIds[0]
    writeIntLe(7) // buildingIds[1]
    writeIntLe(150) // culture
    writeIntLe(0) // owner: RACE index 0
    writeIntLe(4) // size
    writeIntLe(10) // x
    writeIntLe(20) // y
    writeIntLe(2) // cityLevel
    writeIntLe(1) // borderLevel
    writeIntLe(1) // useAutoName
}

/** Builds a well-formed 112-byte TECH item body. */
private fun techItemBody(): Buffer = Buffer().apply {
    writeString("Bronze Working", Charsets.US_ASCII)
    write(ByteArray(32 - 14)) // pad "Bronze Working" (14 bytes) to 32
    write(ByteArray(32)) // civilopediaEntry
    writeIntLe(20) // cost
    writeIntLe(1) // era
    writeIntLe(5) // advanceIcon
    writeIntLe(10) // x
    writeIntLe(20) // y
    writeIntLe(-1) // prerequisite1
    writeIntLe(-1) // prerequisite2
    writeIntLe(-1) // prerequisite3
    writeIntLe(-1) // prerequisite4
    writeIntLe(0b10000001) // flags
    writeIntLe(0) // flavors
    write(ByteArray(4)) // unknown
}

/** Builds a well-formed LEAD item body with 2 start unit types and 2 starting technologies. */
private fun leadItemBody(): Buffer = Buffer().apply {
    writeIntLe(1) // customCivData
    writeIntLe(1) // humanPlayer
    writeString("Caesar", Charsets.US_ASCII)
    write(ByteArray(32 - 6)) // pad "Caesar" (6 bytes) to 32
    write(ByteArray(8)) // unknown
    writeIntLe(2) // numberOfStartUnitTypes
    writeIntLe(2) // startUnits[0].quantity
    writeIntLe(5) // startUnits[0].unitType
    writeIntLe(1) // startUnits[1].quantity
    writeIntLe(8) // startUnits[1].unitType
    writeIntLe(0) // genderOfLeaderName
    writeIntLe(2) // numberOfStartingTechnologies
    writeIntLe(3) // startingTechnologyIds[0]
    writeIntLe(7) // startingTechnologyIds[1]
    writeIntLe(2) // difficulty
    writeIntLe(0) // initialEra
    writeIntLe(50) // startCash
    writeIntLe(0) // government
    writeIntLe(0) // civ
    writeIntLe(1) // color
    writeIntLe(0) // skipFirstTurn
    write(ByteArray(4)) // unknown2
    writeByte(1) // startEmbassies
}

/** Builds a well-formed RULE item body with 2 spaceship parts, 2 culture levels, and
 * `upgradeCost` present. */
private fun ruleItemBody(): Buffer = Buffer().apply {
    writeString("Town", Charsets.US_ASCII)
    write(ByteArray(32 - 4)) // pad "Town" (4 bytes) to 32
    writeString("City", Charsets.US_ASCII)
    write(ByteArray(32 - 4)) // pad "City" (4 bytes) to 32
    writeString("Metropolis", Charsets.US_ASCII)
    write(ByteArray(32 - 10)) // pad "Metropolis" (10 bytes) to 32
    writeIntLe(2) // numberOfSpaceshipParts
    writeIntLe(4) // spaceshipPartQuantities[0]
    writeIntLe(2) // spaceshipPartQuantities[1]
    writeIntLe(10) // advancedBarbarianUnitType
    writeIntLe(9) // basicBarbarianUnitType
    writeIntLe(11) // barbarianSeaUnitType
    writeIntLe(2) // citiesNeededToSupportAnArmy
    writeIntLe(5) // chanceOfRioting
    writeIntLe(1) // turnPenaltyForEachDraftedCitizen
    writeIntLe(2) // shieldCostPerGold
    writeIntLe(50) // fortressDefensiveBonus
    writeIntLe(1) // citizensAffectedByEachHappyFace
    write(ByteArray(8)) // unknown
    writeIntLe(1) // forestValueInShields
    writeIntLe(2) // shieldValueInGold
    writeIntLe(1) // citizenValueInShields
    writeIntLe(2) // defaultDifficultyLevel
    writeIntLe(15) // battleCreatedUnit
    writeIntLe(16) // buildArmyUnit
    writeIntLe(50) // buildingDefensiveBonus
    writeIntLe(25) // citizenDefensiveBonus
    writeIntLe(0) // defaultMoneyResource
    writeIntLe(25) // chanceToInterceptEnemyAirMissions
    writeIntLe(25) // chanceToInterceptEnemyStealthMissions
    writeIntLe(50) // startingTreasury
    write(ByteArray(4)) // unknown2
    writeIntLe(2) // foodConsumptionPerCitizen
    writeIntLe(25) // riverDefensiveBonus
    writeIntLe(1) // turnPenaltyForEachHurrySacrifice
    writeIntLe(17) // scout
    writeIntLe(18) // slave
    writeIntLe(3) // movementAlongRoads
    writeIntLe(1) // startUnit1
    writeIntLe(2) // startUnit2
    writeIntLe(6) // minimumPopulationForWeLoveTheKing
    writeIntLe(25) // townDefenseBonus
    writeIntLe(50) // cityDefenseBonus
    writeIntLe(100) // metropolisDefenseBonus
    writeIntLe(8) // maximumLevel1CitySize
    writeIntLe(16) // maximumLevel2CitySize
    write(ByteArray(4)) // unknown3
    writeIntLe(25) // fortificationsDefensiveBonus
    writeIntLe(2) // numberOfCultureLevels
    writeString("Emerging", Charsets.US_ASCII)
    write(ByteArray(64 - 8)) // pad "Emerging" (8 bytes) to 64
    writeString("Legendary", Charsets.US_ASCII)
    write(ByteArray(64 - 9)) // pad "Legendary" (9 bytes) to 64
    writeIntLe(2) // borderExpansionMultiplier
    writeIntLe(3) // borderFactor
    writeIntLe(1000) // futureTechCost
    writeIntLe(16) // goldenAgeDuration
    writeIntLe(4) // maximumResearchTime
    writeIntLe(1) // minimumResearchTime
    writeIntLe(20) // flagUnitType
    writeIntLe(15) // upgradeCost
}

/** Builds a well-formed PRTO item body with 2 stealth targets. */
private fun prtoItemBody(): Buffer = Buffer().apply {
    writeIntLe(0) // zoneOfControl
    writeString("Warrior", Charsets.US_ASCII)
    write(ByteArray(32 - 7)) // pad "Warrior" (7 bytes) to 32
    writeString("Warrior", Charsets.US_ASCII)
    write(ByteArray(32 - 7)) // pad "Warrior" (7 bytes) to 32
    writeIntLe(0) // bombardStrength
    writeIntLe(0) // bombardRange
    writeIntLe(0) // capacity
    writeIntLe(10) // shieldCost
    writeIntLe(1) // defense
    writeIntLe(5) // iconIndex
    writeIntLe(1) // attack
    writeIntLe(0) // operationalRange
    writeIntLe(0) // populationCost
    writeIntLe(1) // rateOfFire
    writeIntLe(1) // movement
    writeIntLe(0) // required
    writeIntLe(3) // upgradeTo
    writeIntLe(-1) // requiredResource1
    writeIntLe(-1) // requiredResource2
    writeIntLe(-1) // requiredResource3
    write(ByteArray(8)) // flags1
    writeIntLe(-1) // availableTo
    write(ByteArray(8)) // flags2
    writeIntLe(0) // type
    writeIntLe(-1) // otherStrategy
    writeIntLe(0) // hpBonus
    write(ByteArray(20)) // flags3
    writeIntLe(0) // bombardEffects
    write(ByteArray(1)) // ignoreMovementCost, sized to match the 1-entry TERR section the PRTO test writes first
    writeIntLe(0) // requireSupport
    write(ByteArray(16)) // unknown
    writeIntLe(0) // enslaveResults
    write(ByteArray(4)) // unknown2
    writeIntLe(2) // numberOfStealthTargets
    writeIntLe(4) // stealthTargetUnitTypes[0]
    writeIntLe(9) // stealthTargetUnitTypes[1]
    write(ByteArray(8)) // unknown3
    writeByte(0) // createCraters
    writeIntLe(1.5f.toRawBits()) // workerStrength
    write(ByteArray(4)) // unknown4
    writeIntLe(0) // airDefense
}

/** Builds a well-formed BLDG item body. BLDG has no dynamic-length regions. */
private fun bldgItemBody(): Buffer = Buffer().apply {
    writeString("Provides defense", Charsets.US_ASCII)
    write(ByteArray(64 - 16)) // pad "Provides defense" (16 bytes) to 64
    writeString("City Walls", Charsets.US_ASCII)
    write(ByteArray(32 - 10)) // pad "City Walls" (10 bytes) to 32
    writeString("City Walls", Charsets.US_ASCII)
    write(ByteArray(32 - 10)) // pad "City Walls" (10 bytes) to 32
    writeIntLe(0) // doublesHappiness
    writeIntLe(0) // gainInEveryCity
    writeIntLe(0) // gainInEveryCityOnContinent
    writeIntLe(-1) // requiredBuilding
    writeIntLe(60) // cost
    writeIntLe(1) // culture
    writeIntLe(0) // bombardDefense
    writeIntLe(0) // navalBombardDefense
    writeIntLe(100) // defenseBonus
    writeIntLe(0) // navalDefenseBonus
    writeIntLe(0) // maintenanceCost
    writeIntLe(0) // contentFacesAllCities
    writeIntLe(0) // contentFaces
    writeIntLe(0) // unhappyFacesAllCities
    writeIntLe(0) // unhappyFaces
    writeIntLe(0) // numberOfRequiredBuildings
    writeIntLe(0) // airPower
    writeIntLe(0) // navalPower
    writeIntLe(0) // pollution
    writeIntLe(0) // production
    writeIntLe(-1) // requiredGovernment
    writeIntLe(0) // spaceshipPart
    writeIntLe(5) // requiredAdvance
    writeIntLe(-1) // renderedObsoleteBy
    writeIntLe(-1) // requiredResource1
    writeIntLe(-1) // requiredResource2
    write(ByteArray(16)) // flags
    writeIntLe(0) // numberOfArmiesRequired
    writeIntLe(0) // flavors
    write(ByteArray(4)) // unknown
    writeIntLe(-1) // unitProduced
    writeIntLe(0) // unitFrequency
}

/** Builds a well-formed TERR item body with a non-byte-aligned resource count (5 -> 1 byte). */
private fun terrItemBody(): Buffer = Buffer().apply {
    writeIntLe(5) // numberOfPossibleResources
    writeByte(0b00010101) // possibleResources
    writeString("Plains", Charsets.US_ASCII)
    write(ByteArray(32 - 6)) // pad "Plains" (6 bytes) to 32
    writeString("Plains", Charsets.US_ASCII)
    write(ByteArray(32 - 6)) // pad "Plains" (6 bytes) to 32
    writeIntLe(1) // irrigationBonus
    writeIntLe(0) // miningBonus
    writeIntLe(1) // roadBonus
    writeIntLe(0) // defenseBonus
    writeIntLe(1) // movementCost
    writeIntLe(1) // food
    writeIntLe(1) // shields
    writeIntLe(0) // commerce
    writeIntLe(-1) // workerJobAllowed
    writeIntLe(-1) // pollutionEffect
    writeByte(1) // allowCities
    writeByte(1) // allowColonies
    writeByte(0) // impassable
    writeByte(0) // impassableByWheeled
    writeByte(1) // allowAirfields
    writeByte(1) // allowForts
    writeByte(1) // allowOutposts
    writeByte(1) // allowRadarTowers
    write(ByteArray(4)) // unknown
    writeByte(0) // landmarkEnabled
    writeIntLe(0) // landmarkFood
    writeIntLe(0) // landmarkShields
    writeIntLe(0) // landmarkCommerce
    writeIntLe(0) // landmarkIrrigationBonus
    writeIntLe(0) // landmarkMiningBonus
    writeIntLe(0) // landmarkRoadBonus
    writeIntLe(0) // landmarkMovementBonus
    writeIntLe(0) // landmarkDefensiveBonus
    write(ByteArray(32)) // landmarkName
    write(ByteArray(32)) // landmarkCivilopediaEntry
    write(ByteArray(4)) // unknown2
    writeIntLe(0) // terrainFlags
    writeIntLe(0) // diseaseStrength
}

/** Builds a well-formed GAME item body with 2 playable civs, proving both civ-count-sized
 * lists (playableCivIds, civAllianceStatuses) are read from their independent positions. */
private fun gameItemBody(): Buffer = Buffer().apply {
    writeIntLe(1) // defaultGameRules
    writeIntLe(1) // defaultVictoryConditions
    writeIntLe(2) // numberOfPlayableCivs
    writeIntLe(3) // playableCivIds[0]
    writeIntLe(7) // playableCivIds[1]
    write(ByteArray(4)) // flags
    writeIntLe(0) // placeCaptureUnits
    writeIntLe(0) // autoPlaceKings
    writeIntLe(0) // autoPlaceVictoryLocations
    writeIntLe(0) // debugMode
    writeIntLe(0) // useTimeLimit
    writeIntLe(0) // baseTimeUnit
    writeIntLe(1) // startMonth
    writeIntLe(1) // startWeek
    writeIntLe(-4000) // startYear
    writeIntLe(0) // minuteTimeLimit
    writeIntLe(0) // turnTimeLimit
    for (i in 1..7) writeIntLe(i) // timescaleNumberOfTurns
    for (i in 1..7) writeIntLe(i * 10) // turnNumberOfTimeUnits
    writeString("Conquests", Charsets.US_ASCII)
    write(ByteArray(5200 - 9)) // pad "Conquests" (9 bytes) to 5200
    writeIntLe(0) // civAllianceStatuses[0]
    writeIntLe(2) // civAllianceStatuses[1]
    writeIntLe(0) // victoryPointLimit
    writeIntLe(0) // cityEliminationCount
    writeIntLe(0) // oneCityCultureWin
    writeIntLe(0) // allCitiesCultureWin
    writeIntLe(0) // dominationTerrain
    writeIntLe(0) // dominationPopulation
    writeIntLe(0) // wonderCost
    writeIntLe(0) // defeatingOpposingUnitCost
    writeIntLe(0) // advancementCost
    writeIntLe(0) // cityConquestPopulation
    writeIntLe(0) // victoryPointScoring
    writeIntLe(0) // capturingSpecialUnit
    write(ByteArray(5)) // unknown
    write(ByteArray(256)) // allianceNames[0]
    write(ByteArray(256)) // allianceNames[1]
    write(ByteArray(256)) // allianceNames[2]
    write(ByteArray(256)) // allianceNames[3]
    write(ByteArray(256)) // allianceNames[4]
    for (i in 1..25) writeIntLe(0) // allianceWars
    writeIntLe(0) // allianceVictoryType
    write(ByteArray(260)) // plagueName
    writeByte(0) // permitPlagues
    writeIntLe(0) // plagueEarliestStart
    writeIntLe(0) // plagueVariation
    writeIntLe(0) // plagueDuration
    writeIntLe(0) // plagueStrength
    writeIntLe(0) // plagueGracePeriod
    writeIntLe(0) // plagueMaxOccurrence
    write(ByteArray(264)) // unknown2
    writeIntLe(0) // respawnFlagUnits
    writeByte(0) // captureAnyFlag
    writeIntLe(0) // goldForCapture
    writeByte(1) // mapVisible
    writeByte(0) // retainCulture
    write(ByteArray(4)) // unknown3
    writeIntLe(0) // eruptionPeriod
    writeIntLe(0) // mpBasetime
    writeIntLe(0) // mpCityTime
    writeIntLe(0) // mpUnitTime
}

/** Builds a well-formed 45-byte (Conquests-tier) TILE item body. */
private fun tileItemBody(): Buffer = Buffer().apply {
    writeByte(0b00001111) // riverConnections
    writeByte(1) // border
    writeIntLe(3) // resource
    writeByte(42) // textureLocation
    writeByte(5) // textureFile
    write(byteArrayOf(0x11, 0x22)) // unknown
    writeByte(2) // overlayFlags
    writeByte(0x21) // terrain: base=1, overlay=2 packed nibbles
    writeByte(4) // bonusFlags
    writeByte(0) // riverCrossingFlags
    writeShortLe(7) // barbarianTribe
    writeShortLe(2) // city (first short in the item; see TileEntryParser's KDoc for the confirmed
    // colony/city byte-order swap)
    writeShortLe(9) // colony (second short in the item; see above)
    writeShortLe(1) // continent
    write(byteArrayOf(0x33)) // unknown2
    writeShortLe(-1) // victoryPointLocation
    writeIntLe(100) // ruin
    write(byteArrayOf(0x01, 0x02, 0x03, 0x04)) // c3cOverlays
    write(byteArrayOf(0x44)) // unknown3
    writeByte(0x21) // c3cTerrain
    write(byteArrayOf(0x55, 0x66)) // unknown4
    writeShortLe(3) // fogOfWar
    write(byteArrayOf(0x07, 0x08, 0x09, 0x0A)) // c3cBonuses
    write(byteArrayOf(0x77, 0x12)) // unknown5
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
            writeAll(rawSectionBytes("UNKN", listOf(byteArrayOf(1, 2, 3))))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(Civ3RawSection(ChunkId("UNKN"), 1, listOf(ByteString.of(1, 2, 3))))
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

    test("GOVT item with more governments than the old fixed ceiling parses fine (dynamic government count, not a version split)") {
        val relationshipCount = 20
        val item = Buffer().apply {
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
            writeIntLe(relationshipCount) // numberOfGovernments
            repeat(relationshipCount) {
                writeIntLe(1) // canBribe
                writeIntLe(20) // propagandaModifier
                writeIntLe(30) // resistanceModifier
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
            write(ByteArray(4)) // unknown
            writeIntLe(0) // freeUnits
            writeIntLe(0) // freeUnitsPerTown
            writeIntLe(0) // freeUnitsPerCity
            writeIntLe(0) // freeUnitsPerMetropolis
            writeIntLe(0) // unitCost
            writeIntLe(0) // warWeariness
            // xenophobic/forceResettle omitted: real PTW files never have them
        }
        val source = Buffer().apply {
            writeString("BICX", Charsets.US_ASCII)
            writeAll(verSectionBytes(major = 11, minor = 18))
            writeAll(oneItemSectionBytes("GOVT", item))
        }
        val file = Civ3RootParser.parse(source)
        val govtSection = file.sections.filterIsInstance<GovtSection>().single()
        govtSection.entries.single().relationships.size shouldBe relationshipCount
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
            FlavSection(
                listOf(
                    FlavGroupEntry(
                        listOf(
                            FlavorEntry(ByteString.of(*ByteArray(4)), "Barbarian", listOf(80, 20)),
                            FlavorEntry(ByteString.of(*ByteArray(4)), "Eastern Civ", listOf(10, 100)),
                        ),
                    ),
                ),
            ),
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

    test("WMAP section produces a typed WmapSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("WMAP", wmapItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            WmapSection(
                listOf(
                    WmapEntry(
                        resourceIds = listOf(3, 7),
                        numberOfContinents = 4,
                        height = 60,
                        distanceBetweenCivs = 6,
                        numberOfCivs = 7,
                        unknown1 = ByteString.of(*ByteArray(8)),
                        width = 80,
                        unknown2 = ByteString.of(*ByteArray(128)),
                        mapSeed = 12345,
                        flags = 0b101,
                    ),
                ),
            ),
        )
    }

    test("UNIT section produces a typed UnitSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("UNIT", unitItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            UnitSection(listOf(UnitEntry("Phalanx", 2, 0, 0, 5, 0, 10, 20, "Legion", 0))),
        )
    }

    test("CITY section produces a typed CitySection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("CITY", cityItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            CitySection(
                listOf(
                    CityEntry(
                        hasWalls = 1,
                        hasPalace = 0,
                        name = "Rome",
                        ownerType = 2,
                        buildingIds = listOf(3, 7),
                        culture = 150,
                        owner = 0,
                        size = 4,
                        x = 10,
                        y = 20,
                        cityLevel = 2,
                        borderLevel = 1,
                        useAutoName = 1,
                    ),
                ),
            ),
        )
    }

    test("TECH section produces a typed TechSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("TECH", techItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            TechSection(
                listOf(
                    TechEntry(
                        name = "Bronze Working",
                        civilopediaEntry = "",
                        cost = 20,
                        era = 1,
                        advanceIcon = 5,
                        x = 10,
                        y = 20,
                        prerequisite1 = -1,
                        prerequisite2 = -1,
                        prerequisite3 = -1,
                        prerequisite4 = -1,
                        flags = 0b10000001,
                        flavors = 0,
                        unknown = ByteString.of(0, 0, 0, 0),
                    ),
                ),
            ),
        )
    }

    test("LEAD section produces a typed LeadSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("LEAD", leadItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            LeadSection(
                listOf(
                    LeadEntry(
                        customCivData = 1,
                        humanPlayer = 1,
                        name = "Caesar",
                        unknown = ByteString.of(*ByteArray(8)),
                        startUnits = listOf(LeadStartUnit(2, 5), LeadStartUnit(1, 8)),
                        genderOfLeaderName = 0,
                        startingTechnologyIds = listOf(3, 7),
                        difficulty = 2,
                        initialEra = 0,
                        startCash = 50,
                        government = 0,
                        civ = 0,
                        color = 1,
                        skipFirstTurn = 0,
                        unknown2 = ByteString.of(*ByteArray(4)),
                        startEmbassies = 1,
                    ),
                ),
            ),
        )
    }

    test("RULE section produces a typed RuleSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("RULE", ruleItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            RuleSection(
                listOf(
                    RuleEntry(
                        citySizeLevel1Name = "Town",
                        citySizeLevel2Name = "City",
                        citySizeLevel3Name = "Metropolis",
                        spaceshipPartQuantities = listOf(4, 2),
                        advancedBarbarianUnitType = 10,
                        basicBarbarianUnitType = 9,
                        barbarianSeaUnitType = 11,
                        citiesNeededToSupportAnArmy = 2,
                        chanceOfRioting = 5,
                        turnPenaltyForEachDraftedCitizen = 1,
                        shieldCostPerGold = 2,
                        fortressDefensiveBonus = 50,
                        citizensAffectedByEachHappyFace = 1,
                        unknown = ByteString.of(*ByteArray(8)),
                        forestValueInShields = 1,
                        shieldValueInGold = 2,
                        citizenValueInShields = 1,
                        defaultDifficultyLevel = 2,
                        battleCreatedUnit = 15,
                        buildArmyUnit = 16,
                        buildingDefensiveBonus = 50,
                        citizenDefensiveBonus = 25,
                        defaultMoneyResource = 0,
                        chanceToInterceptEnemyAirMissions = 25,
                        chanceToInterceptEnemyStealthMissions = 25,
                        startingTreasury = 50,
                        unknown2 = ByteString.of(*ByteArray(4)),
                        foodConsumptionPerCitizen = 2,
                        riverDefensiveBonus = 25,
                        turnPenaltyForEachHurrySacrifice = 1,
                        scout = 17,
                        slave = 18,
                        movementAlongRoads = 3,
                        startUnit1 = 1,
                        startUnit2 = 2,
                        minimumPopulationForWeLoveTheKing = 6,
                        townDefenseBonus = 25,
                        cityDefenseBonus = 50,
                        metropolisDefenseBonus = 100,
                        maximumLevel1CitySize = 8,
                        maximumLevel2CitySize = 16,
                        unknown3 = ByteString.of(*ByteArray(4)),
                        fortificationsDefensiveBonus = 25,
                        cultureLevelNames = listOf("Emerging", "Legendary"),
                        borderExpansionMultiplier = 2,
                        borderFactor = 3,
                        futureTechCost = 1000,
                        goldenAgeDuration = 16,
                        maximumResearchTime = 4,
                        minimumResearchTime = 1,
                        flagUnitType = 20,
                        upgradeCost = 15,
                    ),
                ),
            ),
        )
    }

    test("PRTO section produces a typed PrtoSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("TERR", terrItemBody()))
            writeAll(oneItemSectionBytes("PRTO", prtoItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            TerrSection(
                listOf(
                    TerrEntry(
                        numberOfPossibleResources = 5,
                        possibleResources = ByteString.of(0b00010101.toByte()),
                        name = "Plains",
                        civilopediaEntry = "Plains",
                        irrigationBonus = 1,
                        miningBonus = 0,
                        roadBonus = 1,
                        defenseBonus = 0,
                        movementCost = 1,
                        food = 1,
                        shields = 1,
                        commerce = 0,
                        workerJobAllowed = -1,
                        pollutionEffect = -1,
                        allowCities = 1,
                        allowColonies = 1,
                        impassable = 0,
                        impassableByWheeled = 0,
                        allowAirfields = 1,
                        allowForts = 1,
                        allowOutposts = 1,
                        allowRadarTowers = 1,
                        unknown = ByteString.of(*ByteArray(4)),
                        landmarkEnabled = 0,
                        landmarkFood = 0,
                        landmarkShields = 0,
                        landmarkCommerce = 0,
                        landmarkIrrigationBonus = 0,
                        landmarkMiningBonus = 0,
                        landmarkRoadBonus = 0,
                        landmarkMovementBonus = 0,
                        landmarkDefensiveBonus = 0,
                        landmarkName = "",
                        landmarkCivilopediaEntry = "",
                        unknown2 = ByteString.of(*ByteArray(4)),
                        terrainFlags = 0,
                        diseaseStrength = 0,
                    ),
                ),
            ),
            PrtoSection(
                listOf(
                    PrtoEntry(
                        zoneOfControl = 0,
                        name = "Warrior",
                        civilopediaEntry = "Warrior",
                        bombardStrength = 0,
                        bombardRange = 0,
                        capacity = 0,
                        shieldCost = 10,
                        defense = 1,
                        iconIndex = 5,
                        attack = 1,
                        operationalRange = 0,
                        populationCost = 0,
                        rateOfFire = 1,
                        movement = 1,
                        required = 0,
                        upgradeTo = 3,
                        requiredResource1 = -1,
                        requiredResource2 = -1,
                        requiredResource3 = -1,
                        abilities = 0,
                        aiStrategies = 0,
                        availableTo = -1,
                        flags2 = ByteString.of(*ByteArray(8)),
                        type = 0,
                        otherStrategy = -1,
                        hpBonus = 0,
                        standardOrders = 0,
                        specialActions = 0,
                        workerActions = 0,
                        airMissions = 0,
                        flags4 = ByteString.of(*ByteArray(4)),
                        bombardEffects = 0,
                        ignoreMovementCost = ByteString.of(0),
                        requireSupport = 0,
                        unknown = ByteString.of(*ByteArray(16)),
                        enslaveResults = 0,
                        unknown2 = ByteString.of(*ByteArray(4)),
                        stealthTargetUnitTypes = listOf(4, 9),
                        unknown3 = ByteString.of(*ByteArray(8)),
                        createCraters = 0,
                        workerStrength = 1.5f,
                        unknown4 = ByteString.of(*ByteArray(4)),
                        airDefense = 0,
                    ),
                ),
            ),
        )
    }

    test("PRTO section before a later TERR section still produces a typed PrtoSection with the correct entry") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("PRTO", prtoItemBody()))
            writeAll(oneItemSectionBytes("TERR", terrItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            PrtoSection(
                listOf(
                    PrtoEntry(
                        zoneOfControl = 0,
                        name = "Warrior",
                        civilopediaEntry = "Warrior",
                        bombardStrength = 0,
                        bombardRange = 0,
                        capacity = 0,
                        shieldCost = 10,
                        defense = 1,
                        iconIndex = 5,
                        attack = 1,
                        operationalRange = 0,
                        populationCost = 0,
                        rateOfFire = 1,
                        movement = 1,
                        required = 0,
                        upgradeTo = 3,
                        requiredResource1 = -1,
                        requiredResource2 = -1,
                        requiredResource3 = -1,
                        abilities = 0,
                        aiStrategies = 0,
                        availableTo = -1,
                        flags2 = ByteString.of(*ByteArray(8)),
                        type = 0,
                        otherStrategy = -1,
                        hpBonus = 0,
                        standardOrders = 0,
                        specialActions = 0,
                        workerActions = 0,
                        airMissions = 0,
                        flags4 = ByteString.of(*ByteArray(4)),
                        bombardEffects = 0,
                        ignoreMovementCost = ByteString.of(0),
                        requireSupport = 0,
                        unknown = ByteString.of(*ByteArray(16)),
                        enslaveResults = 0,
                        unknown2 = ByteString.of(*ByteArray(4)),
                        stealthTargetUnitTypes = listOf(4, 9),
                        unknown3 = ByteString.of(*ByteArray(8)),
                        createCraters = 0,
                        workerStrength = 1.5f,
                        unknown4 = ByteString.of(*ByteArray(4)),
                        airDefense = 0,
                    ),
                ),
            ),
            TerrSection(
                listOf(
                    TerrEntry(
                        numberOfPossibleResources = 5,
                        possibleResources = ByteString.of(0b00010101.toByte()),
                        name = "Plains",
                        civilopediaEntry = "Plains",
                        irrigationBonus = 1,
                        miningBonus = 0,
                        roadBonus = 1,
                        defenseBonus = 0,
                        movementCost = 1,
                        food = 1,
                        shields = 1,
                        commerce = 0,
                        workerJobAllowed = -1,
                        pollutionEffect = -1,
                        allowCities = 1,
                        allowColonies = 1,
                        impassable = 0,
                        impassableByWheeled = 0,
                        allowAirfields = 1,
                        allowForts = 1,
                        allowOutposts = 1,
                        allowRadarTowers = 1,
                        unknown = ByteString.of(*ByteArray(4)),
                        landmarkEnabled = 0,
                        landmarkFood = 0,
                        landmarkShields = 0,
                        landmarkCommerce = 0,
                        landmarkIrrigationBonus = 0,
                        landmarkMiningBonus = 0,
                        landmarkRoadBonus = 0,
                        landmarkMovementBonus = 0,
                        landmarkDefensiveBonus = 0,
                        landmarkName = "",
                        landmarkCivilopediaEntry = "",
                        unknown2 = ByteString.of(*ByteArray(4)),
                        terrainFlags = 0,
                        diseaseStrength = 0,
                    ),
                ),
            ),
        )
    }

    test("PRTO section with no preceding TERR section throws RiffletParseException") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("PRTO", prtoItemBody()))
        }
        shouldThrow<RiffletParseException> { Civ3RootParser.parse(source) }
    }

    test("a section with an implausibly large item count throws RiffletParseException before attempting to allocate") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeString("BLDG", Charsets.US_ASCII)
            writeIntLe(Int.MAX_VALUE)
        }
        shouldThrow<RiffletParseException> { Civ3RootParser.parse(source) }
    }

    test("FLAV section with an implausibly large group count throws RiffletParseException before attempting to allocate") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeString("FLAV", Charsets.US_ASCII)
            writeIntLe(Int.MAX_VALUE)
        }
        shouldThrow<RiffletParseException> { Civ3RootParser.parse(source) }
    }

    test("BLDG section produces a typed BldgSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("BLDG", bldgItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            BldgSection(
                listOf(
                    BldgEntry(
                        description = "Provides defense",
                        name = "City Walls",
                        civilopediaEntry = "City Walls",
                        doublesHappiness = 0,
                        gainInEveryCity = 0,
                        gainInEveryCityOnContinent = 0,
                        requiredBuilding = -1,
                        cost = 60,
                        culture = 1,
                        bombardDefense = 0,
                        navalBombardDefense = 0,
                        defenseBonus = 100,
                        navalDefenseBonus = 0,
                        maintenanceCost = 0,
                        contentFacesAllCities = 0,
                        contentFaces = 0,
                        unhappyFacesAllCities = 0,
                        unhappyFaces = 0,
                        numberOfRequiredBuildings = 0,
                        airPower = 0,
                        navalPower = 0,
                        pollution = 0,
                        production = 0,
                        requiredGovernment = -1,
                        spaceshipPart = 0,
                        requiredAdvance = 5,
                        renderedObsoleteBy = -1,
                        requiredResource1 = -1,
                        requiredResource2 = -1,
                        flags = ByteString.of(*ByteArray(16)),
                        numberOfArmiesRequired = 0,
                        flavors = 0,
                        unknown = ByteString.of(*ByteArray(4)),
                        unitProduced = -1,
                        unitFrequency = 0,
                    ),
                ),
            ),
        )
    }

    test("TERR section produces a typed TerrSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("TERR", terrItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            TerrSection(
                listOf(
                    TerrEntry(
                        numberOfPossibleResources = 5,
                        possibleResources = ByteString.of(0b00010101.toByte()),
                        name = "Plains",
                        civilopediaEntry = "Plains",
                        irrigationBonus = 1,
                        miningBonus = 0,
                        roadBonus = 1,
                        defenseBonus = 0,
                        movementCost = 1,
                        food = 1,
                        shields = 1,
                        commerce = 0,
                        workerJobAllowed = -1,
                        pollutionEffect = -1,
                        allowCities = 1,
                        allowColonies = 1,
                        impassable = 0,
                        impassableByWheeled = 0,
                        allowAirfields = 1,
                        allowForts = 1,
                        allowOutposts = 1,
                        allowRadarTowers = 1,
                        unknown = ByteString.of(*ByteArray(4)),
                        landmarkEnabled = 0,
                        landmarkFood = 0,
                        landmarkShields = 0,
                        landmarkCommerce = 0,
                        landmarkIrrigationBonus = 0,
                        landmarkMiningBonus = 0,
                        landmarkRoadBonus = 0,
                        landmarkMovementBonus = 0,
                        landmarkDefensiveBonus = 0,
                        landmarkName = "",
                        landmarkCivilopediaEntry = "",
                        unknown2 = ByteString.of(*ByteArray(4)),
                        terrainFlags = 0,
                        diseaseStrength = 0,
                    ),
                ),
            ),
        )
    }

    test("GAME section produces a typed GameSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("GAME", gameItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            GameSection(
                listOf(
                    GameEntry(
                        defaultGameRules = 1,
                        defaultVictoryConditions = 1,
                        numberOfPlayableCivs = 2,
                        playableCivIds = listOf(3, 7),
                        flags = ByteString.of(*ByteArray(4)),
                        placeCaptureUnits = 0,
                        autoPlaceKings = 0,
                        autoPlaceVictoryLocations = 0,
                        debugMode = 0,
                        timeOptions = GameTimeOptions(
                            useTimeLimit = 0,
                            baseTimeUnit = 0,
                            startMonth = 1,
                            startWeek = 1,
                            startYear = -4000,
                            minuteTimeLimit = 0,
                            turnTimeLimit = 0,
                            timescaleNumberOfTurns = listOf(1, 2, 3, 4, 5, 6, 7),
                            turnNumberOfTimeUnits = listOf(10, 20, 30, 40, 50, 60, 70),
                        ),
                        scenarioSearchFolders = "Conquests",
                        civAllianceStatuses = listOf(0, 2),
                        victoryPointLimits = GameVictoryPointLimits(
                            victoryPointLimit = 0,
                            cityEliminationCount = 0,
                            oneCityCultureWin = 0,
                            allCitiesCultureWin = 0,
                            dominationTerrain = 0,
                            dominationPopulation = 0,
                            wonderCost = 0,
                            defeatingOpposingUnitCost = 0,
                            advancementCost = 0,
                            cityConquestPopulation = 0,
                            victoryPointScoring = 0,
                            capturingSpecialUnit = 0,
                            respawnFlagUnits = 0,
                            captureAnyFlag = 0,
                            goldForCapture = 0,
                        ),
                        unknown = ByteString.of(*ByteArray(5)),
                        lockedAlliance = GameLockedAlliance(
                            allianceNames = listOf("", "", "", "", ""),
                            allianceWars = List(25) { 0 },
                            allianceVictoryType = 0,
                        ),
                        plagueSettings = GamePlagueSettings(
                            plagueName = "",
                            permitPlagues = 0,
                            plagueEarliestStart = 0,
                            plagueVariation = 0,
                            plagueDuration = 0,
                            plagueStrength = 0,
                            plagueGracePeriod = 0,
                            plagueMaxOccurrence = 0,
                        ),
                        unknown2 = ByteString.of(*ByteArray(264)),
                        mapVisible = 1,
                        retainCulture = 0,
                        unknown3 = ByteString.of(*ByteArray(4)),
                        eruptionPeriod = 0,
                        mpTimers = GameMpTimers(
                            mpBasetime = 0,
                            mpCityTime = 0,
                            mpUnitTime = 0,
                        ),
                    ),
                ),
            ),
        )
    }

    test("TILE section produces a typed TileSection, not a raw fallback") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes())
            writeAll(oneItemSectionBytes("TILE", tileItemBody()))
        }
        val file = Civ3RootParser.parse(source)
        file.sections shouldBe listOf(
            TileSection(
                listOf(
                    TileEntry(
                        riverConnections = 0b00001111,
                        border = 1,
                        resource = 3,
                        textureLocation = 42,
                        textureFile = 5,
                        unknown = ByteString.of(0x11, 0x22),
                        overlayFlags = 2,
                        terrain = 0x21,
                        bonusFlags = 4,
                        riverCrossingFlags = 0,
                        barbarianTribe = 7,
                        colony = 9,
                        city = 2,
                        continent = 1,
                        unknown2 = ByteString.of(0x33),
                        victoryPointLocation = -1,
                        ruin = 100,
                        c3cOverlays = ByteString.of(0x01, 0x02, 0x03, 0x04),
                        unknown3 = ByteString.of(0x44),
                        c3cTerrain = 0x21,
                        unknown4 = ByteString.of(0x55, 0x66),
                        fogOfWar = 3,
                        c3cBonuses = ByteString.of(0x07, 0x08, 0x09, 0x0A),
                        unknown5 = ByteString.of(0x77, 0x12),
                        unknown6 = ByteString.of(0, 0, 0, 0),
                    ),
                ),
            ),
        )
    }

    test("oversized item at a confirmed (magic, major) throws RiffletParseException") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes(major = 4))
            writeAll(rawSectionBytes("BLDG", listOf(ByteArray(268))))
        }
        shouldThrow<RiffletParseException> { Civ3RootParser.parse(source) }
    }

    test("item at exactly the confirmed max for a real Conquests (magic, major) parses fine") {
        val source = Buffer().apply {
            writeString("BICX", Charsets.US_ASCII)
            writeAll(verSectionBytes(major = 12))
            writeAll(rawSectionBytes("BLDG", listOf(ByteArray(268))))
        }
        val file = Civ3RootParser.parse(source)
        file.sections.filterIsInstance<BldgSection>().single().entries.size shouldBe 1
    }

    test("TILE item exceeding the confirmed max for its exact major throws") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes(major = 3))
            writeAll(rawSectionBytes("TILE", listOf(ByteArray(24))))
        }
        shouldThrow<RiffletParseException> { Civ3RootParser.parse(source) }
    }

    test("TILE item at exactly the confirmed max for its exact major parses fine") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes(major = 3))
            writeAll(rawSectionBytes("TILE", listOf(ByteArray(23))))
        }
        val file = Civ3RootParser.parse(source)
        file.sections.filterIsInstance<TileSection>().single().entries.size shouldBe 1
    }

    test("oversized item at an unconfirmed (magic, major) combination does not throw") {
        val source = Buffer().apply {
            writeString("BIC ", Charsets.US_ASCII)
            writeAll(verSectionBytes(major = 2))
            writeAll(rawSectionBytes("BLDG", listOf(ByteArray(268))))
        }
        val file = Civ3RootParser.parse(source)
        file.sections.filterIsInstance<BldgSection>().single().entries.size shouldBe 1
    }
})
