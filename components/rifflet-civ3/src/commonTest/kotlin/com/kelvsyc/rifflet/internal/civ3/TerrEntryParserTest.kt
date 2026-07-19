package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.TerrEntry
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
 * Builds a well-formed TERR item body (length prefix excluded, as with prior sections). Uses
 * numberOfPossibleResources = 5 (not a multiple of 8) to prove the ceiling-division sizing of
 * possibleResources is genuine, not coincidentally correct for byte-aligned counts: 5 resources
 * round up to exactly 1 byte, with 3 padding bits.
 *
 * [includeBooleanFlags] and [includeConquestsTail] model the three confirmed real shapes (see
 * Global Constraints): vanilla writes neither (`includeBooleanFlags = false`), PTW writes only
 * the boolean flags (`includeConquestsTail = false`), Conquests writes both (defaults).
 * [includeConquestsTail] has no effect when [includeBooleanFlags] is `false`, matching the real
 * data's hierarchy — no sample has the Conquests tail without the boolean flags.
 */
private fun terrItemBinary(
    numberOfPossibleResources: Int = 5,
    possibleResources: ByteString = ByteString.of(0b00010101.toByte()),
    name: String = "Plains",
    civilopediaEntry: String = "Plains",
    irrigationBonus: Int = 1,
    miningBonus: Int = 0,
    roadBonus: Int = 1,
    defenseBonus: Int = 0,
    movementCost: Int = 1,
    food: Int = 1,
    shields: Int = 1,
    commerce: Int = 0,
    workerJobAllowed: Int = -1,
    pollutionEffect: Int = -1,
    allowCities: Byte = 1,
    allowColonies: Byte = 1,
    impassable: Byte = 0,
    impassableByWheeled: Byte = 0,
    allowAirfields: Byte = 1,
    allowForts: Byte = 1,
    allowOutposts: Byte = 1,
    allowRadarTowers: Byte = 1,
    unknown: ByteString = ByteString.of(*ByteArray(4)),
    landmarkEnabled: Byte = 0,
    landmarkFood: Int = 0,
    landmarkShields: Int = 0,
    landmarkCommerce: Int = 0,
    landmarkIrrigationBonus: Int = 0,
    landmarkMiningBonus: Int = 0,
    landmarkRoadBonus: Int = 0,
    landmarkMovementBonus: Int = 0,
    landmarkDefensiveBonus: Int = 0,
    landmarkName: String = "",
    landmarkCivilopediaEntry: String = "",
    unknown2: ByteString = ByteString.of(*ByteArray(4)),
    terrainFlags: Int = 0,
    diseaseStrength: Int = 0,
    includeBooleanFlags: Boolean = true,
    includeConquestsTail: Boolean = true,
): Buffer = Buffer().apply {
    writeIntLe(numberOfPossibleResources)
    write(possibleResources)
    writePaddedField(name, 32)
    writePaddedField(civilopediaEntry, 32)
    writeIntLe(irrigationBonus)
    writeIntLe(miningBonus)
    writeIntLe(roadBonus)
    writeIntLe(defenseBonus)
    writeIntLe(movementCost)
    writeIntLe(food)
    writeIntLe(shields)
    writeIntLe(commerce)
    writeIntLe(workerJobAllowed)
    writeIntLe(pollutionEffect)
    writeByte(allowCities.toInt())
    writeByte(allowColonies.toInt())
    if (includeBooleanFlags) {
        writeByte(impassable.toInt())
        writeByte(impassableByWheeled.toInt())
        writeByte(allowAirfields.toInt())
        writeByte(allowForts.toInt())
        writeByte(allowOutposts.toInt())
        writeByte(allowRadarTowers.toInt())
        if (includeConquestsTail) {
            write(unknown)
            writeByte(landmarkEnabled.toInt())
            writeIntLe(landmarkFood)
            writeIntLe(landmarkShields)
            writeIntLe(landmarkCommerce)
            writeIntLe(landmarkIrrigationBonus)
            writeIntLe(landmarkMiningBonus)
            writeIntLe(landmarkRoadBonus)
            writeIntLe(landmarkMovementBonus)
            writeIntLe(landmarkDefensiveBonus)
            writePaddedField(landmarkName, 32)
            writePaddedField(landmarkCivilopediaEntry, 32)
            write(unknown2)
            writeIntLe(terrainFlags)
            writeIntLe(diseaseStrength)
        }
    }
}

