package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.EspnEntry
import com.kelvsyc.rifflet.civ3.ExprEntry
import com.kelvsyc.rifflet.civ3.GovtCorruption
import com.kelvsyc.rifflet.civ3.GovtEntry
import com.kelvsyc.rifflet.civ3.GovtHurrying
import com.kelvsyc.rifflet.civ3.GovtRelationship
import com.kelvsyc.rifflet.civ3.GovtRulerTitles
import com.kelvsyc.rifflet.civ3.GovtUnitSupportCosts
import com.kelvsyc.rifflet.civ3.GovtWarWeariness
import com.kelvsyc.rifflet.civ3.TechEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun govtEntry(
    defaultType: Int = 0,
    transitionType: Int = 0,
    requiresMaintenance: Int = 0,
    toggle1: Int = 0,
    tilePenalty: Int = 0,
    tradeBonus: Int = 0,
    name: String = "",
    civilopediaEntry: String = "",
    rulerTitles: GovtRulerTitles = GovtRulerTitles(
        male1 = "", female1 = "",
        male2 = "", female2 = "",
        male3 = "", female3 = "",
        male4 = "", female4 = "",
    ),
    corruption: GovtCorruption = GovtCorruption.MINIMAL,
    immuneTo: Int = -1,
    diplomatsAre: Int = -1,
    spiesAre: Int = -1,
    relationships: List<GovtRelationship> = emptyList(),
    hurrying: GovtHurrying = GovtHurrying.CANNOT_HURRY,
    assimilationChance: Int = 0,
    draftLimit: Int = 0,
    militaryPoliceLimit: Int = 0,
    rulerTitlePairsUsed: Int = 0,
    prerequisiteTechnology: Int = -1,
    scienceRateCap: Int = 0,
    workerRate: Int = 0,
    toggle2: Int = 0,
    toggle3: Int = 0,
    unknown: ByteString = ByteString.of(0, 0, 0, 0),
    unitSupportCosts: GovtUnitSupportCosts = GovtUnitSupportCosts(
        freeUnits = 0,
        freeUnitsPerTown = 0,
        freeUnitsPerCity = 0,
        freeUnitsPerMetropolis = 0,
        unitCost = 0,
    ),
    warWeariness: GovtWarWeariness = GovtWarWeariness.NONE,
    xenophobic: Int = 0,
    forceResettle: Int = 0,
): GovtEntry = GovtEntry(
    defaultType = defaultType,
    transitionType = transitionType,
    requiresMaintenance = requiresMaintenance,
    toggle1 = toggle1,
    tilePenalty = tilePenalty,
    tradeBonus = tradeBonus,
    name = name,
    civilopediaEntry = civilopediaEntry,
    rulerTitles = rulerTitles,
    corruption = corruption,
    immuneTo = immuneTo,
    diplomatsAre = diplomatsAre,
    spiesAre = spiesAre,
    relationships = relationships,
    hurrying = hurrying,
    assimilationChance = assimilationChance,
    draftLimit = draftLimit,
    militaryPoliceLimit = militaryPoliceLimit,
    rulerTitlePairsUsed = rulerTitlePairsUsed,
    prerequisiteTechnology = prerequisiteTechnology,
    scienceRateCap = scienceRateCap,
    workerRate = workerRate,
    toggle2 = toggle2,
    toggle3 = toggle3,
    unknown = unknown,
    unitSupportCosts = unitSupportCosts,
    warWeariness = warWeariness,
    xenophobic = xenophobic,
    forceResettle = forceResettle,
)

private fun techEntry(): TechEntry = TechEntry(
    name = "", civilopediaEntry = "", cost = 0, era = 0, advanceIcon = 0, x = 0, y = 0,
    prerequisite1 = 0, prerequisite2 = 0, prerequisite3 = 0, prerequisite4 = 0,
    flags = 0, flavors = 0, unknown = ByteString.of(0, 0, 0, 0),
)

private fun espnEntry(): EspnEntry = EspnEntry(
    description = "", name = "", civilopediaEntry = "", missionFlags = 0, baseCost = 0,
)

private fun exprEntry(): ExprEntry = ExprEntry(name = "", baseHitPoints = 0, retreatBonus = 0)

private fun validGovernmentFor(name: String): Government = Government(
    name = name,
    civilopediaEntry = "",
    rulerTitles = GovtRulerTitles(
        male1 = "", female1 = "",
        male2 = "", female2 = "",
        male3 = "", female3 = "",
        male4 = "", female4 = "",
    ),
    corruption = GovtCorruption.MINIMAL,
    hurrying = GovtHurrying.CANNOT_HURRY,
    unitSupportCosts = GovtUnitSupportCosts(
        freeUnits = 0,
        freeUnitsPerTown = 0,
        freeUnitsPerCity = 0,
        freeUnitsPerMetropolis = 0,
        unitCost = 0,
    ),
    warWeariness = GovtWarWeariness.NONE,
)

