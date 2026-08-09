package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.Gender
import com.kelvsyc.rifflet.civ3.GoodResourceType
import com.kelvsyc.rifflet.civ3.PrtoDomain
import com.kelvsyc.rifflet.civ3.PrtoEntry
import com.kelvsyc.rifflet.civ3.PrtoUnitStatistics as WirePrtoUnitStatistics
import com.kelvsyc.rifflet.civ3.RaceCultureGroup
import com.kelvsyc.rifflet.civ3.RaceLeader
import com.kelvsyc.rifflet.civ3.TerrAllowances
import com.kelvsyc.rifflet.civ3.TerrTerraformBonuses
import com.kelvsyc.rifflet.civ3.TerrTileValues
import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.domain.Terrain
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun prtoEntry(
    name: String = "",
    type: PrtoDomain = PrtoDomain.LAND,
    upgradeTo: Int = -1,
    otherStrategy: Int = -1,
    aiStrategies: Int = 0,
    abilities: Int = 0,
    required: Int = -1,
    requiredResource1: Int = -1,
    requiredResource2: Int = -1,
    requiredResource3: Int = -1,
    availableTo: Int = 0,
    enslaveResults: Int = -1,
    ignoreMovementCostByte: Int = 0,
    stealthTargetUnitTypes: List<Int> = emptyList(),
    standardOrders: Int = 0,
    specialActions: Int = 0,
    workerActions: Int = 0,
    airMissions: Int = 0,
    flags4: ByteString = ByteString.of(0, 0, 1, 0), // default: bit 16 always set for CONQUESTS-era
    ignoreMovementCost: ByteString? = null,
): PrtoEntry = PrtoEntry(
    unitStatistics = WirePrtoUnitStatistics(
        zoneOfControl = 0, bombardStrength = 0, bombardRange = 0, capacity = 0, shieldCost = 0,
        defense = 0, attack = 0, operationalRange = 0, populationCost = 0, rateOfFire = 0,
        movement = 0, upgradeTo = upgradeTo, hpBonus = 0, bombardEffects = 0, requireSupport = 0,
        createCraters = 0, workerStrength = 0f, airDefense = 0,
    ),
    name = name,
    civilopediaEntry = "",
    iconIndex = 0,
    required = required,
    requiredResource1 = requiredResource1,
    requiredResource2 = requiredResource2,
    requiredResource3 = requiredResource3,
    abilities = abilities,
    aiStrategies = aiStrategies,
    availableTo = availableTo,
    flags2 = ByteString.of(*ByteArray(8)),
    type = type,
    otherStrategy = otherStrategy,
    standardOrders = standardOrders,
    specialActions = specialActions,
    workerActions = workerActions,
    airMissions = airMissions,
    flags4 = flags4,
    ignoreMovementCost = ignoreMovementCost ?: ByteString.of(ignoreMovementCostByte.toByte()),
    unknown = ByteString.of(*ByteArray(16)),
    enslaveResults = enslaveResults,
    unknown2 = ByteString.of(*ByteArray(4)),
    stealthTargetUnitTypes = stealthTargetUnitTypes,
    unknown3 = ByteString.of(*ByteArray(8)),
    unknown4 = ByteString.of(*ByteArray(4)),
)

private fun resource(): Resource = Resource(name = "Wine", type = GoodResourceType.LUXURY)

private fun tech(name: String = ""): Tech = Tech(name = name, civilopediaEntry = "", cost = 0, era = 0, advanceIcon = 0, x = 0, y = 0)

private fun race(name: String = ""): Race = Race(
    name = name, civilopediaEntry = "", adjective = "", noun = "",
    leader = RaceLeader(name = "", title = "", gender = Gender.MALE),
    cultureGroup = RaceCultureGroup.NONE, civilizationGender = Gender.MALE,
)

private fun validPrto(name: String = "Warrior"): Prto = Prto(
    name = name, civilopediaEntry = "", iconIndex = 0, type = PrtoDomain.LAND,
)

private fun ByteString.toIntLeForTest(offset: Int): Int =
    (this[offset].toInt() and 0xFF) or
        ((this[offset + 1].toInt() and 0xFF) shl 8) or
        ((this[offset + 2].toInt() and 0xFF) shl 16) or
        ((this[offset + 3].toInt() and 0xFF) shl 24)

