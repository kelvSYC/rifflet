package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.PrtoEntry
import com.kelvsyc.rifflet.core.RiffletParseException
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
 * Builds a well-formed PRTO item body (length prefix excluded, as with prior sections). Uses a
 * small (2-element) `stealthTargetUnitTypes` list to prove the dynamic read is genuine, not
 * hardcoded.
 *
 * [includeFlags3Onward] controls whether everything from `flags3` through the end is written —
 * `false` produces the real vanilla shape confirmed against real files: the item ends
 * immediately after `hpBonus`.
 *
 * [includeUnknownOnward], nested inside [includeFlags3Onward], controls whether everything from
 * `unknown` through the end is written — `false` (with the outer flag `true`) produces the real
 * PTW shape confirmed against real files (`VER#` header `minor=18`): the item ends immediately
 * after `requireSupport`.
 */
private fun prtoItemBinary(
    zoneOfControl: Int = 0,
    name: String = "Warrior",
    civilopediaEntry: String = "Warrior",
    bombardStrength: Int = 0,
    bombardRange: Int = 0,
    capacity: Int = 0,
    shieldCost: Int = 10,
    defense: Int = 1,
    iconIndex: Int = 5,
    attack: Int = 1,
    operationalRange: Int = 0,
    populationCost: Int = 0,
    rateOfFire: Int = 1,
    movement: Int = 1,
    required: Int = 0,
    upgradeTo: Int = 3,
    requiredResource1: Int = -1,
    requiredResource2: Int = -1,
    requiredResource3: Int = -1,
    abilities: Int = 0,
    aiStrategies: Int = 0,
    availableTo: Int = -1,
    flags2: ByteString = ByteString.of(*ByteArray(8)),
    type: Int = 0,
    otherStrategy: Int = -1,
    hpBonus: Int = 0,
    standardOrders: Int = 0,
    specialActions: Int = 0,
    workerActions: Int = 0,
    airMissions: Int = 0,
    flags4: ByteString = ByteString.of(*ByteArray(4)),
    bombardEffects: Int = 0,
    ignoreMovementCost: ByteString = ByteString.of(*ByteArray(14)),
    requireSupport: Int = 0,
    unknown: ByteString = ByteString.of(*ByteArray(16)),
    enslaveResults: Int = 0,
    unknown2: ByteString = ByteString.of(*ByteArray(4)),
    stealthTargetUnitTypes: List<Int> = listOf(4, 9),
    unknown3: ByteString = ByteString.of(*ByteArray(8)),
    createCraters: Byte = 0,
    workerStrength: Float = 1.5f,
    unknown4: ByteString = ByteString.of(*ByteArray(4)),
    airDefense: Int = 0,
    includeFlags3Onward: Boolean = true,
    includeUnknownOnward: Boolean = true,
): Buffer = Buffer().apply {
    writeIntLe(zoneOfControl)
    writePaddedField(name, 32)
    writePaddedField(civilopediaEntry, 32)
    writeIntLe(bombardStrength)
    writeIntLe(bombardRange)
    writeIntLe(capacity)
    writeIntLe(shieldCost)
    writeIntLe(defense)
    writeIntLe(iconIndex)
    writeIntLe(attack)
    writeIntLe(operationalRange)
    writeIntLe(populationCost)
    writeIntLe(rateOfFire)
    writeIntLe(movement)
    writeIntLe(required)
    writeIntLe(upgradeTo)
    writeIntLe(requiredResource1)
    writeIntLe(requiredResource2)
    writeIntLe(requiredResource3)
    writeIntLe(abilities)
    writeIntLe(aiStrategies)
    writeIntLe(availableTo)
    write(flags2)
    writeIntLe(type)
    writeIntLe(otherStrategy)
    writeIntLe(hpBonus)
    if (includeFlags3Onward) {
        writeIntLe(standardOrders)
        writeIntLe(specialActions)
        writeIntLe(workerActions)
        writeIntLe(airMissions)
        write(flags4)
        writeIntLe(bombardEffects)
        write(ignoreMovementCost)
        writeIntLe(requireSupport)
        if (includeUnknownOnward) {
            write(unknown)
            writeIntLe(enslaveResults)
            write(unknown2)
            writeIntLe(stealthTargetUnitTypes.size)
            stealthTargetUnitTypes.forEach { writeIntLe(it) }
            write(unknown3)
            writeByte(createCraters.toInt())
            writeIntLe(workerStrength.toRawBits())
            write(unknown4)
            writeIntLe(airDefense)
        }
    }
}

