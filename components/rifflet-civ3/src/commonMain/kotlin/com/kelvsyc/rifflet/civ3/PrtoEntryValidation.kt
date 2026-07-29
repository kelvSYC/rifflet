package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity

/**
 * Flags a [PrtoEntry] whose [PrtoEntry.type] doesn't decode into a [PrtoDomain]. Returns no
 * issues if the `PRTO` section is absent from [file].
 *
 * Every real official file's units have a `type` value in the documented 0-2 range, with zero
 * exceptions across every era and every degree of `PRTO` pruning observed.
 */
fun validatePrtoDomain(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<PrtoSection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if (entry.domainEnum != null) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                index,
                "type",
                "type=${entry.type} is not a valid PrtoDomain index (0..2)",
            )
        }
    }
}

/**
 * Flags a [PrtoEntry] where [PrtoEntry.armyAbility] and [PrtoEntry.armyStrategy] disagree. Returns
 * no issues if the `PRTO` section is absent from [file].
 *
 * These are two independent Units editor checkboxes that share the same population on every real
 * file, but the editor doesn't enforce agreement between them, so this is a
 * [ValidationSeverity.WARNING] rather than an [ValidationSeverity.ERROR].
 */
fun validatePrtoArmyStrategyConsistency(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<PrtoSection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if (entry.armyAbility == entry.armyStrategy) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.WARNING,
                Civ3SectionIds.PRTO,
                index,
                "armyStrategy",
                "armyAbility=${entry.armyAbility} but armyStrategy=${entry.armyStrategy}; usually expected to agree",
            )
        }
    }
}

/**
 * Flags a [PrtoEntry] where [PrtoEntry.kingAbility] and [PrtoEntry.kingStrategy] disagree. Returns
 * no issues if the `PRTO` section is absent from [file].
 *
 * These are two independent Units editor checkboxes that share the same population on nearly
 * every real file, but a handful of real scenario files pair them inconsistently (e.g. a
 * custom Leader-type unit carrying the ability without the strategy, or a Settler-type unit
 * carrying the strategy without the ability) — the editor doesn't enforce agreement between them,
 * so this is a [ValidationSeverity.WARNING] rather than an [ValidationSeverity.ERROR], same as
 * [validatePrtoArmyStrategyConsistency]'s equivalent pairing.
 */
fun validatePrtoKingStrategyConsistency(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<PrtoSection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if (entry.kingAbility == entry.kingStrategy) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.WARNING,
                Civ3SectionIds.PRTO,
                index,
                "kingStrategy",
                "kingAbility=${entry.kingAbility} but kingStrategy=${entry.kingStrategy}; usually expected to agree",
            )
        }
    }
}

/**
 * Flags a [PrtoEntry] whose [PrtoEntry.otherStrategy] is neither -1 nor a valid index into the
 * same `PRTO` section. Returns no issues if the `PRTO` section is absent from [file].
 *
 * Every real official file's entries have an `otherStrategy` that's either -1 or a genuine
 * in-bounds self-reference.
 */
fun validatePrtoOtherStrategyBounds(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<PrtoSection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        val other = entry.otherStrategy
        if (other == -1 || other in section.entries.indices) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                index,
                "otherStrategy",
                "otherStrategy=$other is not -1 or a valid PRTO index (0..${section.entries.size - 1})",
            )
        }
    }
}

/**
 * Flags a [PrtoEntry] whose [PrtoEntry.availableTo] has a bit set beyond the file's own `RACE`
 * section size. Returns no issues if `PRTO` or `RACE` is absent from [file].
 *
 * Every real official file's units have an `availableTo` bounded exactly to the file's own `RACE`
 * entry count, with zero exceptions.
 */
fun validatePrtoAvailableToBounds(file: Civ3File): List<ValidationIssue> {
    val prto = file.sections.filterIsInstance<PrtoSection>().singleOrNull() ?: return emptyList()
    val race = file.sections.filterIsInstance<RaceSection>().singleOrNull() ?: return emptyList()
    val raceCount = race.entries.size
    val mask = if (raceCount >= Int.SIZE_BITS) -1 else (1 shl raceCount) - 1
    return prto.entries.mapIndexedNotNull { index, entry ->
        val stray = entry.availableTo and mask.inv()
        if (stray == 0) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.PRTO,
                index,
                "availableTo",
                "availableTo=${entry.availableTo} has bit(s) set beyond RACE's own $raceCount entries",
            )
        }
    }
}

