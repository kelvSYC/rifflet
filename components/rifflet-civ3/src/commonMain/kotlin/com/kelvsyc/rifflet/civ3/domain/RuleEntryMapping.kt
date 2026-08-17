package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.RuleDefaultUnits
import com.kelvsyc.rifflet.civ3.RuleEntry

/**
 * Converts a parsed `RULE` section to its domain-layer form.
 *
 * [prtos]/[resources]/[difficulties] are the already domain-converted `PRTO`/`GOOD`/`DIFF` lists.
 * Flat list, no cardinality guard — matches `WMAP`'s treatment: `RULE`'s "always exactly 1 entry"
 * is a real-file observation, not a Rules-Editor-enforced structural guarantee the mapping needs
 * to assert.
 */
fun List<RuleEntry>.toDomain(prtos: List<Prto>, resources: List<Resource>, difficulties: List<Difficulty>): List<Rule> = map { entry ->
    Rule(
        citySizeLevels = entry.citySizeLevels,
        spaceshipPartQuantities = entry.spaceshipPartQuantities.toMutableList(),
        defaultUnits = DefaultUnits(
            advancedBarbarianUnitType = prtos.getOrNull(entry.defaultUnits.advancedBarbarianUnitType),
            basicBarbarianUnitType = prtos.getOrNull(entry.defaultUnits.basicBarbarianUnitType),
            barbarianSeaUnitType = prtos.getOrNull(entry.defaultUnits.barbarianSeaUnitType),
            battleCreatedUnit = prtos.getOrNull(entry.defaultUnits.battleCreatedUnit),
            buildArmyUnit = prtos.getOrNull(entry.defaultUnits.buildArmyUnit),
            scout = prtos.getOrNull(entry.defaultUnits.scout),
            slave = prtos.getOrNull(entry.defaultUnits.slave),
            startUnit1 = prtos.getOrNull(entry.defaultUnits.startUnit1),
            startUnit2 = prtos.getOrNull(entry.defaultUnits.startUnit2),
            flagUnitType = entry.defaultUnits.flagUnitType?.let { prtos.getOrNull(it) },
        ),
        citiesNeededToSupportAnArmy = entry.citiesNeededToSupportAnArmy,
        chanceOfRioting = entry.chanceOfRioting,
        turnPenaltyForEachDraftedCitizen = entry.turnPenaltyForEachDraftedCitizen,
        shieldCostPerGold = entry.shieldCostPerGold,
        defensiveBonuses = entry.defensiveBonuses,
        citizensAffectedByEachHappyFace = entry.citizensAffectedByEachHappyFace,
        unknown = entry.unknown,
        forestValueInShields = entry.forestValueInShields,
        shieldValueInGold = entry.shieldValueInGold,
        citizenValueInShields = entry.citizenValueInShields,
        defaultDifficulty = difficulties.getOrNull(entry.defaultDifficultyLevel),
        defaultMoneyResource = resources.getOrNull(entry.defaultMoneyResource),
        chanceToInterceptEnemyAirMissions = entry.chanceToInterceptEnemyAirMissions,
        chanceToInterceptEnemyStealthMissions = entry.chanceToInterceptEnemyStealthMissions,
        startingTreasury = entry.startingTreasury,
        unknown2 = entry.unknown2,
        foodConsumptionPerCitizen = entry.foodConsumptionPerCitizen,
        turnPenaltyForEachHurrySacrifice = entry.turnPenaltyForEachHurrySacrifice,
        movementAlongRoads = entry.movementAlongRoads,
        minimumPopulationForWeLoveTheKing = entry.minimumPopulationForWeLoveTheKing,
        unknown3 = entry.unknown3,
        culture = entry.culture,
        technology = entry.technology,
        goldenAgeDuration = entry.goldenAgeDuration,
        upgradeCost = entry.upgradeCost,
    )
}

/**
 * Converts a `RULE` section's domain-layer form back to wire entries.
 *
 * Throws [IllegalArgumentException] if any `DefaultUnits` field, [Rule.defaultMoneyResource], or
 * [Rule.defaultDifficulty] resolves to an object not present in the corresponding list argument —
 * `indexOf`-based, the same accepted structural-equality limitation as every other `toWire()` in
 * this codebase. A `null` value writes back `-1` for the nine standard `DefaultUnits` fields and
 * for `defaultMoneyResource`/`defaultDifficulty`; `flagUnitType` is an exception — it writes back
 * `null` directly since its wire field is itself `Int?` (nullable).
 */
