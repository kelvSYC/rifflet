package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun prtoEntry(
    type: Int = 0,
    abilities: Int = 0,
    aiStrategies: Int = 0,
    otherStrategy: Int = -1,
    attack: Int = 0,
    defense: Int = 0,
    bombardStrength: Int = 0,
    bombardRange: Int = 0,
    rateOfFire: Int = 0,
    capacity: Int = 0,
    standardOrders: Int = 0,
    specialActions: Int = 0,
    workerActions: Int = 0,
    operationalRange: Int = 0,
    airMissions: Int = 0,
): PrtoEntry = PrtoEntry(
    unitStatistics = PrtoUnitStatistics(
        zoneOfControl = 0,
        bombardStrength = bombardStrength,
        bombardRange = bombardRange,
        capacity = capacity,
        shieldCost = 0,
        defense = defense,
        attack = attack,
        operationalRange = operationalRange,
        populationCost = 0,
        rateOfFire = rateOfFire,
        movement = 0,
        upgradeTo = -1,
        hpBonus = 0,
        bombardEffects = 0,
        requireSupport = 0,
        createCraters = 0,
        workerStrength = 0f,
        airDefense = 0,
    ),
    name = "",
    civilopediaEntry = "",
    iconIndex = 0,
    required = -1,
    requiredResource1 = -1,
    requiredResource2 = -1,
    requiredResource3 = -1,
    abilities = abilities,
    aiStrategies = aiStrategies,
    availableTo = 0,
    flags2 = ByteString.of(*ByteArray(8)),
    type = type,
    otherStrategy = otherStrategy,
    standardOrders = standardOrders,
    specialActions = specialActions,
    workerActions = workerActions,
    airMissions = airMissions,
    flags4 = ByteString.of(*ByteArray(4)),
    ignoreMovementCost = ByteString.of(),
    unknown = ByteString.of(*ByteArray(16)),
    enslaveResults = -1,
    unknown2 = ByteString.of(*ByteArray(4)),
    stealthTargetUnitTypes = emptyList(),
    unknown3 = ByteString.of(*ByteArray(8)),
    unknown4 = ByteString.of(*ByteArray(4)),
)

private fun fileWithPrtos(entries: List<PrtoEntry>): Civ3File = Civ3File(
    Civ3Header(major = 12, minor = 0, description = "", title = ""),
    listOf(PrtoSection(entries)),
)