/**
 * Flags a [PrtoDomain.LAND] [PrtoEntry] whose AI Strategy checkbox is set despite failing that
 * checkbox's own real Units editor prerequisites. Returns no issues if the `PRTO` section is
 * absent from [file], and skips every entry whose [PrtoEntry.domainEnum] isn't [PrtoDomain.LAND]
 * (Sea and Air strategies have their own, different prerequisites).
 *
 * The real Units editor grays out each Land AI Strategy checkbox until its unit meets that
 * checkbox's own prerequisites:
 * - [offenseStrategy]/[defenseStrategy]: [PrtoEntry.unitStatistics]'s `attack`>0, `defense`>0,
 *   [load], [capture].
 * - [artilleryStrategy]: [PrtoEntry.unitStatistics]'s `bombardStrength`>0, [bombard], not
 *   [cruiseMissileAbility], not [nuclearWeaponAbility].
 * - [cruiseMissileStrategy]: [PrtoEntry.unitStatistics]'s `bombardStrength`>0, `bombardRange`>0,
 *   `rateOfFire`>0, [bombard], [cruiseMissileAbility].
 * - [tacticalNukeStrategy]: [PrtoEntry.unitStatistics]'s `bombardRange`>0, [bombard],
 *   [nuclearWeaponAbility], [tacticalMissileAbility].
 * - [icbmStrategy]: [bombard], [nuclearWeaponAbility], [infiniteBombardRangeAbility].
 * - [flagUnitStrategy]: [PrtoEntry.unitStatistics]'s `attack`, `defense`, `bombardStrength`, and
 *   `capacity` all `0`, [immobileAbility], [flagUnitAbility], not [disband].
 * - [exploreStrategy]: not [immobileAbility].
 * - [terraformStrategy]: [buildColony], [buildRoad], [buildRailroad], [buildFort], [buildMine],
 *   [irrigate], [clearForest], [clearJungle], [plantForest], [clearPollution], [automate],
 *   [joinCity].
 * - [settleStrategy]: [load], [buildCity], [joinCity].
 * - [armyStrategy]: [load], [armyAbility].
 * - [leaderStrategy]: [buildArmy], [finishImprovements].
 * - [kingStrategy]: [kingAbility], not [disband].
 */
