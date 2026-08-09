package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.TfrmEntry
import com.kelvsyc.rifflet.civ3.WorkerJobSlot
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun tfrmEntry(
    name: String = "",
    turnsToComplete: Int = 0,
    required: Int = -1,
    requiredResource1: Int = -1,
    requiredResource2: Int = -1,
    order: String = "",
): TfrmEntry = TfrmEntry(
    name = name,
    civilopediaEntry = "",
    turnsToComplete = turnsToComplete,
    required = required,
    requiredResource1 = requiredResource1,
    requiredResource2 = requiredResource2,
    order = order,
)

private fun fillerJobs(count: Int): List<TfrmEntry> = List(count) { tfrmEntry(name = "Filler$it") }

private fun tech(name: String): Tech = Tech(name = name, civilopediaEntry = "", cost = 0, era = 0, advanceIcon = 0, x = 0, y = 0)

private fun resource(name: String): Resource = Resource(name = name)

class TfrmEntryMappingTest : FunSpec({

    test("toDomain requires exactly 9 entries for VANILLA") {
        shouldThrow<IllegalArgumentException> {
            fillerJobs(8).toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList())
        }
        fillerJobs(9).toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList()).size shouldBe 9
    }

    test("toDomain requires exactly 12 entries for PTW") {
        shouldThrow<IllegalArgumentException> {
            fillerJobs(9).toDomain(Civ3FormatEra.PTW, emptyList(), emptyList())
        }
        fillerJobs(12).toDomain(Civ3FormatEra.PTW, emptyList(), emptyList()).size shouldBe 12
    }

    test("toDomain requires exactly 13 entries for CONQUESTS") {
        shouldThrow<IllegalArgumentException> {
            fillerJobs(12).toDomain(Civ3FormatEra.CONQUESTS, emptyList(), emptyList())
        }
        fillerJobs(13).toDomain(Civ3FormatEra.CONQUESTS, emptyList(), emptyList()).size shouldBe 13
    }

    test("toDomain maps scalar fields straight across, keyed by WorkerJobSlot") {
        val entries = listOf(tfrmEntry(name = "Mine", turnsToComplete = 5, order = "Build Mine")) + fillerJobs(8)

        val mine = entries.toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList()).getValue(WorkerJobSlot.MINE)

        mine.name shouldBe "Mine"
        mine.turnsToComplete shouldBe 5
        mine.order shouldBe "Build Mine"
    }

    test("toDomain resolves required against the domain-converted TECH list, null when -1") {
        val bronzeWorking = tech("Bronze Working")
        val entries = listOf(tfrmEntry(name = "Mine", required = 0)) + fillerJobs(8)

        val mine = entries.toDomain(Civ3FormatEra.VANILLA, listOf(bronzeWorking), emptyList()).getValue(WorkerJobSlot.MINE)

        mine.required shouldBe bronzeWorking
    }

    test("toDomain resolves requiredResources against the domain-converted GOOD list, tolerating the same resource twice") {
        val iron = resource("Iron")
        val entries = listOf(tfrmEntry(name = "Mine", requiredResource1 = 0, requiredResource2 = 0)) + fillerJobs(8)

        val mine = entries.toDomain(Civ3FormatEra.VANILLA, emptyList(), listOf(iron)).getValue(WorkerJobSlot.MINE)

        mine.requiredResources shouldBe mutableListOf(iron, iron)
    }

    test("toDomain().toWire() round-trips scalar fields, required, and requiredResources") {
        val bronzeWorking = tech("Bronze Working")
        val iron = resource("Iron")
        val entries = listOf(
            tfrmEntry(
                name = "Mine", turnsToComplete = 5, required = 0,
                requiredResource1 = 0, requiredResource2 = -1, order = "Build Mine",
            ),
        ) + fillerJobs(8)

        val roundTripped = entries.toDomain(Civ3FormatEra.VANILLA, listOf(bronzeWorking), listOf(iron))
            .toWire(Civ3FormatEra.VANILLA, listOf(bronzeWorking), listOf(iron))

        roundTripped shouldBe entries
    }

    test("toWire requires exactly the slot set valid for era") {
        val incomplete = fillerJobs(9).toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList())
            .filterKeys { it != WorkerJobSlot.FORTRESS }

        shouldThrow<IllegalArgumentException> {
            incomplete.toWire(Civ3FormatEra.VANILLA, emptyList(), emptyList())
        }
    }

    test("toWire throws on a dangling required/requiredResources reference") {
        val outsiderTech = tech("Outsider")
        val outsiderResource = Resource(name = "Outsider")

        val withRequired = fillerJobs(9).toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList()).toMutableMap()
        withRequired[WorkerJobSlot.MINE] = withRequired.getValue(WorkerJobSlot.MINE).copy(required = outsiderTech)
        shouldThrow<IllegalArgumentException> { withRequired.toWire(Civ3FormatEra.VANILLA, emptyList(), emptyList()) }

        val withResource = fillerJobs(9).toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList()).toMutableMap()
        withResource[WorkerJobSlot.MINE] = withResource.getValue(WorkerJobSlot.MINE)
            .copy(requiredResources = mutableListOf(outsiderResource, null))
        shouldThrow<IllegalArgumentException> { withResource.toWire(Civ3FormatEra.VANILLA, emptyList(), emptyList()) }
    }

    test("toOrderedList returns WorkerJob values ordered by wire index for the given era") {
        val entries = listOf(tfrmEntry(name = "Mine"), tfrmEntry(name = "Irrigation")) + fillerJobs(7)
        val byIndex = entries.toDomain(Civ3FormatEra.VANILLA, emptyList(), emptyList())

        val ordered = byIndex.toOrderedList(Civ3FormatEra.VANILLA)

        ordered.size shouldBe 9
        ordered[0] shouldBe byIndex.getValue(WorkerJobSlot.MINE)
        ordered[1] shouldBe byIndex.getValue(WorkerJobSlot.IRRIGATION)
    }
})
