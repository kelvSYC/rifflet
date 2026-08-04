package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.BldgCombatValues
import com.kelvsyc.rifflet.civ3.BldgEntry
import com.kelvsyc.rifflet.civ3.BldgHappiness
import com.kelvsyc.rifflet.civ3.BldgRequiredResources as WireBldgRequiredResources
import com.kelvsyc.rifflet.civ3.BldgRequirements as WireBldgRequirements
import com.kelvsyc.rifflet.civ3.BldgUnitsProduced as WireBldgUnitsProduced
import com.kelvsyc.rifflet.civ3.GoodEntry
import com.kelvsyc.rifflet.civ3.GoodResourceType
import com.kelvsyc.rifflet.civ3.GovtCorruption
import com.kelvsyc.rifflet.civ3.GovtHurrying
import com.kelvsyc.rifflet.civ3.GovtRulerTitles
import com.kelvsyc.rifflet.civ3.GovtUnitSupportCosts
import com.kelvsyc.rifflet.civ3.GovtWarWeariness
import com.kelvsyc.rifflet.civ3.PrtoDomain
import com.kelvsyc.rifflet.civ3.PrtoEntry
import com.kelvsyc.rifflet.civ3.PrtoUnitStatistics
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import okio.ByteString

private fun bldgEntry(
    name: String = "",
    doublesHappiness: Int = -1,
    gainInEveryCity: Int = -1,
    gainInEveryCityOnContinent: Int = -1,
    requiredBuilding: Int = -1,
    requiredGovernment: Int = -1,
    requiredAdvance: Int = -1,
    spaceshipPart: Int = -1,
    renderedObsoleteBy: Int = -1,
    requiredResource1: Int = -1,
    requiredResource2: Int = -1,
    numberOfRequiredBuildings: Int = 0,
    wonderBit: Boolean = false,
    smallWonderBit: Boolean = false,
    wonders: Int = 0,
    smallWonders: Int = 0,
    unitProduced: Int? = -1,
): BldgEntry {
    val otherCharacteristics = (if (wonderBit) 1 shl 2 else 0) or (if (smallWonderBit) 1 shl 3 else 0)
    return BldgEntry(
        description = "",
        name = name,
        civilopediaEntry = "",
        doublesHappiness = doublesHappiness,
        gainInEveryCity = gainInEveryCity,
        gainInEveryCityOnContinent = gainInEveryCityOnContinent,
        requirements = WireBldgRequirements(requiredBuilding, requiredGovernment, requiredAdvance),
        cost = 0,
        culture = 0,
        combatValues = BldgCombatValues(0, 0, 0, 0, 0),
        navalDefenseBonus = 0,
        maintenanceCost = 0,
        happiness = BldgHappiness(0, 0, 0, 0),
        numberOfRequiredBuildings = numberOfRequiredBuildings,
        pollution = 0,
        production = 0,
        spaceshipPart = spaceshipPart,
        renderedObsoleteBy = renderedObsoleteBy,
        requiredResources = WireBldgRequiredResources(requiredResource1, requiredResource2),
        flags = ByteString.of(
            0, 0, 0, 0,
            *intToLeBytes(otherCharacteristics),
            *intToLeBytes(smallWonders),
            *intToLeBytes(wonders),
        ),
        numberOfArmiesRequired = 0,
        flavors = 0,
        unknown = ByteString.of(0, 0, 0, 0),
        unitsProduced = unitProduced?.let { WireBldgUnitsProduced(it, 0) },
    )
}

private fun intToLeBytes(value: Int): ByteArray = byteArrayOf(
    (value and 0xFF).toByte(),
    ((value shr 8) and 0xFF).toByte(),
    ((value shr 16) and 0xFF).toByte(),
    ((value shr 24) and 0xFF).toByte(),
)

