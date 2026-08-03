package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.Gender
import com.kelvsyc.rifflet.civ3.RaceCultureGroup
import com.kelvsyc.rifflet.civ3.RaceLeader
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun validRace(): Race = Race(
    name = "Rome",
    civilopediaEntry = "",
    adjective = "Roman",
    noun = "Romans",
    leader = RaceLeader(name = "", title = "", gender = Gender.MALE),
    cultureGroup = RaceCultureGroup.EUROPEAN,
    civilizationGender = Gender.MALE,
)

class RaceFlagsTest : FunSpec({

    test("militaristic is settable and backed by bonuses") {
        val race = validRace()

        race.militaristic shouldBe false
        race.militaristic = true
        race.militaristic shouldBe true
        race.bonuses shouldBe 1
    }

    test("flavor1 is settable and backed by flavors") {
        val race = validRace()

        race.flavor1 = true
        race.flavors shouldBe 1
        race.flavor1 shouldBe true
    }

    test("manageCitizens is settable and backed by governor.settings") {
        val race = validRace()

        race.manageCitizens = true
        race.governor.settings shouldBe 1
        race.manageCitizens shouldBe true
    }

    test("buildNeverOffensiveLandUnits is settable and backed by governor.buildNever") {
        val race = validRace()

        race.buildNeverOffensiveLandUnits = true
        race.governor.buildNever shouldBe 1
        race.buildNeverOffensiveLandUnits shouldBe true
    }

    test("buildOftenOffensiveLandUnits is settable and backed by governor.buildOften") {
        val race = validRace()

        race.buildOftenOffensiveLandUnits = true
        race.governor.buildOften shouldBe 1
        race.buildOftenOffensiveLandUnits shouldBe true
    }

    test("setting a flag false after true clears only that bit") {
        val race = validRace()

        race.militaristic = true
        race.commercial = true
        race.militaristic = false

        race.militaristic shouldBe false
        race.commercial shouldBe true
    }
})
