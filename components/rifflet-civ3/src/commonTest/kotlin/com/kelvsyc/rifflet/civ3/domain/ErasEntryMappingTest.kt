package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.EraSlot
import com.kelvsyc.rifflet.civ3.ErasEntry
import com.kelvsyc.rifflet.civ3.index
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun erasEntry(
    name: String = "",
    civilopediaEntry: String = "",
    researcher1: String = "",
    researcher2: String = "",
    researcher3: String = "",
    researcher4: String = "",
    researcher5: String = "",
    numberOfUsedResearcherNames: Int = 0,
    unknown: ByteString = ByteString.of(0, 0, 0, 0),
): ErasEntry = ErasEntry(
    name = name, civilopediaEntry = civilopediaEntry, researcher1 = researcher1, researcher2 = researcher2,
    researcher3 = researcher3, researcher4 = researcher4, researcher5 = researcher5,
    numberOfUsedResearcherNames = numberOfUsedResearcherNames, unknown = unknown,
)

private fun fourErasEntries(): List<ErasEntry> = listOf(
    erasEntry(name = "Ancient Times"),
    erasEntry(name = "Middle Ages"),
    erasEntry(name = "Industrial Ages"),
    erasEntry(name = "Modern Times"),
)

class ErasEntryMappingTest : FunSpec({

    test("toDomain requires exactly 4 entries") {
        shouldThrow<IllegalArgumentException> {
            fourErasEntries().drop(1).toDomain()
        }
        fourErasEntries().toDomain().size shouldBe 4
    }

    test("toDomain maps scalar fields straight across, keyed by EraSlot") {
        val byIndex = fourErasEntries().toDomain()

        val middleAges = byIndex.getValue(EraSlot.MIDDLE_AGES)
        middleAges.name shouldBe "Middle Ages"
    }

    test("toDomain slices researchers to numberOfUsedResearcherNames, ignoring trailing slots") {
        val entries = fourErasEntries().toMutableList()
        entries[0] = erasEntry(
            name = "Ancient Times", researcher1 = "Aristotle", researcher2 = "Plato",
            researcher3 = "leftover", numberOfUsedResearcherNames = 2,
        )

        val ancient = entries.toDomain().getValue(EraSlot.ANCIENT_TIMES)

        ancient.researchers shouldBe mutableListOf("Aristotle", "Plato")
    }

    test("toDomain().toWire() round-trips") {
        val entries = fourErasEntries().toMutableList()
        entries[0] = erasEntry(
            name = "Ancient Times", researcher1 = "Aristotle", researcher2 = "Plato",
            numberOfUsedResearcherNames = 2,
        )

        val roundTripped = entries.toDomain().toWire()

        roundTripped shouldBe entries
    }

    test("toWire pads researchers back out to 5 wire slots with empty strings, deriving numberOfUsedResearcherNames") {
        val byIndex = fourErasEntries().toDomain().toMutableMap()
        byIndex[EraSlot.ANCIENT_TIMES] = byIndex.getValue(EraSlot.ANCIENT_TIMES).copy(researchers = mutableListOf("Aristotle"))

        val wire = byIndex.toWire()[EraSlot.ANCIENT_TIMES.index]

        wire.researcher1 shouldBe "Aristotle"
        wire.researcher2 shouldBe ""
        wire.researcher5 shouldBe ""
        wire.numberOfUsedResearcherNames shouldBe 1
    }

    test("toWire throws if researchers has more than 5 entries") {
        val byIndex = fourErasEntries().toDomain().toMutableMap()
        byIndex[EraSlot.ANCIENT_TIMES] = byIndex.getValue(EraSlot.ANCIENT_TIMES).copy(
            researchers = mutableListOf("1", "2", "3", "4", "5", "6"),
        )

        shouldThrow<IllegalArgumentException> {
            byIndex.toWire()
        }
    }

    test("toWire requires exactly the 4 slot keys") {
        val incomplete = fourErasEntries().toDomain().filterKeys { it != EraSlot.MODERN_TIMES }

        shouldThrow<IllegalArgumentException> {
            incomplete.toWire()
        }
    }

    test("toOrderedList returns Era values ordered by wire index") {
        val byIndex = fourErasEntries().toDomain()

        val ordered = byIndex.toOrderedList()

        ordered.size shouldBe 4
        ordered[0] shouldBe byIndex.getValue(EraSlot.ANCIENT_TIMES)
        ordered[3] shouldBe byIndex.getValue(EraSlot.MODERN_TIMES)
    }
})
