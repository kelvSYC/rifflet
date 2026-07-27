package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.BldgCombatValues
import com.kelvsyc.rifflet.civ3.BldgEntry
import com.kelvsyc.rifflet.civ3.BldgHappiness
import com.kelvsyc.rifflet.civ3.BldgRequiredResources
import com.kelvsyc.rifflet.civ3.BldgRequirements
import com.kelvsyc.rifflet.civ3.BldgUnitsProduced
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.Buffer
import okio.ByteString

/** Writes [text] into [fieldSize] bytes, null-padding the remainder. */
private fun Buffer.writePaddedField(text: String, fieldSize: Int) {
    val start = size
    writeString(text, Charsets.US_ASCII)
    write(ByteArray((fieldSize - (size - start)).toInt()))
}

/**
 * Builds a well-formed BLDG item body (length prefix excluded, as with prior sections). BLDG
 * has no dynamic-length regions, so every field is given a distinct non-default value to prove
 * the read order is genuine, not coincidentally correct.
 */
private fun bldgItemBinary(
    description: String = "Provides defense",
    name: String = "City Walls",
    civilopediaEntry: String = "City Walls",
    doublesHappiness: Int = 0,
    gainInEveryCity: Int = 0,
    gainInEveryCityOnContinent: Int = 0,
    requiredBuilding: Int = -1,
    cost: Int = 60,
    culture: Int = 1,
    bombardDefense: Int = 0,
    navalBombardDefense: Int = 0,
    defenseBonus: Int = 100,
    navalDefenseBonus: Int = 0,
    maintenanceCost: Int = 0,
    contentFacesAllCities: Int = 0,
    contentFaces: Int = 0,
    unhappyFacesAllCities: Int = 0,
    unhappyFaces: Int = 0,
    numberOfRequiredBuildings: Int = 0,
    airPower: Int = 0,
    navalPower: Int = 0,
    pollution: Int = 0,
    production: Int = 0,
    requiredGovernment: Int = -1,
    spaceshipPart: Int = 0,
    requiredAdvance: Int = 5,
    renderedObsoleteBy: Int = -1,
    requiredResource1: Int = -1,
    requiredResource2: Int = -1,
    flags: ByteString = ByteString.of(*ByteArray(16)),
    numberOfArmiesRequired: Int = 0,
    flavors: Int = 0,
    unknown: ByteString = ByteString.of(*ByteArray(4)),
    unitProduced: Int = -1,
    unitFrequency: Int = 0,
    includeTrailingFields: Boolean = true,
): Buffer = Buffer().apply {
    writePaddedField(description, 64)
    writePaddedField(name, 32)
    writePaddedField(civilopediaEntry, 32)
    writeIntLe(doublesHappiness)
    writeIntLe(gainInEveryCity)
    writeIntLe(gainInEveryCityOnContinent)
    writeIntLe(requiredBuilding)
    writeIntLe(cost)
    writeIntLe(culture)
    writeIntLe(bombardDefense)
    writeIntLe(navalBombardDefense)
    writeIntLe(defenseBonus)
    writeIntLe(navalDefenseBonus)
    writeIntLe(maintenanceCost)
    writeIntLe(contentFacesAllCities)
    writeIntLe(contentFaces)
    writeIntLe(unhappyFacesAllCities)
    writeIntLe(unhappyFaces)
    writeIntLe(numberOfRequiredBuildings)
    writeIntLe(airPower)
    writeIntLe(navalPower)
    writeIntLe(pollution)
    writeIntLe(production)
    writeIntLe(requiredGovernment)
    writeIntLe(spaceshipPart)
    writeIntLe(requiredAdvance)
    writeIntLe(renderedObsoleteBy)
    writeIntLe(requiredResource1)
    writeIntLe(requiredResource2)
    write(flags)
    writeIntLe(numberOfArmiesRequired)
    if (includeTrailingFields) {
        writeIntLe(flavors)
        write(unknown)
        writeIntLe(unitProduced)
        writeIntLe(unitFrequency)
    }
}

class BldgEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields") {
        val entry = BldgEntryParser.parse(bldgItemBinary())
        entry shouldBe BldgEntry(
            description = "Provides defense",
            name = "City Walls",
            civilopediaEntry = "City Walls",
            doublesHappiness = 0,
            gainInEveryCity = 0,
            gainInEveryCityOnContinent = 0,
            requirements = BldgRequirements(requiredBuilding = -1, requiredGovernment = -1, requiredAdvance = 5),
            cost = 60,
            culture = 1,
            combatValues = BldgCombatValues(
                bombardDefense = 0,
                navalBombardDefense = 0,
                defenseBonus = 100,
                airPower = 0,
                navalPower = 0,
            ),
            navalDefenseBonus = 0,
            maintenanceCost = 0,
            happiness = BldgHappiness(
                contentFacesAllCities = 0,
                contentFaces = 0,
                unhappyFacesAllCities = 0,
                unhappyFaces = 0,
            ),
            numberOfRequiredBuildings = 0,
            pollution = 0,
            production = 0,
            spaceshipPart = 0,
            renderedObsoleteBy = -1,
            requiredResources = BldgRequiredResources(requiredResource1 = -1, requiredResource2 = -1),
            flags = ByteString.of(*ByteArray(16)),
            numberOfArmiesRequired = 0,
            flavors = 0,
            unknown = ByteString.of(*ByteArray(4)),
            unitsProduced = BldgUnitsProduced(unitProduced = -1, unitFrequency = 0),
        )
    }

    test("vanilla/PTW-length item (252 bytes, trailing fields absent) leaves unitsProduced null") {
        val entry = BldgEntryParser.parse(bldgItemBinary(includeTrailingFields = false))
        entry.flavors shouldBe 0
        entry.unknown shouldBe ByteString.of(*ByteArray(4))
        entry.unitsProduced shouldBe null
    }

    test("BldgEntry rejects a flags field that is not exactly 16 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedBldgEntry(flags = ByteString.of(0, 0, 0))
        }
    }

    test("BldgEntry rejects an unknown field that is not exactly 4 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedBldgEntry(unknown = ByteString.of(0, 0))
        }
    }
})

/** Builds a well-formed [BldgEntry] with all-zero/empty values, for domain-invariant tests that
 * only care about overriding one `ByteString` field. */
private fun wellFormedBldgEntry(
    flags: ByteString = ByteString.of(*ByteArray(16)),
    unknown: ByteString = ByteString.of(*ByteArray(4)),
): BldgEntry = BldgEntry(
    description = "", name = "", civilopediaEntry = "",
    doublesHappiness = 0, gainInEveryCity = 0, gainInEveryCityOnContinent = 0,
    requirements = BldgRequirements(requiredBuilding = 0, requiredGovernment = 0, requiredAdvance = 0),
    cost = 0, culture = 0,
    combatValues = BldgCombatValues(
        bombardDefense = 0, navalBombardDefense = 0, defenseBonus = 0, airPower = 0, navalPower = 0,
    ),
    navalDefenseBonus = 0, maintenanceCost = 0,
    happiness = BldgHappiness(
        contentFacesAllCities = 0, contentFaces = 0, unhappyFacesAllCities = 0, unhappyFaces = 0,
    ),
    numberOfRequiredBuildings = 0,
    pollution = 0, production = 0, spaceshipPart = 0, renderedObsoleteBy = 0,
    requiredResources = BldgRequiredResources(requiredResource1 = 0, requiredResource2 = 0),
    flags = flags,
    numberOfArmiesRequired = 0, flavors = 0,
    unknown = unknown,
    unitsProduced = BldgUnitsProduced(unitProduced = 0, unitFrequency = 0),
)