class PrtoEntryParserTest : FunSpec({

    test("well-formed item is parsed into all fields, including the dynamic stealth-target list") {
        val entry = PrtoEntryParser.parse(prtoItemBinary(), terrCount = 14)
        entry shouldBe PrtoEntry(
            zoneOfControl = 0,
            name = "Warrior",
            civilopediaEntry = "Warrior",
            bombardStrength = 0,
            bombardRange = 0,
            capacity = 0,
            shieldCost = 10,
            defense = 1,
            iconIndex = 5,
            attack = 1,
            operationalRange = 0,
            populationCost = 0,
            rateOfFire = 1,
            movement = 1,
            required = 0,
            upgradeTo = 3,
            requiredResource1 = -1,
            requiredResource2 = -1,
            requiredResource3 = -1,
            abilities = 0,
            aiStrategies = 0,
            availableTo = -1,
            flags2 = ByteString.of(*ByteArray(8)),
            type = 0,
            otherStrategy = -1,
            hpBonus = 0,
            standardOrders = 0,
            specialActions = 0,
            workerActions = 0,
            airMissions = 0,
            flags4 = ByteString.of(*ByteArray(4)),
            bombardEffects = 0,
            ignoreMovementCost = ByteString.of(*ByteArray(14)),
            requireSupport = 0,
            unknown = ByteString.of(*ByteArray(16)),
            enslaveResults = 0,
            unknown2 = ByteString.of(*ByteArray(4)),
            stealthTargetUnitTypes = listOf(4, 9),
            unknown3 = ByteString.of(*ByteArray(8)),
            createCraters = 0,
            workerStrength = 1.5f,
            unknown4 = ByteString.of(*ByteArray(4)),
            airDefense = 0,
        )
    }

    test("well-formed item with terrCount = 12 (confirmed real vanilla/PTW terrain count) parses a 12-byte ignoreMovementCost") {
        val entry = PrtoEntryParser.parse(
            prtoItemBinary(ignoreMovementCost = ByteString.of(*ByteArray(12))),
            terrCount = 12,
        )
        entry.ignoreMovementCost shouldBe ByteString.of(*ByteArray(12))
    }

    test("vanilla-shape item (ends right after hpBonus, confirmed real vanilla shape) defaults flags3 onward") {
        val entry = PrtoEntryParser.parse(
            prtoItemBinary(includeFlags3Onward = false),
            terrCount = 12,
        )
        entry.standardOrders shouldBe 0
        entry.specialActions shouldBe 0
        entry.workerActions shouldBe 0
        entry.airMissions shouldBe 0
        entry.flags4 shouldBe ByteString.of(*ByteArray(4))
        entry.ignoreMovementCost shouldBe ByteString.of(*ByteArray(12))
        entry.requireSupport shouldBe 0
        entry.unknown shouldBe ByteString.of(*ByteArray(16))
    }

    test("PTW-shape item (ends right after requireSupport, confirmed real minor=18 shape) defaults unknown onward") {
        val entry = PrtoEntryParser.parse(
            prtoItemBinary(
                ignoreMovementCost = ByteString.of(*ByteArray(12)),
                requireSupport = 1,
                includeUnknownOnward = false,
            ),
            terrCount = 12,
        )
        entry.ignoreMovementCost shouldBe ByteString.of(*ByteArray(12))
        entry.requireSupport shouldBe 1
        entry.unknown shouldBe ByteString.of(*ByteArray(16))
        entry.stealthTargetUnitTypes shouldBe emptyList()
    }

    test("PrtoEntry rejects a flags2 field that is not exactly 8 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedPrtoEntry(flags2 = ByteString.of(0, 0, 0))
        }
    }

    test("PrtoEntry rejects a flags4 field that is not exactly 4 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedPrtoEntry(flags4 = ByteString.of(0, 0))
        }
    }

    test("PrtoEntry rejects an unknown field that is not exactly 16 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedPrtoEntry(unknown = ByteString.of(0, 0, 0))
        }
    }

    test("PrtoEntry rejects an unknown2 field that is not exactly 4 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedPrtoEntry(unknown2 = ByteString.of(0, 0))
        }
    }

    test("PrtoEntry rejects an unknown3 field that is not exactly 8 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedPrtoEntry(unknown3 = ByteString.of(0, 0, 0))
        }
    }

    test("PrtoEntry rejects an unknown4 field that is not exactly 4 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedPrtoEntry(unknown4 = ByteString.of(0, 0))
        }
    }

    test("an implausibly large terrCount throws RiffletParseException before attempting to allocate") {
        shouldThrow<RiffletParseException> {
            PrtoEntryParser.parse(prtoItemBinary(), terrCount = Int.MAX_VALUE)
        }
    }

    test("an implausibly large numberOfStealthTargets throws RiffletParseException before attempting to allocate") {
        val buffer = Buffer().apply {
            // zoneOfControl through unknown2, all-zero/valid (with terrCount=14-sized ignoreMovementCost)
            write(ByteArray(230))
            writeIntLe(Int.MAX_VALUE) // numberOfStealthTargets
        }
        shouldThrow<RiffletParseException> { PrtoEntryParser.parse(buffer, terrCount = 14) }
    }
})

/** Builds a well-formed [PrtoEntry] with all-zero/empty values, for domain-invariant tests that
 * only care about overriding one `ByteString` field. */
private fun wellFormedPrtoEntry(
    flags2: ByteString = ByteString.of(*ByteArray(8)),
    flags4: ByteString = ByteString.of(*ByteArray(4)),
    unknown: ByteString = ByteString.of(*ByteArray(16)),
    unknown2: ByteString = ByteString.of(*ByteArray(4)),
    unknown3: ByteString = ByteString.of(*ByteArray(8)),
    unknown4: ByteString = ByteString.of(*ByteArray(4)),
): PrtoEntry = PrtoEntry(
    zoneOfControl = 0, name = "", civilopediaEntry = "",
    bombardStrength = 0, bombardRange = 0, capacity = 0, shieldCost = 0, defense = 0,
    iconIndex = 0, attack = 0, operationalRange = 0, populationCost = 0, rateOfFire = 0,
    movement = 0, required = 0, upgradeTo = 0, requiredResource1 = 0, requiredResource2 = 0,
    requiredResource3 = 0,
    abilities = 0,
    aiStrategies = 0,
    availableTo = 0,
    flags2 = flags2,
    type = 0, otherStrategy = 0, hpBonus = 0,
    standardOrders = 0,
    specialActions = 0,
    workerActions = 0,
    airMissions = 0,
    flags4 = flags4,
    bombardEffects = 0,
    ignoreMovementCost = ByteString.of(*ByteArray(14)),
    requireSupport = 0,
    unknown = unknown,
    enslaveResults = 0,
    unknown2 = unknown2,
    stealthTargetUnitTypes = emptyList(),
    unknown3 = unknown3,
    createCraters = 0,
    workerStrength = 0f,
    unknown4 = unknown4,
    airDefense = 0,
)
