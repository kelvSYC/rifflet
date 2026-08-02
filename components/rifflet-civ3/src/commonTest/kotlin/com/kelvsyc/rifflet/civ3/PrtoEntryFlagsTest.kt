package com.kelvsyc.rifflet.civ3

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun bytesFor(value: Int): List<Byte> = listOf(
    (value and 0xFF).toByte(),
    ((value shr 8) and 0xFF).toByte(),
    ((value shr 16) and 0xFF).toByte(),
    ((value shr 24) and 0xFF).toByte(),
)

private fun validPrtoEntry(
    abilities: Int = 0,
    aiStrategies: Int = 0,
    standardOrders: Int = 0,
    specialActions: Int = 0,
    workerActions: Int = 0,
    airMissions: Int = 0,
    flags2Bits: Int = 0,
    flags2HighBits: Int = 0,
    flags4Bits: Int = 0,
): PrtoEntry = PrtoEntry(
    unitStatistics = PrtoUnitStatistics(
        zoneOfControl = 0, bombardStrength = 0, bombardRange = 0, capacity = 0, shieldCost = 0,
        defense = 0, attack = 0, operationalRange = 0, populationCost = 0, rateOfFire = 0,
        movement = 0, upgradeTo = -1, hpBonus = 0, bombardEffects = 0, requireSupport = 0,
        createCraters = 0, workerStrength = 0f, airDefense = 0,
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
    flags2 = ByteString.of(*(bytesFor(flags2Bits) + bytesFor(flags2HighBits)).toByteArray()),
    type = PrtoDomain.LAND,
    otherStrategy = 0,
    standardOrders = standardOrders,
    specialActions = specialActions,
    workerActions = workerActions,
    airMissions = airMissions,
    flags4 = ByteString.of(*bytesFor(flags4Bits).toByteArray()),
    ignoreMovementCost = ByteString.of(),
    unknown = ByteString.of(*ByteArray(16)),
    enslaveResults = -1,
    unknown2 = ByteString.of(*ByteArray(4)),
    stealthTargetUnitTypes = emptyList(),
    unknown3 = ByteString.of(*ByteArray(8)),
    unknown4 = ByteString.of(*ByteArray(4)),
)

class PrtoEntryAbilitiesTest : FunSpec({

    val properties: List<Pair<Int, (PrtoEntry) -> Boolean>> = listOf(
        0 to PrtoEntry::wheeledAbility,
        1 to PrtoEntry::footUnitAbility,
        2 to PrtoEntry::blitzAbility,
        3 to PrtoEntry::cruiseMissileAbility,
        4 to PrtoEntry::allTerrainAsRoadsAbility,
        5 to PrtoEntry::radarAbility,
        6 to PrtoEntry::amphibiousAbility,
        7 to PrtoEntry::invisibleAbility,
        8 to PrtoEntry::transportsOnlyAircraftAbility,
        9 to PrtoEntry::draftAbility,
        10 to PrtoEntry::immobileAbility,
        11 to PrtoEntry::sinksInSeaAbility,
        12 to PrtoEntry::sinksInOceanAbility,
        13 to PrtoEntry::flagUnitAbility,
        14 to PrtoEntry::transportsOnlyFootUnitsAbility,
        15 to PrtoEntry::startsGoldenAgeAbility,
        16 to PrtoEntry::nuclearWeaponAbility,
        17 to PrtoEntry::hiddenNationalityAbility,
        18 to PrtoEntry::armyAbility,
        19 to PrtoEntry::leaderAbility,
        20 to PrtoEntry::infiniteBombardRangeAbility,
        21 to PrtoEntry::stealthAbility,
        22 to PrtoEntry::detectInvisibleAbility,
        23 to PrtoEntry::tacticalMissileAbility,
        24 to PrtoEntry::transportsOnlyTacticalMissilesAbility,
        25 to PrtoEntry::rangedAttackAnimationAbility,
        26 to PrtoEntry::rotateBeforeAttackAbility,
        27 to PrtoEntry::lethalLandBombardmentAbility,
        28 to PrtoEntry::lethalSeaBombardmentAbility,
        29 to PrtoEntry::kingAbility,
        30 to PrtoEntry::requiresEscortAbility,
    )

    test("abilities is read straight through, unmodified") {
        validPrtoEntry(abilities = 0x01020304).abilities shouldBe 0x01020304
    }

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validPrtoEntry(abilities = 1 shl bit)
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validPrtoEntry(abilities = allBits)
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validPrtoEntry(abilities = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})

class PrtoEntryAiStrategiesTest : FunSpec({

    val properties: List<Pair<Int, (PrtoEntry) -> Boolean>> = listOf(
        0 to PrtoEntry::offenseStrategy,
        1 to PrtoEntry::defenseStrategy,
        2 to PrtoEntry::artilleryStrategy,
        3 to PrtoEntry::exploreStrategy,
        4 to PrtoEntry::armyStrategy,
        5 to PrtoEntry::cruiseMissileStrategy,
        6 to PrtoEntry::airBombardStrategy,
        7 to PrtoEntry::airDefenseStrategy,
        8 to PrtoEntry::navalPowerStrategy,
        9 to PrtoEntry::airTransportStrategy,
        10 to PrtoEntry::navalTransportStrategy,
        11 to PrtoEntry::navalCarrierStrategy,
        12 to PrtoEntry::terraformStrategy,
        13 to PrtoEntry::settleStrategy,
        14 to PrtoEntry::leaderStrategy,
        15 to PrtoEntry::tacticalNukeStrategy,
        16 to PrtoEntry::icbmStrategy,
        17 to PrtoEntry::navalMissileTransportStrategy,
        18 to PrtoEntry::flagUnitStrategy,
        19 to PrtoEntry::kingStrategy,
    )

    test("aiStrategies is read straight through, unmodified") {
        validPrtoEntry(aiStrategies = 0x01020304).aiStrategies shouldBe 0x01020304
    }

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validPrtoEntry(aiStrategies = 1 shl bit)
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validPrtoEntry(aiStrategies = allBits)
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validPrtoEntry(aiStrategies = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})

class PrtoEntryStandardOrdersTest : FunSpec({

    val properties: List<Pair<Int, (PrtoEntry) -> Boolean>> = listOf(
        0 to PrtoEntry::skipTurn,
        1 to PrtoEntry::wait,
        2 to PrtoEntry::fortify,
        3 to PrtoEntry::disband,
        4 to PrtoEntry::goTo,
        5 to PrtoEntry::explore,
        6 to PrtoEntry::sentry,
    )

    test("standardOrders is read straight through, unmodified") {
        validPrtoEntry(standardOrders = 0x01020304).standardOrders shouldBe 0x01020304
    }

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validPrtoEntry(standardOrders = 1 shl bit)
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validPrtoEntry(standardOrders = allBits)
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validPrtoEntry(standardOrders = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})

class PrtoEntrySpecialActionsTest : FunSpec({

    val properties: List<Pair<Int, (PrtoEntry) -> Boolean>> = listOf(
        0 to PrtoEntry::load,
        1 to PrtoEntry::unload,
        2 to PrtoEntry::airlift,
        3 to PrtoEntry::pillage,
        4 to PrtoEntry::bombard,
        5 to PrtoEntry::airdrop,
        6 to PrtoEntry::buildArmy,
        7 to PrtoEntry::finishImprovements,
        8 to PrtoEntry::upgradeUnit,
        9 to PrtoEntry::capture,
        16 to PrtoEntry::stealthAttack,
        18 to PrtoEntry::enslave,
        20 to PrtoEntry::sacrifice,
        21 to PrtoEntry::startsScienceAge,
    )

    test("specialActions is read straight through, unmodified") {
        validPrtoEntry(specialActions = 0x01020304).specialActions shouldBe 0x01020304
    }

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validPrtoEntry(specialActions = 1 shl bit)
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validPrtoEntry(specialActions = allBits)
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validPrtoEntry(specialActions = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})

class PrtoEntryWorkerActionsTest : FunSpec({

    val properties: List<Pair<Int, (PrtoEntry) -> Boolean>> = listOf(
        0 to PrtoEntry::buildColony,
        1 to PrtoEntry::buildCity,
        2 to PrtoEntry::buildRoad,
        3 to PrtoEntry::buildRailroad,
        4 to PrtoEntry::buildFort,
        5 to PrtoEntry::buildMine,
        6 to PrtoEntry::irrigate,
        7 to PrtoEntry::clearForest,
        8 to PrtoEntry::clearJungle,
        9 to PrtoEntry::plantForest,
        10 to PrtoEntry::clearPollution,
        11 to PrtoEntry::automate,
        12 to PrtoEntry::joinCity,
        13 to PrtoEntry::buildAirfield,
        14 to PrtoEntry::buildRadarTower,
        15 to PrtoEntry::buildOutpost,
        16 to PrtoEntry::buildBarricade,
    )

    test("workerActions is read straight through, unmodified") {
        validPrtoEntry(workerActions = 0x01020304).workerActions shouldBe 0x01020304
    }

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validPrtoEntry(workerActions = 1 shl bit)
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validPrtoEntry(workerActions = allBits)
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validPrtoEntry(workerActions = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})

class PrtoEntryVanillaStandardOrdersTest : FunSpec({

    val properties: List<Pair<Int, (PrtoEntry) -> Boolean>> = listOf(
        0 to PrtoEntry::vanillaSkipTurn,
        1 to PrtoEntry::vanillaWait,
        2 to PrtoEntry::vanillaFortify,
        3 to PrtoEntry::vanillaDisband,
        4 to PrtoEntry::vanillaGoTo,
    )

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validPrtoEntry(flags2Bits = 1 shl bit)
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validPrtoEntry(flags2Bits = allBits)
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validPrtoEntry(flags2Bits = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})

class PrtoEntryVanillaSpecialActionsTest : FunSpec({

    val properties: List<Pair<Int, (PrtoEntry) -> Boolean>> = listOf(
        5 to PrtoEntry::vanillaLoad,
        6 to PrtoEntry::vanillaUnload,
        7 to PrtoEntry::vanillaAirlift,
        8 to PrtoEntry::vanillaPillage,
        9 to PrtoEntry::vanillaBombard,
        10 to PrtoEntry::vanillaAirdrop,
        11 to PrtoEntry::vanillaBuildArmy,
        12 to PrtoEntry::vanillaFinishImprovements,
        13 to PrtoEntry::vanillaUpgradeUnit,
    )

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validPrtoEntry(flags2Bits = 1 shl bit)
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validPrtoEntry(flags2Bits = allBits)
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validPrtoEntry(flags2Bits = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})

class PrtoEntryVanillaWorkerActionsTest : FunSpec({

    val properties: List<Pair<Int, (PrtoEntry) -> Boolean>> = listOf(
        14 to PrtoEntry::vanillaBuildColony,
        15 to PrtoEntry::vanillaBuildCity,
        16 to PrtoEntry::vanillaBuildRoad,
        17 to PrtoEntry::vanillaBuildRailroad,
        18 to PrtoEntry::vanillaBuildFort,
        19 to PrtoEntry::vanillaBuildMine,
        20 to PrtoEntry::vanillaIrrigate,
        21 to PrtoEntry::vanillaClearForest,
        22 to PrtoEntry::vanillaClearJungle,
        23 to PrtoEntry::vanillaPlantForest,
        24 to PrtoEntry::vanillaClearPollution,
        25 to PrtoEntry::vanillaAutomate,
        26 to PrtoEntry::vanillaJoinCity,
    )

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validPrtoEntry(flags2Bits = 1 shl bit)
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validPrtoEntry(flags2Bits = allBits)
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validPrtoEntry(flags2Bits = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})

class PrtoEntryAirMissionsTest : FunSpec({

    val properties: List<Pair<Int, (PrtoEntry) -> Boolean>> = listOf(
        0 to PrtoEntry::bombing,
        1 to PrtoEntry::recon,
        2 to PrtoEntry::interception,
        3 to PrtoEntry::rebase,
        4 to PrtoEntry::precisionBombing,
    )

    test("airMissions is read straight through, unmodified") {
        validPrtoEntry(airMissions = 0x01020304).airMissions shouldBe 0x01020304
    }

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validPrtoEntry(airMissions = 1 shl bit)
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validPrtoEntry(airMissions = allBits)
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validPrtoEntry(airMissions = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})

class PrtoEntryFlags4BitsTest : FunSpec({

    test("flags4Bits extracts the entire (4-byte) flags4 field as a little-endian Int") {
        validPrtoEntry(flags4Bits = 0x01020304).flags4Bits shouldBe 0x01020304
    }
})

class PrtoEntryFlags2LowBitsTest : FunSpec({

    test("flags2LowBits extracts the low 4 bytes of the 8-byte flags2 field as a little-endian Int") {
        validPrtoEntry(flags2Bits = 0x01020304).flags2LowBits shouldBe 0x01020304
    }
})

class PrtoEntryFlags2HighBitsTest : FunSpec({

    test("flags2HighBits extracts the high 4 bytes of the 8-byte flags2 field as a little-endian Int") {
        validPrtoEntry(flags2HighBits = 0x01020304).flags2HighBits shouldBe 0x01020304
    }
})

class PrtoEntryVanillaAirMissionsTest : FunSpec({

    val properties: List<Pair<Int, (PrtoEntry) -> Boolean>> = listOf(
        0 to PrtoEntry::vanillaBombing,
        1 to PrtoEntry::vanillaRecon,
        2 to PrtoEntry::vanillaInterception,
        3 to PrtoEntry::vanillaRebase,
        4 to PrtoEntry::vanillaPrecisionBombing,
    )

    test("each bit maps to exactly its own named property") {
        for ((bit, _) in properties) {
            val entry = validPrtoEntry(flags2HighBits = 1 shl bit)
            for ((otherBit, otherProperty) in properties) {
                otherProperty(entry) shouldBe (otherBit == bit)
            }
        }
    }

    test("all named bits set") {
        val allBits = properties.fold(0) { acc, (bit, _) -> acc or (1 shl bit) }
        val entry = validPrtoEntry(flags2HighBits = allBits)
        properties.forEach { (_, property) -> property(entry) shouldBe true }
    }

    test("all named bits clear") {
        val entry = validPrtoEntry(flags2HighBits = 0)
        properties.forEach { (_, property) -> property(entry) shouldBe false }
    }
})
