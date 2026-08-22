package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.Gender
import com.kelvsyc.rifflet.civ3.GovtCorruption
import com.kelvsyc.rifflet.civ3.GovtHurrying
import com.kelvsyc.rifflet.civ3.GovtRulerTitles
import com.kelvsyc.rifflet.civ3.GovtUnitSupportCosts
import com.kelvsyc.rifflet.civ3.GovtWarWeariness
import com.kelvsyc.rifflet.civ3.RaceCultureGroup
import com.kelvsyc.rifflet.civ3.RaceLeader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun validRace(): Race = Race(
    name = "Rome",
    civilopediaEntry = "",
    adjective = "Roman",
    noun = "Romans",
    leader = RaceLeader(name = "Caesar Augustus", title = "Emperor", gender = Gender.MALE),
    cultureGroup = RaceCultureGroup.EUROPEAN,
    civilizationGender = Gender.FEMALE,
)

private fun tech(name: String = ""): Tech = Tech(
    name = name, civilopediaEntry = "", cost = 0, advanceIcon = 0, x = 0, y = 0,
)

class RaceTest : FunSpec({

    test("constructing with only required params gives sensible defaults") {
        val race = validRace()

        race.personality shouldBe RacePersonality()
        race.uniqueCivilizationCounter shouldBe 0
        race.defaultColor shouldBe 0
        race.uniqueColor shouldBe 0
        race.freeTechs shouldBe listOf(null, null, null, null)
        race.bonuses shouldBe 0
        race.governor shouldBe RaceGovernor()
        race.plurality shouldBe 0
        race.unitTypeForKing shouldBe null
        race.flavors shouldBe 0
        race.unknown shouldBe ByteString.of(0, 0, 0, 0)
        race.diplomacyTextIndex shouldBe 0
        race.cityNames shouldBe emptyList()
        race.greatLeaderNames shouldBe emptyList()
        race.scientificLeaderNames shouldBe emptyList()
        race.eras shouldBe emptyList()
    }

    test("a freeTechs list of any other size throws IllegalArgumentException") {
        shouldThrow<IllegalArgumentException> {
            validRace().copy(freeTechs = mutableListOf(null, null, null))
        }
    }

    test("fields are mutable after construction") {
        val race = validRace()

        race.name = "Egypt"
        race.bonuses = 5

        race.name shouldBe "Egypt"
        race.bonuses shouldBe 5
    }

    test("freeTechsOf front-packs fewer than 4 techs, nulls trailing") {
        val bronzeWorking = tech("Bronze Working")

        freeTechsOf(bronzeWorking) shouldBe listOf(bronzeWorking, null, null, null)
        freeTechsOf() shouldBe listOf(null, null, null, null)
    }

    test("freeTechsOf with exactly 4 techs fills every slot") {
        val techs = List(4) { tech("Tech$it") }

        freeTechsOf(techs[0], techs[1], techs[2], techs[3]) shouldBe techs
    }

    test("freeTechsOf throws on more than 4 techs") {
        val techs = List(5) { tech("Tech$it") }

        shouldThrow<IllegalArgumentException> {
            freeTechsOf(techs[0], techs[1], techs[2], techs[3], techs[4])
        }
    }

    test("RacePersonality can hold a real Government reference") {
        val government = Government(
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
        val race = validRace()
        race.personality.favoriteGovernment = government

        race.personality.favoriteGovernment shouldBe government
    }
})
