package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.CtznEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun ctznEntry(
    singularName: String = "",
    pluralName: String = "",
    defaultCitizen: Int = 0,
    prerequisite: Int = -1,
    luxuries: Int = 0,
    research: Int = 0,
    taxes: Int = 0,
    corruption: Int = 0,
    construction: Int = 0,
): CtznEntry = CtznEntry(
    defaultCitizen = defaultCitizen, singularName = singularName, civilopediaEntry = "",
    pluralName = pluralName, prerequisite = prerequisite, luxuries = luxuries, research = research,
    taxes = taxes, corruption = corruption, construction = construction,
)

private fun tech(name: String = ""): Tech = Tech(
    name = name, civilopediaEntry = "", cost = 0, advanceIcon = 0, x = 0, y = 0,
)

class CtznEntryMappingTest : FunSpec({

    test("toDomain maps scalar fields straight across") {
        val entry = ctznEntry(
            singularName = "Entertainer", pluralName = "Entertainers", defaultCitizen = 1,
            luxuries = 1, research = 2, taxes = 3, corruption = 4, construction = 5,
        )

        val citizen = listOf(entry).toDomain(emptyList()).single()

        citizen.singularName shouldBe "Entertainer"
        citizen.pluralName shouldBe "Entertainers"
        citizen.isDefault shouldBe true
        citizen.luxuries shouldBe 1
        citizen.research shouldBe 2
        citizen.taxes shouldBe 3
        citizen.corruption shouldBe 4
        citizen.construction shouldBe 5
    }

    test("toDomain resolves prerequisite against the domain-converted TECH list, null when dangling") {
        val bronzeWorking = tech("Bronze Working")
        val entries = listOf(ctznEntry(prerequisite = 0), ctznEntry(prerequisite = -1))

        val citizens = entries.toDomain(listOf(bronzeWorking))

        citizens[0].prerequisite shouldBe bronzeWorking
        citizens[1].prerequisite shouldBe null
    }

    test("toDomain().toWire() round-trips") {
        val bronzeWorking = tech("Bronze Working")
        val entries = listOf(ctznEntry(singularName = "Entertainer", defaultCitizen = 1, prerequisite = 0))

        val roundTripped = entries.toDomain(listOf(bronzeWorking)).toWire(listOf(bronzeWorking))

        roundTripped shouldBe entries
    }

    test("toWire throws on a dangling prerequisite reference") {
        val bronzeWorking = tech("Bronze Working")
        val citizen = listOf(ctznEntry(prerequisite = 0)).toDomain(listOf(bronzeWorking)).single()
        val outsider = tech("Outsider")
        citizen.prerequisite = outsider

        shouldThrow<IllegalArgumentException> { listOf(citizen).toWire(listOf(bronzeWorking)) }
    }
})
