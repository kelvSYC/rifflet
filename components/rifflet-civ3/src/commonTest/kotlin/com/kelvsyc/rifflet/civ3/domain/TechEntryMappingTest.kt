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

private fun era(name: String): Era = Era(name = name)

class TechEntryMappingTest : FunSpec({

    test("toDomain maps scalar fields straight across") {
        val entry = techEntry(name = "Bronze Working", era = -1, flags = 5, flavors = 3, unknown = ByteString.of(9, 9, 9, 9))

        val tech = listOf(entry).toDomain(emptyList()).single()

        tech.name shouldBe "Bronze Working"
        tech.civilopediaEntry shouldBe "civilopedia text"
        tech.cost shouldBe 10
        tech.advanceIcon shouldBe 1
        tech.x shouldBe 2
        tech.y shouldBe 3
        tech.flags shouldBe 5
        tech.flavors shouldBe 3
        tech.unknown shouldBe ByteString.of(9, 9, 9, 9)
    }

    test("toDomain resolves era against the supplied ERAS list, null for a dangling index") {
        val ancient = era("Ancient Times")
        val entries = listOf(techEntry(name = "Bronze Working", era = 0), techEntry(name = "Never", era = -1))

        val techs = entries.toDomain(listOf(ancient))

        techs[0].era shouldBe ancient
        techs[1].era shouldBe null
    }

    test("toDomain resolves prerequisites against sibling entries, preserving each of the 4 slots") {
        val entries = listOf(
            techEntry(name = "Bronze Working"),
            techEntry(name = "Iron Working", prerequisite1 = 0),
            techEntry(name = "Currency", prerequisite2 = 0, prerequisite4 = 1),
        )

        val techs = entries.toDomain(emptyList())

        techs[1].prerequisite1 shouldBe techs[0]
        techs[2].prerequisite2 shouldBe techs[0]
        techs[2].prerequisite4 shouldBe techs[1]
    }

    test("toDomain resolves -1/out-of-range prerequisites to null") {
        val entry = techEntry(prerequisite1 = -1, prerequisite2 = 99)

        val tech = listOf(entry).toDomain(emptyList()).single()

        tech.prerequisite1 shouldBe null
        tech.prerequisite2 shouldBe null
    }

    test("toDomain throws on a prerequisite cycle") {
        val entries = listOf(
            techEntry(name = "A", prerequisite1 = 1),
            techEntry(name = "B", prerequisite1 = 0),
        )

        shouldThrow<IllegalArgumentException> { entries.toDomain(emptyList()) }
    }

    test("toDomain throws on a self-loop") {
        val entries = listOf(techEntry(name = "A", prerequisite1 = 0))

        shouldThrow<IllegalArgumentException> { entries.toDomain(emptyList()) }
    }

    test("toDomain().toWire() round-trips a full TECH section") {
        val ancient = era("Ancient Times")
        val entries = listOf(
            techEntry(name = "Bronze Working", era = 0, flags = 5, flavors = 3, unknown = ByteString.of(9, 9, 9, 9)),
            techEntry(name = "Iron Working", era = 0, prerequisite1 = 0),
            techEntry(name = "Currency", era = 0, prerequisite2 = 0, prerequisite4 = 1),
        )

        val roundTripped = entries.toDomain(listOf(ancient)).toWire(listOf(ancient))

        roundTripped shouldBe entries
    }

    test("toWire throws on a prerequisite1 not present in the passed-through roster") {
        val tech = listOf(techEntry(name = "A")).toDomain(emptyList()).single()
        val outsider = listOf(techEntry(name = "Outsider")).toDomain(emptyList()).single()
        tech.prerequisite1 = outsider

        shouldThrow<IllegalArgumentException> { listOf(tech).toWire(emptyList()) }
    }

    test("toWire throws on an era not present in the passed-through eras list") {
        val ancient = era("Ancient Times")
        val tech = listOf(techEntry(name = "A", era = 0)).toDomain(listOf(ancient)).single()
        val outsider = era("Outsider Era")
        tech.era = outsider

        shouldThrow<IllegalArgumentException> { listOf(tech).toWire(listOf(ancient)) }
    }

    test("toWire writes -1 for a null era") {
        val tech = listOf(techEntry(name = "A", era = -1)).toDomain(emptyList()).single()

        val wire = listOf(tech).toWire(emptyList()).single()

        wire.era shouldBe -1
    }
})
