package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.Gender
import com.kelvsyc.rifflet.civ3.GovtCorruption
import com.kelvsyc.rifflet.civ3.GovtHurrying
import com.kelvsyc.rifflet.civ3.GovtRulerTitles
import com.kelvsyc.rifflet.civ3.GovtUnitSupportCosts
import com.kelvsyc.rifflet.civ3.GovtWarWeariness
import com.kelvsyc.rifflet.civ3.PrtoDomain
import com.kelvsyc.rifflet.civ3.RaceCultureGroup
import com.kelvsyc.rifflet.civ3.RaceEntry
import com.kelvsyc.rifflet.civ3.RaceEraFilenames
import com.kelvsyc.rifflet.civ3.RaceGovernor as WireRaceGovernor
import com.kelvsyc.rifflet.civ3.RaceLeader
import com.kelvsyc.rifflet.civ3.RacePersonality as WireRacePersonality
import io.kotest.assertions.throwables.shouldThrow
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

private fun tech(name: String = ""): Tech = Tech(
    name = name, civilopediaEntry = "", cost = 0, advanceIcon = 0, x = 0, y = 0,
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

private fun prto(name: String = ""): Prto = Prto(name = name, civilopediaEntry = "", iconIndex = 0, type = PrtoDomain.LAND)

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
        val unit = prto()
        val entry = raceEntry(unitTypeForKing = 0)

        val race = listOf(entry).toDomain(emptyList(), emptyList(), listOf(unit)).single()

        race.unitTypeForKing shouldBe unit
    }

    test("toDomain resolves each freeTechs slot against the provided techs list, preserving position") {
        val bronzeWorking = tech("Bronze Working")
        val entry = raceEntry(freeTechs = listOf(0, -1, 0, -1))

        val race = listOf(entry).toDomain(listOf(bronzeWorking), emptyList(), emptyList()).single()

        race.freeTechs shouldBe listOf(bronzeWorking, null, bronzeWorking, null)
    }

    test("toDomain().toWire() round-trips a full RACE section") {
        val bronzeWorking = tech("Bronze Working")
        val gov = government()
        val unit = prto()
        val entry = raceEntry(
            favoriteGovernment = 0,
            shunnedGovernment = 0,
            unitTypeForKing = 0,
            freeTechs = listOf(0, -1, 0, -1),
        )
        val original = listOf(entry)

        val roundTripped = original.toDomain(listOf(bronzeWorking), listOf(gov), listOf(unit))
            .toWire(listOf(bronzeWorking), listOf(gov), listOf(unit))

        roundTripped shouldBe original
    }

    test("toWire preserves a scattered (non-front-packed) freeTechs arrangement without reordering") {
        val bronzeWorking = tech("Bronze Working")
        val race = listOf(raceEntry(freeTechs = listOf(-1, 0, -1, -1)))
            .toDomain(listOf(bronzeWorking), emptyList(), emptyList())
            .single()

        val wire = listOf(race).toWire(listOf(bronzeWorking), emptyList(), emptyList()).single()

        wire.freeTechs shouldBe listOf(-1, 0, -1, -1)
    }

    test("toWire throws on a favoriteGovernment not present in the passed governments list") {
        val race = listOf(raceEntry()).toDomain(emptyList(), emptyList(), emptyList()).single()
        race.personality.favoriteGovernment = government()

        shouldThrow<IllegalArgumentException> {
            listOf(race).toWire(emptyList(), emptyList(), emptyList())
        }
    }

    test("toWire throws on a shunnedGovernment not present in the passed governments list") {
        val race = listOf(raceEntry()).toDomain(emptyList(), emptyList(), emptyList()).single()
        race.personality.shunnedGovernment = government()

        shouldThrow<IllegalArgumentException> {
            listOf(race).toWire(emptyList(), emptyList(), emptyList())
        }
    }

    test("toWire throws on a unitTypeForKing not present in the passed units list") {
        val race = listOf(raceEntry()).toDomain(emptyList(), emptyList(), emptyList()).single()
        race.unitTypeForKing = prto()

        shouldThrow<IllegalArgumentException> {
            listOf(race).toWire(emptyList(), emptyList(), emptyList())
        }
    }

    test("toWire throws on a freeTechs slot not present in the passed techs list") {
        val race = listOf(raceEntry()).toDomain(emptyList(), emptyList(), emptyList()).single()
        race.freeTechs[0] = tech()

        shouldThrow<IllegalArgumentException> {
            listOf(race).toWire(emptyList(), emptyList(), emptyList())
        }
    }
})