fun List<Rule>.toWire(prtos: List<Prto>, resources: List<Resource>, difficulties: List<Difficulty>): List<RuleEntry> = map { rule ->
    fun resolvePrto(field: String, prto: Prto?): Int = prto?.let {
        val index = prtos.indexOf(it)
        require(index >= 0) { "DefaultUnits.$field references a Prto not present in prtos" }
        index
    } ?: -1

    val defaultUnits = RuleDefaultUnits(
        advancedBarbarianUnitType = resolvePrto("advancedBarbarianUnitType", rule.defaultUnits.advancedBarbarianUnitType),
        basicBarbarianUnitType = resolvePrto("basicBarbarianUnitType", rule.defaultUnits.basicBarbarianUnitType),
        barbarianSeaUnitType = resolvePrto("barbarianSeaUnitType", rule.defaultUnits.barbarianSeaUnitType),
        battleCreatedUnit = resolvePrto("battleCreatedUnit", rule.defaultUnits.battleCreatedUnit),
        buildArmyUnit = resolvePrto("buildArmyUnit", rule.defaultUnits.buildArmyUnit),
        scout = resolvePrto("scout", rule.defaultUnits.scout),
        slave = resolvePrto("slave", rule.defaultUnits.slave),
        startUnit1 = resolvePrto("startUnit1", rule.defaultUnits.startUnit1),
        startUnit2 = resolvePrto("startUnit2", rule.defaultUnits.startUnit2),
        flagUnitType = rule.defaultUnits.flagUnitType?.let {
            val index = prtos.indexOf(it)
            require(index >= 0) { "DefaultUnits.flagUnitType references a Prto not present in prtos" }
            index
        },
    )
    val defaultMoneyResourceIndex = rule.defaultMoneyResource?.let {
        val index = resources.indexOf(it)
        require(index >= 0) { "Rule.defaultMoneyResource references a Resource not present in resources" }
        index
    } ?: -1
    val defaultDifficultyLevelIndex = rule.defaultDifficulty?.let {
        val index = difficulties.indexOf(it)
        require(index >= 0) { "Rule.defaultDifficulty references a Difficulty not present in difficulties" }
        index
    } ?: -1

    RuleEntry(
        citySizeLevels = rule.citySizeLevels,
        spaceshipPartQuantities = rule.spaceshipPartQuantities,
        defaultUnits = defaultUnits,
        citiesNeededToSupportAnArmy = rule.citiesNeededToSupportAnArmy,
        chanceOfRioting = rule.chanceOfRioting,
        turnPenaltyForEachDraftedCitizen = rule.turnPenaltyForEachDraftedCitizen,
        shieldCostPerGold = rule.shieldCostPerGold,
        defensiveBonuses = rule.defensiveBonuses,
        citizensAffectedByEachHappyFace = rule.citizensAffectedByEachHappyFace,
        unknown = rule.unknown,
        forestValueInShields = rule.forestValueInShields,
        shieldValueInGold = rule.shieldValueInGold,
        citizenValueInShields = rule.citizenValueInShields,
        defaultDifficultyLevel = defaultDifficultyLevelIndex,
        defaultMoneyResource = defaultMoneyResourceIndex,
        chanceToInterceptEnemyAirMissions = rule.chanceToInterceptEnemyAirMissions,
        chanceToInterceptEnemyStealthMissions = rule.chanceToInterceptEnemyStealthMissions,
        startingTreasury = rule.startingTreasury,
        unknown2 = rule.unknown2,
        foodConsumptionPerCitizen = rule.foodConsumptionPerCitizen,
        turnPenaltyForEachHurrySacrifice = rule.turnPenaltyForEachHurrySacrifice,
        movementAlongRoads = rule.movementAlongRoads,
        minimumPopulationForWeLoveTheKing = rule.minimumPopulationForWeLoveTheKing,
        unknown3 = rule.unknown3,
        culture = rule.culture,
        technology = rule.technology,
        goldenAgeDuration = rule.goldenAgeDuration,
        upgradeCost = rule.upgradeCost,
    )
}
