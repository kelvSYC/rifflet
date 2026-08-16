package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.GoodEntry
import com.kelvsyc.rifflet.civ3.GoodResourceType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun goodEntry(
    name: String = "",
    type: GoodResourceType = GoodResourceType.LUXURY,
    appearanceRatio: Int = 0,
    disappearanceProbability: Int = 0,
    icon: Int = 0,
    prerequisite: Int = -1,
    foodBonus: Int = 0,
    shieldsBonus: Int = 0,
    commerceBonus: Int = 0,
): GoodEntry = GoodEntry(
    name = name,
    civilopediaEntry = "",
    type = type,
    appearanceRatio = appearanceRatio,
    disappearanceProbability = disappearanceProbability,
    icon = icon,
    prerequisite = prerequisite,
    foodBonus = foodBonus,
    shieldsBonus = shieldsBonus,
    commerceBonus = commerceBonus,
)

private fun tech(name: String = ""): Tech = Tech(
    name = name, civilopediaEntry = "", cost = 0, advanceIcon = 0, x = 0, y = 0,
)

class GoodEntryMappingTest : FunSpec({

    test("toDomain maps scalar fields straight across") {
        val entry = goodEntry(
            name = "Wine", type = GoodResourceType.LUXURY, appearanceRatio = 50,
            disappearanceProbability = 10, icon = 3, foodBonus = 1, shieldsBonus = 2, commerceBonus = 3,
        )

        val resource = listOf(entry).toDomain(emptyList()).single()

        resource.name shouldBe "Wine"
        resource.type shouldBe GoodResourceType.LUXURY
        resource.appearanceRatio shouldBe 50
        resource.disappearanceProbability shouldBe 10
        resource.icon shouldBe 3
        resource.foodBonus shouldBe 1
        resource.shieldsBonus shouldBe 2
        resource.commerceBonus shouldBe 3
    }

    test("toDomain resolves prerequisite against the domain-converted TECH list, null when absent") {
        val bronzeWorking = tech("Bronze Working")
        val withPrereq = listOf(goodEntry(prerequisite = 0)).toDomain(listOf(bronzeWorking)).single()
        val withoutPrereq = listOf(goodEntry(prerequisite = -1)).toDomain(listOf(bronzeWorking)).single()

        withPrereq.prerequisite shouldBe bronzeWorking
        withoutPrereq.prerequisite shouldBe null
    }

    test("toDomain().toWire() round-trips scalar fields and a resolved prerequisite") {
        val bronzeWorking = tech("Bronze Working")
        val entries = listOf(
            goodEntry(
                name = "Iron", type = GoodResourceType.STRATEGIC, appearanceRatio = 30,
                disappearanceProbability = 5, icon = 7, prerequisite = 0,
                foodBonus = 0, shieldsBonus = 3, commerceBonus = 0,
            ),
        )

        val roundTripped = entries.toDomain(listOf(bronzeWorking)).toWire(listOf(bronzeWorking))

        roundTripped shouldBe entries
    }

    test("toWire writes -1 for a null prerequisite") {
        val resource = Resource(name = "Whales")

        val wire = listOf(resource).toWire(emptyList()).single()

        wire.prerequisite shouldBe -1
    }

    test("toWire throws on a dangling prerequisite reference") {
        val resource = Resource(name = "Whales", prerequisite = tech("Outsider"))

        shouldThrow<IllegalArgumentException> {
            listOf(resource).toWire(emptyList())
        }
    }
})
