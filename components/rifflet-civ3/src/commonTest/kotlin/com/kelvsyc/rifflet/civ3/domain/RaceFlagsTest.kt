package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.kotlin.core.collections.mutableEnumSetOf
import com.kelvsyc.rifflet.civ3.Gender
import com.kelvsyc.rifflet.civ3.RaceCultureGroup
import com.kelvsyc.rifflet.civ3.RaceLeader
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import com.kelvsyc.rifflet.civ3.FlavorSlot

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

    test("flavorSlots reads and writes the full FLAV membership set") {
        val race = validRace()

        race.flavorSlots shouldBe mutableEnumSetOf<FlavorSlot>()

        race.flavorSlots = mutableEnumSetOf(FlavorSlot.FLAVOR_1)

        race.flavorSlots shouldBe mutableEnumSetOf(FlavorSlot.FLAVOR_1)
        race.flavors shouldBe 1
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

    test("flavorSlots setter replaces the whole set, not just adds") {
        val race = validRace()
        race.flavorSlots = mutableEnumSetOf(FlavorSlot.FLAVOR_1, FlavorSlot.FLAVOR_2)

        race.flavorSlots = mutableEnumSetOf(FlavorSlot.FLAVOR_7)

        race.flavorSlots shouldBe mutableEnumSetOf(FlavorSlot.FLAVOR_7)
        race.flavors shouldBe (1 shl 6)
    }

    test("buildNeverCulture (highest buildNever bit, bit 14) is independently settable") {
        val race = validRace()

        race.buildNeverOffensiveLandUnits = true
        race.buildNeverCulture = true
        race.buildNeverOffensiveLandUnits = false

        race.buildNeverOffensiveLandUnits shouldBe false
        race.buildNeverCulture shouldBe true
        race.governor.buildNever shouldBe (1 shl 14)
    }

    test("buildOftenCulture (highest buildOften bit, bit 14) is independently settable") {
        val race = validRace()

        race.buildOftenOffensiveLandUnits = true
        race.buildOftenCulture = true
        race.buildOftenOffensiveLandUnits = false

        race.buildOftenOffensiveLandUnits shouldBe false
        race.buildOftenCulture shouldBe true
        race.governor.buildOften shouldBe (1 shl 14)
    }
})
