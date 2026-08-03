package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.EspnEntry
import com.kelvsyc.rifflet.civ3.ExprEntry
import com.kelvsyc.rifflet.civ3.Gender
import com.kelvsyc.rifflet.civ3.GovtCorruption
import com.kelvsyc.rifflet.civ3.GovtHurrying
import com.kelvsyc.rifflet.civ3.GovtRulerTitles
import com.kelvsyc.rifflet.civ3.GovtUnitSupportCosts
import com.kelvsyc.rifflet.civ3.GovtWarWeariness
import com.kelvsyc.rifflet.civ3.PrtoDomain
import com.kelvsyc.rifflet.civ3.PrtoEntry
import com.kelvsyc.rifflet.civ3.PrtoUnitStatistics
import com.kelvsyc.rifflet.civ3.RaceCultureGroup
import com.kelvsyc.rifflet.civ3.RaceEntry
import com.kelvsyc.rifflet.civ3.RaceEraFilenames
import com.kelvsyc.rifflet.civ3.RaceGovernor as WireRaceGovernor
import com.kelvsyc.rifflet.civ3.RaceLeader
import com.kelvsyc.rifflet.civ3.RacePersonality as WireRacePersonality
import com.kelvsyc.rifflet.civ3.TechEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun raceEntry(
    name: String = "Rome",
    freeTechs: List<Int> = listOf(-1, -1, -1, -1),
    favoriteGovernment: Int = -1,
    shunnedGovernment: Int = -1,
    unitTypeForKing: Int = -1,
): RaceEntry = RaceEntry(
    cityNames = listOf("Roma"),
    greatLeaderNames = listOf("Caesar"),
    leader = RaceLeader(name = "Caesar Augustus", title = "Emperor", gender = Gender.MALE),
    civilopediaEntry = "civilopedia text",
    adjective = "Roman",
    name = name,
    noun = "Romans",
    eras = listOf(RaceEraFilenames("anc_fwd", "anc_rev")),
    cultureGroup = RaceCultureGroup.EUROPEAN,
    civilizationGender = Gender.FEMALE,
    personality = WireRacePersonality(
        favoriteGovernment = favoriteGovernment,
        shunnedGovernment = shunnedGovernment,
        aggressionLevel = 3,
    ),
    uniqueCivilizationCounter = 1,
    defaultColor = 2,
    uniqueColor = 3,
    freeTechs = freeTechs,
    bonuses = 5,
    governor = WireRaceGovernor(settings = 17, buildNever = 0, buildOften = 0),
    plurality = 1,
    unitTypeForKing = unitTypeForKing,
    flavors = 3,
    unknown = ByteString.of(9, 9, 9, 9),
    diplomacyTextIndex = 7,
    scientificLeaderNames = listOf("Archimedes"),
)

private fun techEntry(): TechEntry = TechEntry(
    name = "", civilopediaEntry = "", cost = 0, era = 0, advanceIcon = 0, x = 0, y = 0,
    prerequisite1 = 0, prerequisite2 = 0, prerequisite3 = 0, prerequisite4 = 0,
    flags = 0, flavors = 0, unknown = ByteString.of(0, 0, 0, 0),
)

private fun government(): Government = Government(
    name = "Despotism",
    civilopediaEntry = "",
    rulerTitles = GovtRulerTitles(
        male1 = "", female1 = "", male2 = "", female2 = "", male3 = "", female3 = "", male4 = "", female4 = "",
    ),
    corruption = GovtCorruption.RAMPANT,
    hurrying = GovtHurrying.CANNOT_HURRY,
    unitSupportCosts = GovtUnitSupportCosts(
        freeUnits = 0, freeUnitsPerTown = 0, freeUnitsPerCity = 0, freeUnitsPerMetropolis = 0, unitCost = 0,
    ),
    warWeariness = GovtWarWeariness.NONE,
)