class PrtoEntryValidationTest : FunSpec({

    test("returns no issues for every documented type value (0-2)") {
        val file = fileWithPrtos((0..2).map { prtoEntry(type = it) })

        validatePrtoDomain(file) shouldBe emptyList()
    }

    test("flags a type value outside the documented 0-2 range") {
        val file = fileWithPrtos(listOf(prtoEntry(type = 3)))

        validatePrtoDomain(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "type",
                "type=3 is not a valid PrtoDomain index (0..2)",
            ),
        )
    }

    test("returns no issues when PRTO is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validatePrtoDomain(file) shouldBe emptyList()
    }

    test("returns no issues when armyAbility and armyStrategy agree") {
        val bothSet = prtoEntry(abilities = 1 shl 18, aiStrategies = 1 shl 4)
        val bothClear = prtoEntry()
        val file = fileWithPrtos(listOf(bothSet, bothClear))

        validatePrtoArmyStrategyConsistency(file) shouldBe emptyList()
    }

    test("flags armyAbility and armyStrategy disagreeing as a warning") {
        val abilityOnly = prtoEntry(abilities = 1 shl 18)
        val file = fileWithPrtos(listOf(abilityOnly))

        validatePrtoArmyStrategyConsistency(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.WARNING,
                Civ3SectionIds.PRTO,
                0,
                "armyStrategy",
                "armyAbility=true but armyStrategy=false; usually expected to agree",
            ),
        )
    }

    test("returns no issues for armyStrategy when PRTO is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validatePrtoArmyStrategyConsistency(file) shouldBe emptyList()
    }

    test("returns no issues when kingAbility and kingStrategy agree") {
        val bothSet = prtoEntry(abilities = 1 shl 29, aiStrategies = 1 shl 19)
        val bothClear = prtoEntry()
        val file = fileWithPrtos(listOf(bothSet, bothClear))

        validatePrtoKingStrategyConsistency(file) shouldBe emptyList()
    }

    test("flags kingAbility and kingStrategy disagreeing as a warning") {
        val strategyOnly = prtoEntry(aiStrategies = 1 shl 19)
        val file = fileWithPrtos(listOf(strategyOnly))

        validatePrtoKingStrategyConsistency(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.WARNING,
                Civ3SectionIds.PRTO,
                0,
                "kingStrategy",
                "kingAbility=false but kingStrategy=true; usually expected to agree",
            ),
        )
    }

    test("returns no issues for kingStrategy when PRTO is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validatePrtoKingStrategyConsistency(file) shouldBe emptyList()
    }

    test("returns no issues for otherStrategy of -1 or any valid PRTO index") {
        val entries = listOf(prtoEntry(otherStrategy = -1), prtoEntry(otherStrategy = 0), prtoEntry(otherStrategy = 1))
        val file = fileWithPrtos(entries)

        validatePrtoOtherStrategyBounds(file) shouldBe emptyList()
    }

    test("flags an otherStrategy outside the section's index bounds") {
        val file = fileWithPrtos(listOf(prtoEntry(otherStrategy = 1)))

        validatePrtoOtherStrategyBounds(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "otherStrategy",
                "otherStrategy=1 is not -1 or a valid PRTO index (0..0)",
            ),
        )
    }

    test("returns no issues for otherStrategy when PRTO is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validatePrtoOtherStrategyBounds(file) shouldBe emptyList()
    }

    test("returns no issues when every land strategy's prerequisites are satisfied") {
        val entries = listOf(
            prtoEntry(aiStrategies = 1 shl 0, attack = 1, defense = 1, specialActions = (1 shl 0) or (1 shl 9)),
            prtoEntry(aiStrategies = 1 shl 1, attack = 1, defense = 1, specialActions = (1 shl 0) or (1 shl 9)),
            prtoEntry(aiStrategies = 1 shl 2, bombardStrength = 1, specialActions = 1 shl 4),
            prtoEntry(
                aiStrategies = 1 shl 5,
                bombardStrength = 1,
                bombardRange = 1,
                rateOfFire = 1,
                specialActions = 1 shl 4,
                abilities = 1 shl 3,
            ),
            prtoEntry(
                aiStrategies = 1 shl 15,
                bombardRange = 1,
                specialActions = 1 shl 4,
                abilities = (1 shl 16) or (1 shl 23),
            ),
            prtoEntry(aiStrategies = 1 shl 16, specialActions = 1 shl 4, abilities = (1 shl 16) or (1 shl 20)),
            prtoEntry(aiStrategies = 1 shl 18, abilities = (1 shl 10) or (1 shl 13)),
            prtoEntry(aiStrategies = 1 shl 3),
            prtoEntry(
                aiStrategies = 1 shl 12,
                workerActions = (1 shl 0) or (1 shl 2) or (1 shl 3) or (1 shl 4) or (1 shl 5) or (1 shl 6) or
                    (1 shl 7) or (1 shl 8) or (1 shl 9) or (1 shl 10) or (1 shl 11) or (1 shl 12),
            ),
            prtoEntry(
                aiStrategies = 1 shl 13,
                specialActions = 1 shl 0,
                workerActions = (1 shl 1) or (1 shl 12),
            ),
            prtoEntry(aiStrategies = 1 shl 4, specialActions = 1 shl 0, abilities = 1 shl 18),
            prtoEntry(aiStrategies = 1 shl 14, specialActions = (1 shl 6) or (1 shl 7)),
            prtoEntry(aiStrategies = 1 shl 19, abilities = 1 shl 29),
        )
        val file = fileWithPrtos(entries)

        validatePrtoLandStrategyPrerequisites(file) shouldBe emptyList()
    }

    test("flags offenseStrategy set without its prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(aiStrategies = 1 shl 0)))

        validatePrtoLandStrategyPrerequisites(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "offenseStrategy",
                "offenseStrategy is set but requires attack>0 (0), defense>0 (0), load (false), capture (false)",
            ),
        )
    }

    test("flags defenseStrategy set without its prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(aiStrategies = 1 shl 1)))

        validatePrtoLandStrategyPrerequisites(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "defenseStrategy",
                "defenseStrategy is set but requires attack>0 (0), defense>0 (0), load (false), capture (false)",
            ),
        )
    }

    test("flags artilleryStrategy set without its prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(aiStrategies = 1 shl 2)))

        validatePrtoLandStrategyPrerequisites(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "artilleryStrategy",
                "artilleryStrategy is set but requires bombardStrength>0 (0), bombard (false), " +
                    "no cruiseMissileAbility (false), no nuclearWeaponAbility (false)",
            ),
        )
    }

    test("flags cruiseMissileStrategy set without its prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(aiStrategies = 1 shl 5)))

        validatePrtoLandStrategyPrerequisites(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "cruiseMissileStrategy",
                "cruiseMissileStrategy is set but requires bombardStrength>0 (0), bombardRange>0 (0), " +
                    "rateOfFire>0 (0), bombard (false), cruiseMissileAbility (false)",
            ),
        )
    }

    test("flags tacticalNukeStrategy set without its prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(aiStrategies = 1 shl 15)))

        validatePrtoLandStrategyPrerequisites(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "tacticalNukeStrategy",
                "tacticalNukeStrategy is set but requires bombardRange>0 (0), bombard (false), " +
                    "nuclearWeaponAbility (false), tacticalMissileAbility (false)",
            ),
        )
    }

    test("flags icbmStrategy set without its prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(aiStrategies = 1 shl 16)))

        validatePrtoLandStrategyPrerequisites(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "icbmStrategy",
                "icbmStrategy is set but requires bombard (false), nuclearWeaponAbility (false), " +
                    "infiniteBombardRangeAbility (false)",
            ),
        )
    }

    test("flags flagUnitStrategy set without its prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(aiStrategies = 1 shl 18, attack = 1)))

        validatePrtoLandStrategyPrerequisites(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "flagUnitStrategy",
                "flagUnitStrategy is set but requires attack=0 (1), defense=0 (0), bombardStrength=0 (0), " +
                    "capacity=0 (0), immobileAbility (false), flagUnitAbility (false), no disband (false)",
            ),
        )
    }

    test("flags exploreStrategy set on an immobile unit") {
        val file = fileWithPrtos(listOf(prtoEntry(aiStrategies = 1 shl 3, abilities = 1 shl 10)))

        validatePrtoLandStrategyPrerequisites(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "exploreStrategy",
                "exploreStrategy is set but requires no immobileAbility (true)",
            ),
        )
    }

    test("flags terraformStrategy set without its prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(aiStrategies = 1 shl 12)))

        validatePrtoLandStrategyPrerequisites(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "terraformStrategy",
                "terraformStrategy is set but requires buildColony (false), buildRoad (false), " +
                    "buildRailroad (false), buildFort (false), buildMine (false), irrigate (false), " +
                    "clearForest (false), clearJungle (false), plantForest (false), clearPollution (false), " +
                    "automate (false), joinCity (false)",
            ),
        )
    }

    test("flags settleStrategy set without its prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(aiStrategies = 1 shl 13)))

        validatePrtoLandStrategyPrerequisites(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "settleStrategy",
                "settleStrategy is set but requires load (false), buildCity (false), joinCity (false)",
            ),
        )
    }

    test("flags armyStrategy set without its prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(aiStrategies = 1 shl 4)))

        validatePrtoLandStrategyPrerequisites(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "armyStrategy",
                "armyStrategy is set but requires load (false), armyAbility (false)",
            ),
        )
    }

    test("flags leaderStrategy set without its prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(aiStrategies = 1 shl 14)))

        validatePrtoLandStrategyPrerequisites(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "leaderStrategy",
                "leaderStrategy is set but requires buildArmy (false), finishImprovements (false)",
            ),
        )
    }

    test("flags kingStrategy set without its prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(aiStrategies = 1 shl 19)))

        validatePrtoLandStrategyPrerequisites(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "kingStrategy",
                "kingStrategy is set but requires kingAbility (false), no disband (false)",
            ),
        )
    }

    test("skips non-LAND entries even when a strategy is set without prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(type = 1, aiStrategies = 1 shl 0)))

        validatePrtoLandStrategyPrerequisites(file) shouldBe emptyList()
    }

    test("returns no issues for land strategy prerequisites when PRTO is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validatePrtoLandStrategyPrerequisites(file) shouldBe emptyList()
    }

    test("returns no issues when every sea strategy's prerequisites are satisfied") {
        val entries = listOf(
            prtoEntry(type = 1, aiStrategies = 1 shl 8, attack = 1, defense = 1),
            prtoEntry(type = 1, aiStrategies = 1 shl 10, specialActions = 1 shl 1),
            prtoEntry(type = 1, aiStrategies = 1 shl 11, abilities = 1 shl 8, specialActions = 1 shl 1),
            prtoEntry(type = 1, aiStrategies = 1 shl 17, abilities = 1 shl 24, specialActions = 1 shl 1),
        )
        val file = fileWithPrtos(entries)

        validatePrtoSeaStrategyPrerequisites(file) shouldBe emptyList()
    }

    test("flags navalPowerStrategy set without its prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(type = 1, aiStrategies = 1 shl 8)))

        validatePrtoSeaStrategyPrerequisites(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "navalPowerStrategy",
                "navalPowerStrategy is set but requires attack>0 (0), defense>0 (0)",
            ),
        )
    }

    test("flags navalTransportStrategy set without its prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(type = 1, aiStrategies = 1 shl 10)))

        validatePrtoSeaStrategyPrerequisites(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "navalTransportStrategy",
                "navalTransportStrategy is set but requires unload (false)",
            ),
        )
    }

    test("flags navalCarrierStrategy set without its prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(type = 1, aiStrategies = 1 shl 11)))

        validatePrtoSeaStrategyPrerequisites(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "navalCarrierStrategy",
                "navalCarrierStrategy is set but requires transportsOnlyAircraftAbility (false), unload (false)",
            ),
        )
    }

    test("flags navalMissileTransportStrategy set without its prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(type = 1, aiStrategies = 1 shl 17)))

        validatePrtoSeaStrategyPrerequisites(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "navalMissileTransportStrategy",
                "navalMissileTransportStrategy is set but requires transportsOnlyTacticalMissilesAbility (false), " +
                    "unload (false)",
            ),
        )
    }

    test("skips non-SEA entries even when a sea strategy is set without prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(type = 0, aiStrategies = 1 shl 8)))

        validatePrtoSeaStrategyPrerequisites(file) shouldBe emptyList()
    }

    test("returns no issues for sea strategy prerequisites when PRTO is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validatePrtoSeaStrategyPrerequisites(file) shouldBe emptyList()
    }

    test("returns no issues when every air strategy's prerequisites are satisfied") {
        val entries = listOf(
            prtoEntry(
                type = 2,
                aiStrategies = 1 shl 6,
                bombardStrength = 1,
                operationalRange = 1,
                airMissions = 1 shl 0,
            ),
            prtoEntry(
                type = 2,
                aiStrategies = 1 shl 7,
                attack = 1,
                operationalRange = 1,
                airMissions = 1 shl 2,
            ),
            prtoEntry(
                type = 2,
                aiStrategies = 1 shl 9,
                operationalRange = 1,
                specialActions = (1 shl 5) or (1 shl 1),
            ),
        )
        val file = fileWithPrtos(entries)

        validatePrtoAirStrategyPrerequisites(file) shouldBe emptyList()
    }

    test("flags airBombardStrategy set without its prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(type = 2, aiStrategies = 1 shl 6)))

        validatePrtoAirStrategyPrerequisites(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "airBombardStrategy",
                "airBombardStrategy is set but requires bombardStrength>0 (0), operationalRange>0 (0), " +
                    "bombing or precisionBombing (false / false)",
            ),
        )
    }

    test("flags airDefenseStrategy set without its prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(type = 2, aiStrategies = 1 shl 7)))

        validatePrtoAirStrategyPrerequisites(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "airDefenseStrategy",
                "airDefenseStrategy is set but requires attack>0 (0), operationalRange>0 (0), interception (false)",
            ),
        )
    }

    test("flags airTransportStrategy set without its prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(type = 2, aiStrategies = 1 shl 9)))

        validatePrtoAirStrategyPrerequisites(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                0,
                "airTransportStrategy",
                "airTransportStrategy is set but requires operationalRange>0 (0), airdrop (false), unload (false)",
            ),
        )
    }

    test("skips non-AIR entries even when an air strategy is set without prerequisites") {
        val file = fileWithPrtos(listOf(prtoEntry(type = 0, aiStrategies = 1 shl 6)))

        validatePrtoAirStrategyPrerequisites(file) shouldBe emptyList()
    }

    test("returns no issues for air strategy prerequisites when PRTO is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validatePrtoAirStrategyPrerequisites(file) shouldBe emptyList()
    }
})
