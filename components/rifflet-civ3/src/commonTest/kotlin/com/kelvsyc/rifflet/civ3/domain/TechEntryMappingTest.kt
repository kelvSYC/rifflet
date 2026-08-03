package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.TechEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun techEntry(
    name: String = "",
    era: Int = 0,
    prerequisite1: Int = -1,
    prerequisite2: Int = -1,
    prerequisite3: Int = -1,
    prerequisite4: Int = -1,
    flags: Int = 0,
    flavors: Int = 0,
    unknown: ByteString = ByteString.of(0, 0, 0, 0),
): TechEntry = TechEntry(
    name = name,
    civilopediaEntry = "civilopedia text",
    cost = 10,
    era = era,
    advanceIcon = 1,
    x = 2,
    y = 3,
    prerequisite1 = prerequisite1,
    prerequisite2 = prerequisite2,
    prerequisite3 = prerequisite3,
    prerequisite4 = prerequisite4,
    flags = flags,
    flavors = flavors,
    unknown = unknown,
)

class TechEntryMappingTest : FunSpec({

    test("toDomain maps scalar fields straight across") {
        val entry = techEntry(name = "Bronze Working", era = 0, flags = 5, flavors = 3, unknown = ByteString.of(9, 9, 9, 9))

        val tech = listOf(entry).toDomain().single()

        tech.name shouldBe "Bronze Working"
        tech.civilopediaEntry shouldBe "civilopedia text"
        tech.cost shouldBe 10
        tech.era shouldBe 0
        tech.advanceIcon shouldBe 1
        tech.x shouldBe 2
        tech.y shouldBe 3
        tech.flags shouldBe 5
        tech.flavors shouldBe 3
        tech.unknown shouldBe ByteString.of(9, 9, 9, 9)
    }

    test("toDomain resolves prerequisites against sibling entries, preserving each of the 4 slots") {
        val entries = listOf(
            techEntry(name = "Bronze Working"),
            techEntry(name = "Iron Working", prerequisite1 = 0),
            techEntry(name = "Currency", prerequisite2 = 0, prerequisite4 = 1),
        )

        val techs = entries.toDomain()

        techs[1].prerequisite1 shouldBe techs[0]
        techs[2].prerequisite2 shouldBe techs[0]
        techs[2].prerequisite4 shouldBe techs[1]
    }

    test("toDomain resolves -1/out-of-range prerequisites to null") {
        val entry = techEntry(prerequisite1 = -1, prerequisite2 = 99)

        val tech = listOf(entry).toDomain().single()

        tech.prerequisite1 shouldBe null
        tech.prerequisite2 shouldBe null
    }

    test("toDomain throws on a prerequisite cycle") {
        val entries = listOf(
            techEntry(name = "A", prerequisite1 = 1),
            techEntry(name = "B", prerequisite1 = 0),
        )

        shouldThrow<IllegalArgumentException> { entries.toDomain() }
    }

    test("toDomain throws on a self-loop") {
        val entries = listOf(techEntry(name = "A", prerequisite1 = 0))

        shouldThrow<IllegalArgumentException> { entries.toDomain() }
    }
})