fun validatePrtoLandStrategyPrerequisites(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<PrtoSection>().singleOrNull() ?: return emptyList()
    return section.entries.flatMapIndexed { index, entry ->
        if (entry.domainEnum != PrtoDomain.LAND) return@flatMapIndexed emptyList()

        fun issue(field: String, requirement: String) = ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.PRTO,
            index,
            field,
            "$field is set but requires $requirement",
        )

        listOfNotNull(
            if (entry.offenseStrategy &&
                !(entry.unitStatistics.attack > 0 && entry.unitStatistics.defense > 0 && entry.load && entry.capture)
            ) {
                issue(
                    "offenseStrategy",
                    "attack>0 (${entry.unitStatistics.attack}), defense>0 (${entry.unitStatistics.defense}), " +
                        "load (${entry.load}), capture (${entry.capture})",
                )
            } else {
                null
            },
            if (entry.defenseStrategy &&
                !(entry.unitStatistics.attack > 0 && entry.unitStatistics.defense > 0 && entry.load && entry.capture)
            ) {
                issue(
                    "defenseStrategy",
                    "attack>0 (${entry.unitStatistics.attack}), defense>0 (${entry.unitStatistics.defense}), " +
                        "load (${entry.load}), capture (${entry.capture})",
                )
            } else {
                null
            },
            if (entry.artilleryStrategy &&
                !(
                    entry.unitStatistics.bombardStrength > 0 && entry.bombard &&
                        !entry.cruiseMissileAbility && !entry.nuclearWeaponAbility
                    )
            ) {
                issue(
                    "artilleryStrategy",
                    "bombardStrength>0 (${entry.unitStatistics.bombardStrength}), bombard (${entry.bombard}), " +
                        "no cruiseMissileAbility (${entry.cruiseMissileAbility}), " +
                        "no nuclearWeaponAbility (${entry.nuclearWeaponAbility})",
                )
            } else {
                null
            },
            if (entry.cruiseMissileStrategy &&
                !(
                    entry.unitStatistics.bombardStrength > 0 && entry.unitStatistics.bombardRange > 0 &&
                        entry.unitStatistics.rateOfFire > 0 && entry.bombard && entry.cruiseMissileAbility
                    )
            ) {
                issue(
                    "cruiseMissileStrategy",
                    "bombardStrength>0 (${entry.unitStatistics.bombardStrength}), " +
                        "bombardRange>0 (${entry.unitStatistics.bombardRange}), " +
                        "rateOfFire>0 (${entry.unitStatistics.rateOfFire}), bombard (${entry.bombard}), " +
                        "cruiseMissileAbility (${entry.cruiseMissileAbility})",
                )
            } else {
                null
            },
            if (entry.tacticalNukeStrategy &&
                !(
                    entry.unitStatistics.bombardRange > 0 && entry.bombard && entry.nuclearWeaponAbility &&
                        entry.tacticalMissileAbility
                    )
            ) {
                issue(
                    "tacticalNukeStrategy",
                    "bombardRange>0 (${entry.unitStatistics.bombardRange}), bombard (${entry.bombard}), " +
                        "nuclearWeaponAbility (${entry.nuclearWeaponAbility}), " +
                        "tacticalMissileAbility (${entry.tacticalMissileAbility})",
                )
            } else {
                null
            },
            if (entry.icbmStrategy && !(entry.bombard && entry.nuclearWeaponAbility && entry.infiniteBombardRangeAbility)) {
                issue(
                    "icbmStrategy",
                    "bombard (${entry.bombard}), nuclearWeaponAbility (${entry.nuclearWeaponAbility}), " +
                        "infiniteBombardRangeAbility (${entry.infiniteBombardRangeAbility})",
                )
            } else {
                null
            },
            if (entry.flagUnitStrategy &&
                !(
                    entry.unitStatistics.attack == 0 && entry.unitStatistics.defense == 0 &&
                        entry.unitStatistics.bombardStrength == 0 && entry.unitStatistics.capacity == 0 &&
                        entry.immobileAbility && entry.flagUnitAbility && !entry.disband
                    )
            ) {
                issue(
                    "flagUnitStrategy",
                    "attack=0 (${entry.unitStatistics.attack}), defense=0 (${entry.unitStatistics.defense}), " +
                        "bombardStrength=0 (${entry.unitStatistics.bombardStrength}), " +
                        "capacity=0 (${entry.unitStatistics.capacity}), " +
                        "immobileAbility (${entry.immobileAbility}), flagUnitAbility (${entry.flagUnitAbility}), " +
                        "no disband (${entry.disband})",
                )
            } else {
                null
            },
            if (entry.exploreStrategy && entry.immobileAbility) {
                issue("exploreStrategy", "no immobileAbility (${entry.immobileAbility})")
            } else {
                null
            },
            if (entry.terraformStrategy &&
                !(
                    entry.buildColony && entry.buildRoad && entry.buildRailroad && entry.buildFort &&
                        entry.buildMine && entry.irrigate && entry.clearForest && entry.clearJungle &&
                        entry.plantForest && entry.clearPollution && entry.automate && entry.joinCity
                    )
            ) {
                issue(
                    "terraformStrategy",
                    "buildColony (${entry.buildColony}), buildRoad (${entry.buildRoad}), " +
                        "buildRailroad (${entry.buildRailroad}), buildFort (${entry.buildFort}), " +
                        "buildMine (${entry.buildMine}), irrigate (${entry.irrigate}), " +
                        "clearForest (${entry.clearForest}), clearJungle (${entry.clearJungle}), " +
                        "plantForest (${entry.plantForest}), clearPollution (${entry.clearPollution}), " +
                        "automate (${entry.automate}), joinCity (${entry.joinCity})",
                )
            } else {
                null
            },
            if (entry.settleStrategy && !(entry.load && entry.buildCity && entry.joinCity)) {
                issue(
                    "settleStrategy",
                    "load (${entry.load}), buildCity (${entry.buildCity}), joinCity (${entry.joinCity})",
                )
            } else {
                null
            },
            if (entry.armyStrategy && !(entry.load && entry.armyAbility)) {
                issue("armyStrategy", "load (${entry.load}), armyAbility (${entry.armyAbility})")
            } else {
                null
            },
            if (entry.leaderStrategy && !(entry.buildArmy && entry.finishImprovements)) {
                issue(
                    "leaderStrategy",
                    "buildArmy (${entry.buildArmy}), finishImprovements (${entry.finishImprovements})",
                )
            } else {
                null
            },
            if (entry.kingStrategy && !(entry.kingAbility && !entry.disband)) {
                issue("kingStrategy", "kingAbility (${entry.kingAbility}), no disband (${entry.disband})")
            } else {
                null
            },
        )
    }
}

/**
 * Flags a [PrtoDomain.SEA] [PrtoEntry] whose AI Strategy checkbox is set despite failing that
 * checkbox's own real Units editor prerequisites. Returns no issues if the `PRTO` section is
 * absent from [file], and skips every entry whose [PrtoEntry.domainEnum] isn't [PrtoDomain.SEA]
 * (Land and Air strategies have their own, different prerequisites; see
 * [validatePrtoLandStrategyPrerequisites]).
 *
 * The real Units editor grays out each Sea AI Strategy checkbox until its unit meets that
 * checkbox's own prerequisites:
 * - [navalPowerStrategy]: [PrtoEntry.unitStatistics]'s `attack`>0, `defense`>0.
 * - [navalTransportStrategy]: [unload].
 * - [navalCarrierStrategy]: [transportsOnlyAircraftAbility], [unload].
 * - [navalMissileTransportStrategy]: [transportsOnlyTacticalMissilesAbility], [unload].
 */
