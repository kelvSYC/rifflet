package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun bytesFor(value: Int): List<Byte> = listOf(
    (value and 0xFF).toByte(),
    ((value shr 8) and 0xFF).toByte(),
    ((value shr 16) and 0xFF).toByte(),
    ((value shr 24) and 0xFF).toByte(),
)

private fun bldgEntry(
    improvements: Int = 0,
    otherCharacteristics: Int = 0,
    smallWonders: Int = 0,
    wonders: Int = 0,
    requiredBuilding: Int = -1,
    doublesHappiness: Int = -1,
    gainInEveryCity: Int = -1,
    gainInEveryCityOnContinent: Int = -1,
    spaceshipPart: Int = -1,
    numberOfRequiredBuildings: Int = 1,
    bombardDefense: Int = 0,
    navalBombardDefense: Int = 0,
    defenseBonus: Int = 0,
    navalDefenseBonus: Int = 0,
    airPower: Int = 0,
    navalPower: Int = 0,
    numberOfArmiesRequired: Int = 0,
    renderedObsoleteBy: Int = -1,
    unitProduced: Int = -1,
    maintenanceCost: Int = 0,
    culture: Int = 0,
    production: Int = 0,
    pollution: Int = 0,
    contentFacesAllCities: Int = 0,
    contentFaces: Int = 0,
    unhappyFacesAllCities: Int = 0,
    unhappyFaces: Int = 0,
): BldgEntry = BldgEntry(
    description = "",
    name = "",
    civilopediaEntry = "",
    doublesHappiness = doublesHappiness,
    gainInEveryCity = gainInEveryCity,
    gainInEveryCityOnContinent = gainInEveryCityOnContinent,
    requirements = BldgRequirements(requiredBuilding = requiredBuilding, requiredGovernment = -1, requiredAdvance = -1),
    cost = 0,
    culture = culture,
    combatValues = BldgCombatValues(
        bombardDefense = bombardDefense,
        navalBombardDefense = navalBombardDefense,
        defenseBonus = defenseBonus,
        airPower = airPower,
        navalPower = navalPower,
    ),
    navalDefenseBonus = navalDefenseBonus,
    maintenanceCost = maintenanceCost,
    happiness = BldgHappiness(
        contentFacesAllCities = contentFacesAllCities,
        contentFaces = contentFaces,
        unhappyFacesAllCities = unhappyFacesAllCities,
        unhappyFaces = unhappyFaces,
    ),
    numberOfRequiredBuildings = numberOfRequiredBuildings,
    pollution = pollution,
    production = production,
    spaceshipPart = spaceshipPart,
    renderedObsoleteBy = renderedObsoleteBy,
    requiredResources = BldgRequiredResources(requiredResource1 = -1, requiredResource2 = -1),
    flags = ByteString.of(
        *(bytesFor(improvements) + bytesFor(otherCharacteristics) + bytesFor(smallWonders) + bytesFor(wonders)).toByteArray(),
    ),
    numberOfArmiesRequired = numberOfArmiesRequired,
    flavors = 0,
    unknown = ByteString.of(*ByteArray(4)),
    unitsProduced = BldgUnitsProduced(unitProduced = unitProduced, unitFrequency = 0),
)

private fun fileWithBldgs(entries: List<BldgEntry>): Civ3File = Civ3File(
    Civ3Header(major = 12, minor = 0, description = "", title = ""),
    listOf(BldgSection(entries)),
)

private fun goodEntryForBonusCheck(type: GoodResourceType): GoodEntry = GoodEntry(
    name = "", civilopediaEntry = "", type = type, appearanceRatio = 0,
    disappearanceProbability = 0, icon = 0, prerequisite = -1,
    foodBonus = 0, shieldsBonus = 0, commerceBonus = 0,
)

private fun fileWithGoodsAndBldg(goods: List<GoodEntry>, bldgEntries: List<BldgEntry>): Civ3File = Civ3File(
    Civ3Header(major = 12, minor = 0, description = "", title = ""),
    listOf(GoodSection(goods), BldgSection(bldgEntries)),
)