private fun prtoEntry(): PrtoEntry = PrtoEntry(
    unitStatistics = PrtoUnitStatistics(
        zoneOfControl = 0, bombardStrength = 0, bombardRange = 0, capacity = 0, shieldCost = 0,
        defense = 0, attack = 0, operationalRange = 0, populationCost = 0, rateOfFire = 0,
        movement = 0, upgradeTo = 0, hpBonus = 0, bombardEffects = 0, requireSupport = 0,
        createCraters = 0, workerStrength = 0f, airDefense = 0,
    ),
    name = "", civilopediaEntry = "", iconIndex = 0, required = 0,
    requiredResource1 = 0, requiredResource2 = 0, requiredResource3 = 0,
    abilities = 0, aiStrategies = 0, availableTo = 0,
    flags2 = ByteString.of(*ByteArray(8)), type = PrtoDomain.LAND, otherStrategy = 0,
    standardOrders = 0, specialActions = 0, workerActions = 0, airMissions = 0,
    flags4 = ByteString.of(*ByteArray(4)), ignoreMovementCost = ByteString.of(),
    unknown = ByteString.of(*ByteArray(16)), enslaveResults = 0, unknown2 = ByteString.of(0, 0, 0, 0),
    stealthTargetUnitTypes = emptyList(), unknown3 = ByteString.of(*ByteArray(8)), unknown4 = ByteString.of(0, 0, 0, 0),
)

class RaceEntryMappingTest : FunSpec({

    test("toDomain maps scalar and grouped fields straight across") {
        val entry = raceEntry()

        val race = listOf(entry).toDomain(emptyList(), emptyList(), emptyList()).single()

        race.name shouldBe "Rome"
        race.civilopediaEntry shouldBe "civilopedia text"
        race.adjective shouldBe "Roman"
        race.noun shouldBe "Romans"
        race.leader shouldBe RaceLeader(name = "Caesar Augustus", title = "Emperor", gender = Gender.MALE)
        race.cultureGroup shouldBe RaceCultureGroup.EUROPEAN
        race.civilizationGender shouldBe Gender.FEMALE
        race.personality.aggressionLevel shouldBe 3
        race.uniqueCivilizationCounter shouldBe 1
        race.defaultColor shouldBe 2
        race.uniqueColor shouldBe 3
        race.bonuses shouldBe 5
        race.governor shouldBe RaceGovernor(settings = 17, buildNever = 0, buildOften = 0)
        race.plurality shouldBe 1
        race.flavors shouldBe 3
        race.unknown shouldBe ByteString.of(9, 9, 9, 9)
        race.diplomacyTextIndex shouldBe 7
        race.cityNames shouldBe listOf("Roma")
        race.greatLeaderNames shouldBe listOf("Caesar")
        race.scientificLeaderNames shouldBe listOf("Archimedes")
        race.eras shouldBe listOf(RaceEraFilenames("anc_fwd", "anc_rev"))
    }

    test("toDomain resolves personality's GOVT cross-refs against the provided Government list") {
        val gov = government()
        val entry = raceEntry(favoriteGovernment = 0, shunnedGovernment = 0)

        val race = listOf(entry).toDomain(emptyList(), listOf(gov), emptyList()).single()

        race.personality.favoriteGovernment shouldBe gov
        race.personality.shunnedGovernment shouldBe gov
    }

    test("toDomain resolves -1/out-of-range GOVT cross-refs to null") {
        val entry = raceEntry(favoriteGovernment = -1, shunnedGovernment = -1)

        val race = listOf(entry).toDomain(emptyList(), emptyList(), emptyList()).single()

        race.personality.favoriteGovernment shouldBe null
        race.personality.shunnedGovernment shouldBe null
    }

    test("toDomain resolves unitTypeForKing against the provided units list") {
        val unit = prtoEntry()
        val entry = raceEntry(unitTypeForKing = 0)

        val race = listOf(entry).toDomain(emptyList(), emptyList(), listOf(unit)).single()

        race.unitTypeForKing shouldBe unit
    }

    test("toDomain resolves each freeTechs slot against the provided techs list, preserving position") {
        val tech = techEntry()
        val entry = raceEntry(freeTechs = listOf(0, -1, 0, -1))

        val race = listOf(entry).toDomain(listOf(tech), emptyList(), emptyList()).single()

        race.freeTechs shouldBe listOf(tech, null, tech, null)
    }
})