private fun government(): Government = Government(
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

private fun tech(name: String = ""): Tech = Tech(
    name = name, civilopediaEntry = "", cost = 0, era = 0, advanceIcon = 0, x = 0, y = 0,
)

private fun good(): GoodEntry = GoodEntry(
    name = "", civilopediaEntry = "", type = GoodResourceType.LUXURY,
    appearanceRatio = 0, disappearanceProbability = 0, icon = 0, prerequisite = 0,
    foodBonus = 0, shieldsBonus = 0, commerceBonus = 0,
)

private fun prto(): PrtoEntry = PrtoEntry(
    unitStatistics = PrtoUnitStatistics(
        zoneOfControl = 0, bombardStrength = 0, bombardRange = 0, capacity = 0, shieldCost = 0,
        defense = 0, attack = 0, operationalRange = 0, populationCost = 0, rateOfFire = 0,
        movement = 0, upgradeTo = 0, hpBonus = 0, bombardEffects = 0, requireSupport = 0,
        createCraters = 0, workerStrength = 0f, airDefense = 0,
    ),
    name = "", civilopediaEntry = "", iconIndex = 0, required = 0,
    requiredResource1 = 0, requiredResource2 = 0, requiredResource3 = 0,
    abilities = 0, aiStrategies = 0, availableTo = 0,
    flags2 = ByteString.of(*ByteArray(8)), type = PrtoDomain.LAND, otherStrategy = 0,
    standardOrders = 0, specialActions = 0, workerActions = 0, airMissions = 0,
    flags4 = ByteString.of(*ByteArray(4)), ignoreMovementCost = ByteString.of(),
    unknown = ByteString.of(*ByteArray(16)), enslaveResults = 0, unknown2 = ByteString.of(0, 0, 0, 0),
    stealthTargetUnitTypes = emptyList(), unknown3 = ByteString.of(*ByteArray(8)), unknown4 = ByteString.of(0, 0, 0, 0),
)

class BldgEntryMappingTest : FunSpec({

    test("toDomain discriminates a spaceship part") {
        val entry = bldgEntry(name = "SS Structural", spaceshipPart = 3)

        val building = listOf(entry).toDomain(emptyList(), emptyList(), emptyList(), emptyList()).single()

        building.shouldBeInstanceOf<SpaceshipPart>()
        (building as SpaceshipPart).partIndex shouldBe 3
    }

    test("toDomain discriminates a Great Wonder") {
        val entry = bldgEntry(name = "Oracle", wonderBit = true)

        val building = listOf(entry).toDomain(emptyList(), emptyList(), emptyList(), emptyList()).single()

        building.shouldBeInstanceOf<GreatWonder>()
    }

    test("toDomain discriminates a Small Wonder") {
        val entry = bldgEntry(name = "Pyramids", smallWonderBit = true)

        val building = listOf(entry).toDomain(emptyList(), emptyList(), emptyList(), emptyList()).single()

        building.shouldBeInstanceOf<SmallWonder>()
    }

    test("toDomain discriminates a plain Improvement") {
        val entry = bldgEntry(name = "Granary")

        val building = listOf(entry).toDomain(emptyList(), emptyList(), emptyList(), emptyList()).single()

        building.shouldBeInstanceOf<Improvement>()
    }

    test("toDomain resolves requiredBuilding against sibling entries") {
        val entries = listOf(bldgEntry(name = "Granary"), bldgEntry(name = "Harbor", requiredBuilding = 0))

        val buildings = entries.toDomain(emptyList(), emptyList(), emptyList(), emptyList())

        buildings[1].requirements.requiredBuilding shouldBe buildings[0]
    }

    test("toDomain resolves requiredGovernment/requiredAdvance against the domain-converted lists") {
        val gov = government()
        val advance = tech("Bronze Working")
        val entry = bldgEntry(requiredGovernment = 0, requiredAdvance = 0)

        val building = listOf(entry).toDomain(listOf(gov), listOf(advance), emptyList(), emptyList()).single()

        building.requirements.requiredGovernment shouldBe gov
        building.requirements.requiredAdvance shouldBe advance
    }

    test("toDomain resolves requiredResources against the wire GOOD list") {
        val g = good()
        val entry = bldgEntry(requiredResource1 = 0)

        val building = listOf(entry).toDomain(emptyList(), emptyList(), listOf(g), emptyList()).single()

        building.requiredResources.requiredResource1 shouldBe g
    }

    test("toDomain resolves GreatWonder's effect fields against sibling entries") {
        val entries = listOf(
            bldgEntry(name = "Granary"),
            bldgEntry(name = "Pyramids", wonderBit = true, gainInEveryCity = 0),
        )

        val buildings = entries.toDomain(emptyList(), emptyList(), emptyList(), emptyList())

        (buildings[1] as GreatWonder).gainInEveryCity shouldBe buildings[0]
    }

    test("toDomain resolves renderedObsoleteBy and unitsProduced for a StandardBuilding") {
        val advance = tech("Automobile")
        val unit = prto()
        val entry = bldgEntry(renderedObsoleteBy = 0, unitProduced = 0)

        val building = listOf(entry).toDomain(emptyList(), listOf(advance), emptyList(), listOf(unit)).single()

        (building as StandardBuilding).renderedObsoleteBy shouldBe advance
        building.unitsProduced?.unitProduced shouldBe unit
    }

    test("toDomain throws on a requiredBuilding cycle") {
        val entries = listOf(bldgEntry(name = "A", requiredBuilding = 1), bldgEntry(name = "B", requiredBuilding = 0))

        shouldThrow<IllegalArgumentException> { entries.toDomain(emptyList(), emptyList(), emptyList(), emptyList()) }
    }

    test("toDomain throws on a GreatWonder effect-field cycle") {
        val entries = listOf(
            bldgEntry(name = "A", wonderBit = true, doublesHappiness = 1),
            bldgEntry(name = "B", wonderBit = true, doublesHappiness = 0),
        )

        shouldThrow<IllegalArgumentException> { entries.toDomain(emptyList(), emptyList(), emptyList(), emptyList()) }
    }

    test("toDomain().toWire() round-trips a plain Improvement") {
        val entry = bldgEntry(name = "Granary")
        val original = listOf(entry)

        val roundTripped = original.toDomain(emptyList(), emptyList(), emptyList(), emptyList())
            .toWire(emptyList(), emptyList(), emptyList(), emptyList())

        roundTripped shouldBe original
    }

    test("toDomain().toWire() round-trips a Great Wonder with a resolved effect field") {
        val entries = listOf(
            bldgEntry(name = "Granary"),
            bldgEntry(name = "Pyramids", wonderBit = true, gainInEveryCity = 0, wonders = 5),
        )

        val roundTripped = entries.toDomain(emptyList(), emptyList(), emptyList(), emptyList())
            .toWire(emptyList(), emptyList(), emptyList(), emptyList())

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() round-trips a SmallWonder with smallWonders field") {
        val entries = listOf(
            bldgEntry(name = "Granary"),
            bldgEntry(name = "Stonehenge", smallWonderBit = true, smallWonders = 7),
        )

        val roundTripped = entries.toDomain(emptyList(), emptyList(), emptyList(), emptyList())
            .toWire(emptyList(), emptyList(), emptyList(), emptyList())

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() round-trips a SpaceshipPart") {
        val entries = listOf(bldgEntry(name = "SS Structural", spaceshipPart = 3))

        val roundTripped = entries.toDomain(emptyList(), emptyList(), emptyList(), emptyList())
            .toWire(emptyList(), emptyList(), emptyList(), emptyList())

        roundTripped shouldBe entries
    }

    test("toWire throws on a requiredBuilding not present in the passed-through roster") {
        val building = listOf(bldgEntry(name = "A")).toDomain(emptyList(), emptyList(), emptyList(), emptyList()).single()
        val outsider = listOf(bldgEntry(name = "Outsider")).toDomain(emptyList(), emptyList(), emptyList(), emptyList()).single()
        building.requirements.requiredBuilding = outsider

        shouldThrow<IllegalArgumentException> {
            listOf(building).toWire(emptyList(), emptyList(), emptyList(), emptyList())
        }
    }

    test("toWire throws on a requiredGovernment not present in the passed governments list") {
        val building = listOf(bldgEntry(name = "A")).toDomain(emptyList(), emptyList(), emptyList(), emptyList()).single()
        building.requirements.requiredGovernment = government()

        shouldThrow<IllegalArgumentException> {
            listOf(building).toWire(emptyList(), emptyList(), emptyList(), emptyList())
        }
    }

    test("toWire throws on a GreatWonder effect field not present in the passed-through roster") {
        val wonder = listOf(bldgEntry(name = "Pyramids", wonderBit = true))
            .toDomain(emptyList(), emptyList(), emptyList(), emptyList()).single() as GreatWonder
        val outsider = listOf(bldgEntry(name = "Outsider")).toDomain(emptyList(), emptyList(), emptyList(), emptyList()).single()
        wonder.gainInEveryCity = outsider

        shouldThrow<IllegalArgumentException> {
            listOf(wonder).toWire(emptyList(), emptyList(), emptyList(), emptyList())
        }
    }
})