class BldgEntryValidationTest : FunSpec({

    test("returns no issues when at most one entry has centerOfEmpire") {
        val center = bldgEntry(improvements = 1)
        val ordinary = bldgEntry()
        val file = fileWithBldgs(listOf(center, ordinary))

        validateBldgSingleCenterOfEmpire(file) shouldBe emptyList()
    }

    test("flags a BLDG section with more than one centerOfEmpire entry") {
        val first = bldgEntry(improvements = 1)
        val second = bldgEntry(improvements = 1)
        val file = fileWithBldgs(listOf(first, second))

        validateBldgSingleCenterOfEmpire(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                null,
                "centerOfEmpire",
                "2 entries have centerOfEmpire set; at most one is expected",
            ),
        )
    }

    test("returns no issues for centerOfEmpire when BLDG is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateBldgSingleCenterOfEmpire(file) shouldBe emptyList()
    }

    test("returns no issues for a plain improvement with no wonder effects") {
        val file = fileWithBldgs(listOf(bldgEntry()))

        validateBldgImprovementHasNoWonderEffects(file) shouldBe emptyList()
    }

    test("returns no issues for a Wonder or Small Wonder carrying wonder effects") {
        val wonder = bldgEntry(otherCharacteristics = 1 shl 2, wonders = 1, doublesHappiness = 3)
        val smallWonder = bldgEntry(otherCharacteristics = 1 shl 3, smallWonders = 1)
        val file = fileWithBldgs(listOf(wonder, smallWonder))

        validateBldgImprovementHasNoWonderEffects(file) shouldBe emptyList()
    }

    test("flags a plain improvement carrying wonder effects") {
        val file = fileWithBldgs(
            listOf(bldgEntry(wonders = 1, smallWonders = 2, doublesHappiness = 3, gainInEveryCity = 4, gainInEveryCityOnContinent = 5)),
        )

        validateBldgImprovementHasNoWonderEffects(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                0,
                "wonders",
                "wonders=1 is set on a plain Improvement, which the Rules Editor never allows",
            ),
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                0,
                "smallWonders",
                "smallWonders=2 is set on a plain Improvement, which the Rules Editor never allows",
            ),
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                0,
                "doublesHappiness",
                "doublesHappiness=3 is set on a plain Improvement, which the Rules Editor never allows",
            ),
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                0,
                "gainInEveryCity",
                "gainInEveryCity=4 is set on a plain Improvement, which the Rules Editor never allows",
            ),
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                0,
                "gainInEveryCityOnContinent",
                "gainInEveryCityOnContinent=5 is set on a plain Improvement, which the Rules Editor never allows",
            ),
        )
    }

    test("returns no issues for improvement wonder effects when BLDG is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateBldgImprovementHasNoWonderEffects(file) shouldBe emptyList()
    }

    test("returns no issues for a Small Wonder with no happiness reference") {
        val file = fileWithBldgs(listOf(bldgEntry(otherCharacteristics = 1 shl 3, smallWonders = 1)))

        validateBldgSmallWonderHasNoHappinessReference(file) shouldBe emptyList()
    }

    test("flags a Small Wonder carrying a happiness reference") {
        val file = fileWithBldgs(
            listOf(
                bldgEntry(
                    otherCharacteristics = 1 shl 3,
                    smallWonders = 1,
                    doublesHappiness = 3,
                    gainInEveryCity = 4,
                    gainInEveryCityOnContinent = 5,
                ),
            ),
        )

        validateBldgSmallWonderHasNoHappinessReference(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                0,
                "doublesHappiness",
                "doublesHappiness=3 is set on a Small Wonder, which the Rules Editor never allows",
            ),
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                0,
                "gainInEveryCity",
                "gainInEveryCity=4 is set on a Small Wonder, which the Rules Editor never allows",
            ),
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                0,
                "gainInEveryCityOnContinent",
                "gainInEveryCityOnContinent=5 is set on a Small Wonder, which the Rules Editor never allows",
            ),
        )
    }

    test("returns no issues for Small Wonder happiness reference when BLDG is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateBldgSmallWonderHasNoHappinessReference(file) shouldBe emptyList()
    }

    test("returns no issues for a well-formed spaceship part") {
        val file = fileWithBldgs(listOf(bldgEntry(spaceshipPart = 0)))

        validateBldgSpaceshipPartInvariants(file) shouldBe emptyList()
    }

    test("flags a spaceship part that is a Wonder") {
        val file = fileWithBldgs(listOf(bldgEntry(spaceshipPart = 0, otherCharacteristics = 1 shl 2)))

        validateBldgSpaceshipPartInvariants(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                0,
                "spaceshipPart",
                "spaceshipPart is set but requires Building Type Improvement (not Wonder or Small Wonder)",
            ),
        )
    }

    test("flags a spaceship part with numberOfRequiredBuildings other than 1") {
        val file = fileWithBldgs(listOf(bldgEntry(spaceshipPart = 0, numberOfRequiredBuildings = 2)))

        validateBldgSpaceshipPartInvariants(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                0,
                "numberOfRequiredBuildings",
                "spaceshipPart is set but requires numberOfRequiredBuildings=1 (was 2)",
            ),
        )
    }

    test("flags a spaceship part with a nonzero combat value") {
        val file = fileWithBldgs(listOf(bldgEntry(spaceshipPart = 0, airPower = 1)))

        validateBldgSpaceshipPartInvariants(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                0,
                "combat values",
                "spaceshipPart is set but requires bombardDefense=0 (0), navalBombardDefense=0 (0), " +
                    "defenseBonus=0 (0), navalDefenseBonus=0 (0), airPower=0 (1), navalPower=0 (0)",
            ),
        )
    }

    test("flags a spaceship part with numberOfArmiesRequired set") {
        val file = fileWithBldgs(listOf(bldgEntry(spaceshipPart = 0, numberOfArmiesRequired = 1)))

        validateBldgSpaceshipPartInvariants(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                0,
                "numberOfArmiesRequired",
                "spaceshipPart is set but requires numberOfArmiesRequired=0 (was 1)",
            ),
        )
    }

    test("flags a spaceship part that renders another building obsolete") {
        val file = fileWithBldgs(listOf(bldgEntry(spaceshipPart = 0, renderedObsoleteBy = 5)))

        validateBldgSpaceshipPartInvariants(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                0,
                "renderedObsoleteBy",
                "spaceshipPart is set but requires renderedObsoleteBy=-1 (was 5)",
            ),
        )
    }

    test("flags a spaceship part that produces a unit on a Conquests-era file") {
        val file = fileWithBldgs(listOf(bldgEntry(spaceshipPart = 0, unitProduced = 5)))

        validateBldgSpaceshipPartInvariants(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                0,
                "unitProduced",
                "spaceshipPart is set but requires unitProduced=-1 (was 5)",
            ),
        )
    }

    test("does not check unitProduced on a non-Conquests-era file") {
        val file = Civ3File(
            Civ3Header(major = 11, minor = 18, description = "", title = ""),
            listOf(BldgSection(listOf(bldgEntry(spaceshipPart = 0, unitProduced = 5)))),
        )

        validateBldgSpaceshipPartInvariants(file) shouldBe emptyList()
    }

    test("returns no issues for spaceship part invariants when BLDG is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateBldgSpaceshipPartInvariants(file) shouldBe emptyList()
    }

    test("returns no issues for a conventionally-shaped spaceship part") {
        val file = fileWithBldgs(listOf(bldgEntry(spaceshipPart = 0)))

        validateBldgSpaceshipPartConventionalStats(file) shouldBe emptyList()
    }

    test("allows agricultural/seafaring on a spaceship part without a warning") {
        val file = fileWithBldgs(listOf(bldgEntry(spaceshipPart = 0, otherCharacteristics = (1 shl 10) or (1 shl 11))))

        validateBldgSpaceshipPartConventionalStats(file) shouldBe emptyList()
    }

    test("warns about a spaceship part with nonzero maintenance/culture/production/pollution") {
        val file = fileWithBldgs(listOf(bldgEntry(spaceshipPart = 0, maintenanceCost = 1, culture = 1)))

        validateBldgSpaceshipPartConventionalStats(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.WARNING,
                Civ3SectionIds.BLDG,
                0,
                "maintenanceCost/culture/production/pollution",
                "spaceshipPart is set but conventionally has maintenanceCost=0 (1), culture=0 (1), " +
                    "production=0 (0), pollution=0 (0)",
            ),
        )
    }

    test("warns about a spaceship part with a happiness effect") {
        val file = fileWithBldgs(listOf(bldgEntry(spaceshipPart = 0, contentFaces = 1)))

        validateBldgSpaceshipPartConventionalStats(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.WARNING,
                Civ3SectionIds.BLDG,
                0,
                "happiness settings",
                "spaceshipPart is set but conventionally has no happiness effect " +
                    "(contentFacesAllCities=0, contentFaces=1, unhappyFacesAllCities=0, unhappyFaces=0, " +
                    "doublesHappiness=-1, gainInEveryCity=-1, gainInEveryCityOnContinent=-1)",
            ),
        )
    }

    test("warns about a spaceship part with a characteristic flag other than agricultural/seafaring") {
        val file = fileWithBldgs(listOf(bldgEntry(spaceshipPart = 0, otherCharacteristics = 1 shl 5)))

        validateBldgSpaceshipPartConventionalStats(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.WARNING,
                Civ3SectionIds.BLDG,
                0,
                "otherCharacteristics",
                "spaceshipPart is set but conventionally has no characteristic flags other than " +
                    "agricultural/seafaring (otherCharacteristics=32)",
            ),
        )
    }

    test("warns about a spaceship part with an improvement flag") {
        val file = fileWithBldgs(listOf(bldgEntry(spaceshipPart = 0, improvements = 4)))

        validateBldgSpaceshipPartConventionalStats(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.WARNING,
                Civ3SectionIds.BLDG,
                0,
                "improvements",
                "spaceshipPart is set but conventionally has no improvement flags (improvements=4)",
            ),
        )
    }

    test("returns no issues for spaceship part conventional stats when BLDG is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateBldgSpaceshipPartConventionalStats(file) shouldBe emptyList()
    }

    test("findSelfReferenceCycle returns null for an acyclic graph") {
        val entries = listOf(bldgEntry(), bldgEntry(requiredBuilding = 0))
        findSelfReferenceCycle(entries) { it.requirements.requiredBuilding } shouldBe null
    }

    test("findSelfReferenceCycle finds a 2-node cycle") {
        val entries = listOf(bldgEntry(requiredBuilding = 1), bldgEntry(requiredBuilding = 0))
        findSelfReferenceCycle(entries) { it.requirements.requiredBuilding }?.size shouldBe 3
    }

    test("findSelfReferenceCycle finds a self-loop") {
        val entries = listOf(bldgEntry(requiredBuilding = 0))
        findSelfReferenceCycle(entries) { it.requirements.requiredBuilding }?.size shouldBe 2
    }

    test("validateBldgGainInEveryCityNotWonder returns no issues for a plain-Improvement target") {
        val target = bldgEntry()
        val entry = bldgEntry(wonders = 1, gainInEveryCity = 0)
        val file = fileWithBldgs(listOf(target, entry))

        validateBldgGainInEveryCityNotWonder(file) shouldBe emptyList()
    }

    test("validateBldgGainInEveryCityNotWonder flags a Wonder target") {
        val target = bldgEntry(otherCharacteristics = 1 shl 2) // wonder bit
        val entry = bldgEntry(wonders = 1, gainInEveryCity = 0)
        val file = fileWithBldgs(listOf(target, entry))

        validateBldgGainInEveryCityNotWonder(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                1,
                "gainInEveryCity",
                "gainInEveryCity=0 resolves to a Wonder or Small Wonder, which the Rules Editor never allows",
            ),
        )
    }

    test("validateBldgGainInEveryCityOnContinentNotWonder flags a Small Wonder target") {
        val target = bldgEntry(otherCharacteristics = 1 shl 3) // smallWonder bit
        val entry = bldgEntry(wonders = 1, gainInEveryCityOnContinent = 0)
        val file = fileWithBldgs(listOf(target, entry))

        validateBldgGainInEveryCityOnContinentNotWonder(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                1,
                "gainInEveryCityOnContinent",
                "gainInEveryCityOnContinent=0 resolves to a Wonder or Small Wonder, which the Rules Editor never allows",
            ),
        )
    }

    test("validateBldgRequiredBuildingAcyclic returns no issues for an acyclic graph") {
        val file = fileWithBldgs(listOf(bldgEntry(), bldgEntry(requiredBuilding = 0)))

        validateBldgRequiredBuildingAcyclic(file) shouldBe emptyList()
    }

    test("validateBldgRequiredBuildingAcyclic flags a cycle") {
        val file = fileWithBldgs(listOf(bldgEntry(requiredBuilding = 1), bldgEntry(requiredBuilding = 0)))

        val issues = validateBldgRequiredBuildingAcyclic(file)
        issues.size shouldBe 1
        issues.single().severity shouldBe ValidationSeverity.ERROR
        issues.single().field shouldBe "requiredBuilding"
    }

    test("validateBldgWonderEffectsAcyclic returns no issues for an acyclic graph") {
        val file = fileWithBldgs(listOf(bldgEntry(), bldgEntry(wonders = 1, doublesHappiness = 0)))

        validateBldgWonderEffectsAcyclic(file) shouldBe emptyList()
    }

    test("validateBldgWonderEffectsAcyclic flags a doublesHappiness cycle") {
        val file = fileWithBldgs(
            listOf(
                bldgEntry(wonders = 1, doublesHappiness = 1),
                bldgEntry(wonders = 1, doublesHappiness = 0),
            ),
        )

        val issues = validateBldgWonderEffectsAcyclic(file)
        issues.size shouldBe 1
        issues.single().field shouldBe "doublesHappiness"
    }

    test("validateBldgNotBothWonderAndSmallWonder returns no issues for a plain wonder-only entry") {
        val file = fileWithBldgs(listOf(bldgEntry(otherCharacteristics = 1 shl 2)))

        validateBldgNotBothWonderAndSmallWonder(file) shouldBe emptyList()
    }

    test("validateBldgNotBothWonderAndSmallWonder flags an entry with both bits set") {
        val file = fileWithBldgs(listOf(bldgEntry(otherCharacteristics = (1 shl 2) or (1 shl 3))))

        val issues = validateBldgNotBothWonderAndSmallWonder(file)
        issues.size shouldBe 1
        issues.single().field shouldBe "otherCharacteristics"
    }

    test("validateBldgRequiredResourceNotBonus returns no issues when both required resources are Luxury/Strategic") {
        val goods = listOf(goodEntryForBonusCheck(GoodResourceType.LUXURY), goodEntryForBonusCheck(GoodResourceType.STRATEGIC))
        val entry = bldgEntry().copy(requiredResources = BldgRequiredResources(requiredResource1 = 0, requiredResource2 = 1))
        val file = fileWithGoodsAndBldg(goods, listOf(entry))

        validateBldgRequiredResourceNotBonus(file) shouldBe emptyList()
    }

    test("validateBldgRequiredResourceNotBonus flags a Bonus-type requiredResource1") {
        val goods = listOf(goodEntryForBonusCheck(GoodResourceType.BONUS))
        val entry = bldgEntry().copy(requiredResources = BldgRequiredResources(requiredResource1 = 0, requiredResource2 = -1))
        val file = fileWithGoodsAndBldg(goods, listOf(entry))

        validateBldgRequiredResourceNotBonus(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                0,
                "requiredResource1",
                "a required resource must be Luxury or Strategic, not Bonus (requiredResource1)",
            ),
        )
    }

    test("validateBldgRequiredResourceNotBonus flags both fields when both are Bonus-type") {
        val goods = listOf(goodEntryForBonusCheck(GoodResourceType.BONUS))
        val entry = bldgEntry().copy(requiredResources = BldgRequiredResources(requiredResource1 = 0, requiredResource2 = 0))
        val file = fileWithGoodsAndBldg(goods, listOf(entry))

        validateBldgRequiredResourceNotBonus(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.BLDG,
                0,
                "requiredResource1, requiredResource2",
                "a required resource must be Luxury or Strategic, not Bonus (requiredResource1, requiredResource2)",
            ),
        )
    }

    test("validateBldgRequiredResourceNotBonus returns no issues when GOOD or BLDG is absent") {
        val onlyBldg = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), listOf(BldgSection(listOf(bldgEntry()))))
        val onlyGoods = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), listOf(GoodSection(listOf(goodEntryForBonusCheck(GoodResourceType.BONUS)))))

        validateBldgRequiredResourceNotBonus(onlyBldg) shouldBe emptyList()
        validateBldgRequiredResourceNotBonus(onlyGoods) shouldBe emptyList()
    }
})