class TerrEntryParserTest : FunSpec({

    test("well-formed item with a non-byte-aligned resource count is parsed into all fields") {
        val entry = TerrEntryParser.parse(terrItemBinary())
        entry shouldBe TerrEntry(
            numberOfPossibleResources = 5,
            possibleResources = ByteString.of(0b00010101.toByte()),
            name = "Plains",
            civilopediaEntry = "Plains",
            irrigationBonus = 1,
            miningBonus = 0,
            roadBonus = 1,
            defenseBonus = 0,
            movementCost = 1,
            food = 1,
            shields = 1,
            commerce = 0,
            workerJobAllowed = -1,
            pollutionEffect = -1,
            allowCities = 1,
            allowColonies = 1,
            impassable = 0,
            impassableByWheeled = 0,
            allowAirfields = 1,
            allowForts = 1,
            allowOutposts = 1,
            allowRadarTowers = 1,
            unknown = ByteString.of(*ByteArray(4)),
            landmarkEnabled = 0,
            landmarkFood = 0,
            landmarkShields = 0,
            landmarkCommerce = 0,
            landmarkIrrigationBonus = 0,
            landmarkMiningBonus = 0,
            landmarkRoadBonus = 0,
            landmarkMovementBonus = 0,
            landmarkDefensiveBonus = 0,
            landmarkName = "",
            landmarkCivilopediaEntry = "",
            unknown2 = ByteString.of(*ByteArray(4)),
            terrainFlags = 0,
            diseaseStrength = 0,
        )
    }

    test("item missing all 21 trailing fields defaults them (vanilla shape)") {
        val entry = TerrEntryParser.parse(terrItemBinary(includeBooleanFlags = false))
        entry.impassable shouldBe 0
        entry.impassableByWheeled shouldBe 0
        entry.allowAirfields shouldBe 0
        entry.allowForts shouldBe 0
        entry.allowOutposts shouldBe 0
        entry.allowRadarTowers shouldBe 0
        entry.unknown shouldBe ByteString.of(*ByteArray(4))
        entry.landmarkEnabled shouldBe 0
        entry.landmarkFood shouldBe 0
        entry.landmarkShields shouldBe 0
        entry.landmarkCommerce shouldBe 0
        entry.landmarkIrrigationBonus shouldBe 0
        entry.landmarkMiningBonus shouldBe 0
        entry.landmarkRoadBonus shouldBe 0
        entry.landmarkMovementBonus shouldBe 0
        entry.landmarkDefensiveBonus shouldBe 0
        entry.landmarkName shouldBe ""
        entry.landmarkCivilopediaEntry shouldBe ""
        entry.unknown2 shouldBe ByteString.of(*ByteArray(4))
        entry.terrainFlags shouldBe 0
        entry.diseaseStrength shouldBe 0
    }

    test("item with boolean flags but missing the Conquests tail defaults the rest (PTW shape)") {
        val entry = TerrEntryParser.parse(
            terrItemBinary(
                impassable = 1,
                impassableByWheeled = 1,
                allowAirfields = 0,
                allowForts = 0,
                allowOutposts = 0,
                allowRadarTowers = 0,
                includeConquestsTail = false,
            ),
        )
        entry.impassable shouldBe 1
        entry.impassableByWheeled shouldBe 1
        entry.allowAirfields shouldBe 0
        entry.allowForts shouldBe 0
        entry.allowOutposts shouldBe 0
        entry.allowRadarTowers shouldBe 0
        entry.unknown shouldBe ByteString.of(*ByteArray(4))
        entry.landmarkEnabled shouldBe 0
        entry.landmarkFood shouldBe 0
        entry.landmarkName shouldBe ""
        entry.landmarkCivilopediaEntry shouldBe ""
        entry.unknown2 shouldBe ByteString.of(*ByteArray(4))
        entry.terrainFlags shouldBe 0
        entry.diseaseStrength shouldBe 0
    }

    test("TerrEntry rejects a possibleResources whose size doesn't match the ceiling-divided count") {
        shouldThrow<IllegalArgumentException> {
            wellFormedTerrEntry(
                numberOfPossibleResources = 5,
                possibleResources = ByteString.of(0, 0),
            )
        }
    }

    test("TerrEntry rejects an unknown field that is not exactly 4 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedTerrEntry(unknown = ByteString.of(0, 0))
        }
    }

    test("TerrEntry rejects an unknown2 field that is not exactly 4 bytes") {
        shouldThrow<IllegalArgumentException> {
            wellFormedTerrEntry(unknown2 = ByteString.of(0, 0))
        }
    }

    test("a numberOfPossibleResources whose ceiling-divided byte requirement exceeds remaining data throws RiffletParseException") {
        val buffer = Buffer().apply {
            writeIntLe(1_000_000) // numberOfPossibleResources -> 125,000 bytes required
            write(ByteArray(10))
        }
        shouldThrow<RiffletParseException> { TerrEntryParser.parse(buffer) }
    }

    test("a numberOfPossibleResources near Int.MAX_VALUE does not overflow the ceiling-division arithmetic") {
        val buffer = Buffer().apply {
            writeIntLe(Int.MAX_VALUE - 3) // +7 would overflow Int arithmetic if not computed as Long
            write(ByteArray(10))
        }
        shouldThrow<RiffletParseException> { TerrEntryParser.parse(buffer) }
    }
})

/** Builds a well-formed [TerrEntry] with all-zero/empty values, for domain-invariant tests that
 * only care about overriding one field. */
private fun wellFormedTerrEntry(
    numberOfPossibleResources: Int = 0,
    possibleResources: ByteString = ByteString.of(),
    unknown: ByteString = ByteString.of(*ByteArray(4)),
    unknown2: ByteString = ByteString.of(*ByteArray(4)),
): TerrEntry = TerrEntry(
    numberOfPossibleResources = numberOfPossibleResources,
    possibleResources = possibleResources,
    name = "", civilopediaEntry = "",
    irrigationBonus = 0, miningBonus = 0, roadBonus = 0, defenseBonus = 0, movementCost = 0,
    food = 0, shields = 0, commerce = 0, workerJobAllowed = 0, pollutionEffect = 0,
    allowCities = 0, allowColonies = 0, impassable = 0, impassableByWheeled = 0,
    allowAirfields = 0, allowForts = 0, allowOutposts = 0, allowRadarTowers = 0,
    unknown = unknown,
    landmarkEnabled = 0, landmarkFood = 0, landmarkShields = 0, landmarkCommerce = 0,
    landmarkIrrigationBonus = 0, landmarkMiningBonus = 0, landmarkRoadBonus = 0,
    landmarkMovementBonus = 0, landmarkDefensiveBonus = 0,
    landmarkName = "", landmarkCivilopediaEntry = "",
    unknown2 = unknown2,
    terrainFlags = 0, diseaseStrength = 0,
)