fun validatePrtoSeaStrategyPrerequisites(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<PrtoSection>().singleOrNull() ?: return emptyList()
    return section.entries.flatMapIndexed { index, entry ->
        if (entry.domainEnum != PrtoDomain.SEA) return@flatMapIndexed emptyList()

        fun issue(field: String, requirement: String) = ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.PRTO,
            index,
            field,
            "$field is set but requires $requirement",
        )

        listOfNotNull(
            if (entry.navalPowerStrategy && !(entry.unitStatistics.attack > 0 && entry.unitStatistics.defense > 0)) {
                issue(
                    "navalPowerStrategy",
                    "attack>0 (${entry.unitStatistics.attack}), defense>0 (${entry.unitStatistics.defense})",
                )
            } else {
                null
            },
            if (entry.navalTransportStrategy && !entry.unload) {
                issue("navalTransportStrategy", "unload (${entry.unload})")
            } else {
                null
            },
            if (entry.navalCarrierStrategy && !(entry.transportsOnlyAircraftAbility && entry.unload)) {
                issue(
                    "navalCarrierStrategy",
                    "transportsOnlyAircraftAbility (${entry.transportsOnlyAircraftAbility}), " +
                        "unload (${entry.unload})",
                )
            } else {
                null
            },
            if (entry.navalMissileTransportStrategy &&
                !(entry.transportsOnlyTacticalMissilesAbility && entry.unload)
            ) {
                issue(
                    "navalMissileTransportStrategy",
                    "transportsOnlyTacticalMissilesAbility (${entry.transportsOnlyTacticalMissilesAbility}), " +
                        "unload (${entry.unload})",
                )
            } else {
                null
            },
        )
    }
}

/**
 * Flags a [PrtoDomain.AIR] [PrtoEntry] whose AI Strategy checkbox is set despite failing that
 * checkbox's own real Units editor prerequisites. Returns no issues if the `PRTO` section is
 * absent from [file], and skips every entry whose [PrtoEntry.domainEnum] isn't [PrtoDomain.AIR]
 * (Land and Sea strategies have their own, different prerequisites; see
 * [validatePrtoLandStrategyPrerequisites]).
 *
 * The real Units editor grays out each Air AI Strategy checkbox until its unit meets that
 * checkbox's own prerequisites:
 * - [airBombardStrategy]: [PrtoEntry.unitStatistics]'s `bombardStrength`>0, `operationalRange`>0,
 *   and either [bombing] or [precisionBombing].
 * - [airDefenseStrategy]: [PrtoEntry.unitStatistics]'s `attack`>0, `operationalRange`>0,
 *   [interception].
 * - [airTransportStrategy]: [PrtoEntry.unitStatistics]'s `operationalRange`>0, [airdrop],
 *   [unload].
 */
fun validatePrtoAirStrategyPrerequisites(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<PrtoSection>().singleOrNull() ?: return emptyList()
    return section.entries.flatMapIndexed { index, entry ->
        if (entry.domainEnum != PrtoDomain.AIR) return@flatMapIndexed emptyList()

        fun issue(field: String, requirement: String) = ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.PRTO,
            index,
            field,
            "$field is set but requires $requirement",
        )

        listOfNotNull(
            if (entry.airBombardStrategy &&
                !(
                    entry.unitStatistics.bombardStrength > 0 && entry.unitStatistics.operationalRange > 0 &&
                        (entry.bombing || entry.precisionBombing)
                    )
            ) {
                issue(
                    "airBombardStrategy",
                    "bombardStrength>0 (${entry.unitStatistics.bombardStrength}), " +
                        "operationalRange>0 (${entry.unitStatistics.operationalRange}), " +
                        "bombing or precisionBombing (${entry.bombing} / ${entry.precisionBombing})",
                )
            } else {
                null
            },
            if (entry.airDefenseStrategy &&
                !(entry.unitStatistics.attack > 0 && entry.unitStatistics.operationalRange > 0 && entry.interception)
            ) {
                issue(
                    "airDefenseStrategy",
                    "attack>0 (${entry.unitStatistics.attack}), " +
                        "operationalRange>0 (${entry.unitStatistics.operationalRange}), " +
                        "interception (${entry.interception})",
                )
            } else {
                null
            },
            if (entry.airTransportStrategy &&
                !(entry.unitStatistics.operationalRange > 0 && entry.airdrop && entry.unload)
            ) {
                issue(
                    "airTransportStrategy",
                    "operationalRange>0 (${entry.unitStatistics.operationalRange}), airdrop (${entry.airdrop}), " +
                        "unload (${entry.unload})",
                )
            } else {
                null
            },
        )
    }
}