class GovtEntryMappingTest : FunSpec({

    test("toDomain maps scalar and grouped fields straight across") {
        val rulerTitles = GovtRulerTitles(
            male1 = "Chief", female1 = "Chieftess",
            male2 = "", female2 = "",
            male3 = "", female3 = "",
            male4 = "", female4 = "",
        )
        val unitSupportCosts = GovtUnitSupportCosts(
            freeUnits = 2, freeUnitsPerTown = 1, freeUnitsPerCity = 2, freeUnitsPerMetropolis = 3, unitCost = 1,
        )
        val entry = govtEntry(
            name = "Despotism",
            civilopediaEntry = "civilopedia text",
            rulerTitles = rulerTitles,
            corruption = GovtCorruption.RAMPANT,
            hurrying = GovtHurrying.FORCED_LABOR,
            unitSupportCosts = unitSupportCosts,
            warWeariness = GovtWarWeariness.HIGH,
            tilePenalty = 1,
            tradeBonus = 2,
            assimilationChance = 3,
            draftLimit = 4,
            militaryPoliceLimit = 5,
            rulerTitlePairsUsed = 1,
            scienceRateCap = 60,
            workerRate = 1,
            toggle1 = 111,
            toggle2 = 222,
            toggle3 = 333,
            unknown = ByteString.of(9, 9, 9, 9),
        )

        val government = listOf(entry).toDomain(emptyList(), emptyList(), emptyList()).single()

        government.name shouldBe "Despotism"
        government.civilopediaEntry shouldBe "civilopedia text"
        government.rulerTitles shouldBe rulerTitles
        government.corruption shouldBe GovtCorruption.RAMPANT
        government.hurrying shouldBe GovtHurrying.FORCED_LABOR
        government.unitSupportCosts shouldBe unitSupportCosts
        government.warWeariness shouldBe GovtWarWeariness.HIGH
        government.tilePenalty shouldBe 1
        government.tradeBonus shouldBe 2
        government.assimilationChance shouldBe 3
        government.draftLimit shouldBe 4
        government.militaryPoliceLimit shouldBe 5
        government.rulerTitlePairsUsed shouldBe 1
        government.scienceRateCap shouldBe 60
        government.workerRate shouldBe 1
        government.toggle1 shouldBe 111
        government.toggle2 shouldBe 222
        government.toggle3 shouldBe 333
        government.unknown shouldBe ByteString.of(9, 9, 9, 9)
    }

    test("toDomain converts Int-shaped booleans to Boolean") {
        val entry = govtEntry(
            defaultType = 1, transitionType = 0, requiresMaintenance = 1, xenophobic = 0, forceResettle = 1,
        )

        val government = listOf(entry).toDomain(emptyList(), emptyList(), emptyList()).single()

        government.defaultType shouldBe true
        government.transitionType shouldBe false
        government.requiresMaintenance shouldBe true
        government.xenophobic shouldBe false
        government.forceResettle shouldBe true
    }

    test("toDomain resolves external cross-references against the provided lists") {
        val tech = techEntry()
        val espn = espnEntry()
        val expr = exprEntry()
        val entry = govtEntry(prerequisiteTechnology = 0, immuneTo = 0, diplomatsAre = 0, spiesAre = 0)

        val government = listOf(entry).toDomain(listOf(tech), listOf(espn), listOf(expr)).single()

        government.prerequisiteTechnology shouldBe tech
        government.immuneTo shouldBe espn
        government.diplomatsAre shouldBe expr
        government.spiesAre shouldBe expr
    }

    test("toDomain resolves -1/out-of-range cross-references to null") {
        val entry = govtEntry(prerequisiteTechnology = -1, immuneTo = -1, diplomatsAre = -1, spiesAre = -1)

        val government = listOf(entry).toDomain(emptyList(), emptyList(), emptyList()).single()

        government.prerequisiteTechnology shouldBe null
        government.immuneTo shouldBe null
        government.diplomatsAre shouldBe null
        government.spiesAre shouldBe null
    }

    test("toDomain populates relationships keyed by sibling Government references") {
        val relationship1 = GovtRelationship(canBribe = 0, propagandaModifier = 5, resistanceModifier = 10)
        val relationship2 = GovtRelationship(canBribe = 1, propagandaModifier = 15, resistanceModifier = 20)
        val entry1 = govtEntry(name = "Despotism", relationships = listOf(relationship1, relationship2))
        val entry2 = govtEntry(name = "Anarchy", relationships = listOf(relationship2, relationship1))

        val governments = listOf(entry1, entry2).toDomain(emptyList(), emptyList(), emptyList())
        val (government1, government2) = governments

        government1.relationships shouldBe mapOf(government1 to relationship1, government2 to relationship2)
        government2.relationships shouldBe mapOf(government1 to relationship2, government2 to relationship1)
    }

    test("toDomain().toWire() round-trips a full GOVT section") {
        val rulerTitles = GovtRulerTitles(
            male1 = "Chief", female1 = "Chieftess",
            male2 = "", female2 = "",
            male3 = "", female3 = "",
            male4 = "", female4 = "",
        )
        val relationship1 = GovtRelationship(canBribe = 0, propagandaModifier = 5, resistanceModifier = 10)
        val relationship2 = GovtRelationship(canBribe = 1, propagandaModifier = 15, resistanceModifier = 20)
        val tech = techEntry()
        val espn = espnEntry()
        val expr = exprEntry()
        val entry1 = govtEntry(
            name = "Despotism",
            rulerTitles = rulerTitles,
            defaultType = 1,
            prerequisiteTechnology = 0,
            immuneTo = 0,
            diplomatsAre = 0,
            spiesAre = 0,
            relationships = listOf(relationship1, relationship2),
            toggle1 = 111,
        )
        val entry2 = govtEntry(name = "Anarchy", relationships = listOf(relationship2, relationship1))
        val original = listOf(entry1, entry2)

        val roundTripped = original.toDomain(listOf(tech), listOf(espn), listOf(expr))
            .toWire(listOf(tech), listOf(espn), listOf(expr))

        roundTripped shouldBe original
    }

    test("toWire defaults missing relationship entries to zero") {
        val government1 = validGovernmentFor("Despotism")
        val government2 = validGovernmentFor("Anarchy")
        // government1's relationships map is left empty entirely.

        val wire = listOf(government1, government2).toWire(emptyList(), emptyList(), emptyList())

        wire[0].relationships shouldBe listOf(
            GovtRelationship(canBribe = 0, propagandaModifier = 0, resistanceModifier = 0),
            GovtRelationship(canBribe = 0, propagandaModifier = 0, resistanceModifier = 0),
        )
    }

    test("toWire ignores relationship entries for governments outside the encoded roster") {
        val government1 = validGovernmentFor("Despotism")
        val outsider = validGovernmentFor("Outsider")
        val relationship = GovtRelationship(canBribe = 1, propagandaModifier = 1, resistanceModifier = 1)
        government1.relationships[outsider] = relationship
        government1.relationships[government1] = relationship

        val wire = listOf(government1).toWire(emptyList(), emptyList(), emptyList())

        wire.single().relationships shouldBe listOf(relationship)
    }

    test("toWire throws on a prerequisiteTechnology not present in the passed techs list") {
        val government = validGovernmentFor("Despotism")
        government.prerequisiteTechnology = techEntry()

        shouldThrow<IllegalArgumentException> { listOf(government).toWire(emptyList(), emptyList(), emptyList()) }
    }

    test("toWire throws on an immuneTo not present in the passed espionageMissions list") {
        val government = validGovernmentFor("Despotism")
        government.immuneTo = espnEntry()

        shouldThrow<IllegalArgumentException> { listOf(government).toWire(emptyList(), emptyList(), emptyList()) }
    }

    test("toWire throws on a diplomatsAre not present in the passed experienceLevels list") {
        val government = validGovernmentFor("Despotism")
        government.diplomatsAre = exprEntry()

        shouldThrow<IllegalArgumentException> { listOf(government).toWire(emptyList(), emptyList(), emptyList()) }
    }

    test("toWire throws on a spiesAre not present in the passed experienceLevels list") {
        val government = validGovernmentFor("Despotism")
        government.spiesAre = exprEntry()

        shouldThrow<IllegalArgumentException> { listOf(government).toWire(emptyList(), emptyList(), emptyList()) }
    }

    test("toWire allows cross-composition from independent toDomain() calls") {
        val tech = techEntry()
        val fileATechs = listOf(tech)
        val fileBGovtEntry = govtEntry(name = "Anarchy")
        val fileBGovernment = listOf(fileBGovtEntry).toDomain(emptyList(), emptyList(), emptyList()).single()
        fileBGovernment.prerequisiteTechnology = tech

        val wire = listOf(fileBGovernment).toWire(fileATechs, emptyList(), emptyList())

        wire.single().prerequisiteTechnology shouldBe 0
    }

    test("toggle1/2/3 round-trip raw, non-boolean values unchanged") {
        val entry = govtEntry(toggle1 = 7, toggle2 = -1, toggle3 = 12345)

        val roundTripped = listOf(entry).toDomain(emptyList(), emptyList(), emptyList())
            .toWire(emptyList(), emptyList(), emptyList())
            .single()

        roundTripped.toggle1 shouldBe 7
        roundTripped.toggle2 shouldBe -1
        roundTripped.toggle3 shouldBe 12345
    }
})
