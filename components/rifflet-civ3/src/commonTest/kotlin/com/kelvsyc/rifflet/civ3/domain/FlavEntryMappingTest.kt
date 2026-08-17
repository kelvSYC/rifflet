package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.FlavGroupEntry
import com.kelvsyc.rifflet.civ3.FlavorEntry
import com.kelvsyc.rifflet.civ3.FlavorSlot
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun flavorEntry(name: String, relations: List<Int> = List(7) { 0 }): FlavorEntry =
    FlavorEntry(unknown = ByteString.of(0, 0, 0, 0), name = name, relations = relations)

private fun sevenFlavorGroup(relations: (Int, Int) -> Int = { _, _ -> 0 }): FlavGroupEntry = FlavGroupEntry(
    flavors = (0 until 7).map { i ->
        flavorEntry(name = "Flavor${i + 1}", relations = (0 until 7).map { j -> relations(i, j) })
    },
)

class FlavEntryMappingTest : FunSpec({

    test("toDomain requires exactly 7 flavors per group") {
        val tooFew = FlavGroupEntry(flavors = sevenFlavorGroup().flavors.drop(1))

        shouldThrow<IllegalArgumentException> {
            listOf(tooFew).toDomain()
        }
    }

    test("toDomain requires exactly 7 relations per flavor") {
        val badRelations = FlavGroupEntry(
            flavors = listOf(flavorEntry(name = "Flavor1", relations = listOf(1, 2))) +
                sevenFlavorGroup().flavors.drop(1),
        )

        shouldThrow<IllegalArgumentException> {
            listOf(badRelations).toDomain()
        }
    }

    test("toDomain maps flavor identities straight across, keyed by FlavorSlot") {
        val group = sevenFlavorGroup()

        val flavorGroup = listOf(group).toDomain().single()

        flavorGroup.flavors.getValue(FlavorSlot.FLAVOR_3).name shouldBe "Flavor3"
    }

    test("toDomain maps relations[from.index][to.index] to relations[from, to]") {
        val group = sevenFlavorGroup { i, j -> i * 10 + j }

        val flavorGroup = listOf(group).toDomain().single()

        // FLAVOR_1 index=0, FLAVOR_3 index=2 -> flavors[0].relations[2] == 0*10+2 == 2
        flavorGroup.relations[FlavorSlot.FLAVOR_1, FlavorSlot.FLAVOR_3] shouldBe 2
        // FLAVOR_5 index=4, FLAVOR_2 index=1 -> flavors[4].relations[1] == 4*10+1 == 41
        flavorGroup.relations[FlavorSlot.FLAVOR_5, FlavorSlot.FLAVOR_2] shouldBe 41
    }

    test("toDomain().toWire() round-trips") {
        val entries = listOf(sevenFlavorGroup { i, j -> i * 10 + j })

        val roundTripped = entries.toDomain().toWire()

        roundTripped shouldBe entries
    }

    test("toWire throws if flavors isn't keyed by exactly the 7 FlavorSlot values") {
        val flavorGroup = listOf(sevenFlavorGroup()).toDomain().single()
        flavorGroup.flavors.remove(FlavorSlot.FLAVOR_7)

        shouldThrow<IllegalArgumentException> { listOf(flavorGroup).toWire() }
    }

    test("toWire throws if relations is missing any (from, to) pair") {
        val flavorGroup = FlavorGroup(
            flavors = FlavorSlot.entries.associateWith { Flavor(name = it.name) }.toMutableMap(),
            relations = FlavorRelations(),
        )

        shouldThrow<IllegalArgumentException> { listOf(flavorGroup).toWire() }
    }
})