class PrtoEntryMappingTest : FunSpec({

    test("toDomain maps a plain single entry with no duplicates straight across") {
        val entry = prtoEntry(name = "Warrior", aiStrategies = 1 shl 0)

        val prtos = listOf(entry).toDomain(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList())

        prtos.size shouldBe 1
        prtos.single().name shouldBe "Warrior"
        prtos.single().aiStrategies shouldBe 1 shl 0
    }

    test("toDomain merges a canonical and its duplicate into one Prto with a multi-bit aiStrategies") {
        val canonical = prtoEntry(name = "Rifleman", aiStrategies = 1 shl 0)
        val duplicate = prtoEntry(name = "Rifleman", aiStrategies = 1 shl 1, otherStrategy = 0)

        val prtos = listOf(canonical, duplicate).toDomain(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList())

        prtos.size shouldBe 1
        prtos.single().aiStrategies shouldBe ((1 shl 0) or (1 shl 1))
    }

    test("toDomain merges 3 duplicates sharing one canonical") {
        val canonical = prtoEntry(name = "X", aiStrategies = 1 shl 0)
        val dup1 = prtoEntry(name = "X", aiStrategies = 1 shl 1, otherStrategy = 0)
        val dup2 = prtoEntry(name = "X", aiStrategies = 1 shl 2, otherStrategy = 0)
        val dup3 = prtoEntry(name = "X", aiStrategies = 1 shl 3, otherStrategy = 0)

        val prtos = listOf(canonical, dup1, dup2, dup3).toDomain(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList())

        prtos.size shouldBe 1
        prtos.single().aiStrategies shouldBe ((1 shl 0) or (1 shl 1) or (1 shl 2) or (1 shl 3))
    }

    test("toDomain resolves upgradeTo, enslaveResults, and stealthTargetUnitTypes against sibling entries") {
        val entries = listOf(
            prtoEntry(name = "Warrior"),
            prtoEntry(name = "Musketeers", upgradeTo = 0, enslaveResults = 0, stealthTargetUnitTypes = listOf(0)),
        )

        val prtos = entries.toDomain(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList())

        prtos[1].unitStatistics.upgradeTo shouldBe prtos[0]
        prtos[1].enslaveResults shouldBe prtos[0]
        prtos[1].stealthTargetUnitTypes shouldBe mutableSetOf(prtos[0])
    }

    test("toDomain allows a self-referencing enslaveResults") {
        val entry = prtoEntry(name = "Man-O-War", enslaveResults = 0)

        val prtos = listOf(entry).toDomain(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList())

        prtos.single().enslaveResults shouldBe prtos.single()
    }

    test("toDomain resolves required, requiredResources, availableTo, ignoreMovementCost") {
        val advance = tech("Gunpowder")
        val r = resource()
        val raceVal = race("Rome")
        val t = Terrain(
            name = "Desert",
            allowances = TerrAllowances(
                allowCities = 0, allowColonies = 0, impassable = 0, impassableByWheeled = 0,
                allowAirfields = 0, allowForts = 0, allowOutposts = 0, allowRadarTowers = 0,
            ),
        )
        val entry = prtoEntry(required = 0, requiredResource1 = 0, availableTo = 1 shl 0, ignoreMovementCostByte = 1)

        val prtos = listOf(entry).toDomain(Civ3FormatEra.CONQUESTS, listOf(advance), listOf(r), listOf(raceVal), listOf(t))

        prtos.single().required shouldBe advance
        prtos.single().requiredResources shouldBe mutableListOf(r, null, null)
        prtos.single().availableTo shouldBe mutableSetOf(raceVal)
        prtos.single().ignoreMovementCost shouldBe mutableSetOf(t)
    }

    test("toDomain reads unified era-resolved actions correctly for Conquests") {
        val entry = prtoEntry(standardOrders = 1 shl 0, specialActions = 1 shl 0, workerActions = 1 shl 0, airMissions = 1 shl 0)

        val prto = listOf(entry).toDomain(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList()).single()

        prto.skipTurn shouldBe true
        prto.load shouldBe true
        prto.buildColony shouldBe true
        prto.bombing shouldBe true
    }

    test("toDomain reads the 11 direct (no-VANILLA-counterpart) actions correctly") {
        val entry = prtoEntry(
            standardOrders = (1 shl 5) or (1 shl 6),
            specialActions = (1 shl 9) or (1 shl 16) or (1 shl 18) or (1 shl 20) or (1 shl 21),
            workerActions = (1 shl 13) or (1 shl 14) or (1 shl 15) or (1 shl 16),
        )

        val prto = listOf(entry).toDomain(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList()).single()

        prto.explore shouldBe true
        prto.sentry shouldBe true
        prto.capture shouldBe true
        prto.stealthAttack shouldBe true
        prto.enslave shouldBe true
        prto.sacrifice shouldBe true
        prto.startsScienceAge shouldBe true
        prto.buildAirfield shouldBe true
        prto.buildRadarTower shouldBe true
        prto.buildOutpost shouldBe true
        prto.buildBarricade shouldBe true
    }

    test("toDomain throws on an upgradeTo cycle") {
        val entries = listOf(prtoEntry(name = "A", upgradeTo = 1), prtoEntry(name = "B", upgradeTo = 0))

        shouldThrow<IllegalArgumentException> {
            entries.toDomain(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList())
        }
    }

    test("toDomain throws on an upgradeTo self-loop") {
        val entries = listOf(prtoEntry(name = "A", upgradeTo = 0))

        shouldThrow<IllegalArgumentException> {
            entries.toDomain(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList())
        }
    }

    test("toDomain().toWire() round-trips a plain single entry") {
        val entries = listOf(prtoEntry(name = "Warrior", aiStrategies = 1 shl 0, flags4 = ByteString.of(0, 0, 1, 0), ignoreMovementCost = ByteString.of()))

        val roundTripped = entries.toDomain(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList())
            .toWire(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList())

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() splits a multi-bit aiStrategies back into canonical + duplicate entries") {
        val entries = listOf(
            prtoEntry(name = "Rifleman", aiStrategies = 1 shl 0, flags4 = ByteString.of(0, 0, 1, 0), ignoreMovementCost = ByteString.of()),
            prtoEntry(name = "Rifleman", aiStrategies = 1 shl 1, otherStrategy = 0, flags4 = ByteString.of(0, 0, 1, 0), ignoreMovementCost = ByteString.of()),
        )

        val roundTripped = entries.toDomain(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList())
            .toWire(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList())

        roundTripped.size shouldBe 2
        roundTripped[0].aiStrategies shouldBe (1 shl 0)
        roundTripped[0].otherStrategy shouldBe -1
        roundTripped[1].aiStrategies shouldBe (1 shl 1)
        roundTripped[1].otherStrategy shouldBe 0
    }

    test("toDomain().toWire() splits 3 duplicates sharing one canonical") {
        val flagsValue = ByteString.of(0, 0, 1, 0)
        val emptyIgnore = ByteString.of()
        val entries = listOf(
            prtoEntry(name = "X", aiStrategies = 1 shl 0, flags4 = flagsValue, ignoreMovementCost = emptyIgnore),
            prtoEntry(name = "X", aiStrategies = 1 shl 1, otherStrategy = 0, flags4 = flagsValue, ignoreMovementCost = emptyIgnore),
            prtoEntry(name = "X", aiStrategies = 1 shl 2, otherStrategy = 0, flags4 = flagsValue, ignoreMovementCost = emptyIgnore),
            prtoEntry(name = "X", aiStrategies = 1 shl 3, otherStrategy = 0, flags4 = flagsValue, ignoreMovementCost = emptyIgnore),
        )

        val roundTripped = entries.toDomain(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList())
            .toWire(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList())

        roundTripped.size shouldBe 4
        roundTripped.drop(1).forEach { it.otherStrategy shouldBe 0 }
    }

    test("toDomain().toWire() round-trips cross-references and self-references") {
        val advance = tech("Gunpowder")
        val r = resource()
        val raceVal = race("Rome")
        val t = Terrain(
            name = "Desert",
            allowances = TerrAllowances(
                allowCities = 0, allowColonies = 0, impassable = 0, impassableByWheeled = 0,
                allowAirfields = 0, allowForts = 0, allowOutposts = 0, allowRadarTowers = 0,
            ),
        )
        val entries = listOf(
            prtoEntry(name = "Warrior"),
            prtoEntry(
                name = "Musketeers", upgradeTo = 0, enslaveResults = 0, stealthTargetUnitTypes = listOf(0),
                required = 0, requiredResource1 = 0, availableTo = 1 shl 0, ignoreMovementCostByte = 1,
                flags4 = ByteString.of(0, 0, 1, 0),
            ),
        )

        val roundTripped = entries.toDomain(Civ3FormatEra.CONQUESTS, listOf(advance), listOf(r), listOf(raceVal), listOf(t))
            .toWire(Civ3FormatEra.CONQUESTS, listOf(advance), listOf(r), listOf(raceVal), listOf(t))

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() round-trips unified and direct actions for Conquests") {
        val entries = listOf(
            prtoEntry(
                standardOrders = (1 shl 0) or (1 shl 5) or (1 shl 6),
                specialActions = (1 shl 0) or (1 shl 9) or (1 shl 18),
                workerActions = (1 shl 0) or (1 shl 13),
                airMissions = 1 shl 0,
                // Computed flags4: bits 0 (sentry), 12 (bombing), 16 (always 1) = 69633 = 0x11001
                // Little-endian bytes: 0x01, 0x10, 0x01, 0x00
                flags4 = ByteString.of(0x01.toByte(), 0x10.toByte(), 0x01.toByte(), 0x00.toByte()),
                ignoreMovementCost = ByteString.of(),
            ),
        )

        val roundTripped = entries.toDomain(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList())
            .toWire(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList())

        roundTripped shouldBe entries
    }

    test("toDomain().toWire() writes VANILLA-era output into flags2 and zeroes the modern fields") {
        val prto = validPrto("Warrior").also {
            it.skipTurn = true
            it.load = true
            it.buildColony = true
            it.bombing = true
        }

        val wire = listOf(prto).toWire(Civ3FormatEra.VANILLA, emptyList(), emptyList(), emptyList(), emptyList()).single()

        wire.standardOrders shouldBe 0
        wire.specialActions shouldBe 0
        wire.workerActions shouldBe 0
        wire.airMissions shouldBe 0
        wire.flags4 shouldBe ByteString.of(0, 0, 0, 0)
        wire.flags2.toIntLeForTest(0).let { (it shr 0) and 1 } shouldBe 1
        wire.flags2.toIntLeForTest(0).let { (it shr 5) and 1 } shouldBe 1
        wire.flags2.toIntLeForTest(0).let { (it shr 14) and 1 } shouldBe 1
        wire.flags2.toIntLeForTest(4).let { (it shr 0) and 1 } shouldBe 1
    }

    test("toDomain().toWire() drops VANILLA-unrepresentable direct actions for VANILLA output") {
        val prto = validPrto("Spy").also { it.enslave = true }

        val wire = listOf(prto).toWire(Civ3FormatEra.VANILLA, emptyList(), emptyList(), emptyList(), emptyList()).single()

        wire.specialActions shouldBe 0
    }

    test("toWire throws on a dangling required reference") {
        val prto = validPrto().also { it.required = tech("Outsider") }

        shouldThrow<IllegalArgumentException> {
            listOf(prto).toWire(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList())
        }
    }

    test("toWire throws on a dangling availableTo reference") {
        val prto = validPrto().also { it.availableTo = mutableSetOf(race("Outsider")) }

        shouldThrow<IllegalArgumentException> {
            listOf(prto).toWire(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList())
        }
    }

    test("toWire throws on a dangling self-reference not present in the passed-through roster") {
        val prto = validPrto()
        val outsider = validPrto("Outsider")
        prto.enslaveResults = outsider

        shouldThrow<IllegalArgumentException> {
            listOf(prto).toWire(Civ3FormatEra.CONQUESTS, emptyList(), emptyList(), emptyList(), emptyList())
        }
    }
})
