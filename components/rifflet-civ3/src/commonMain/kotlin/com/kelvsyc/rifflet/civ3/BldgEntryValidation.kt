package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity

/**
 * Flags a `BLDG` section with more than one [BldgEntry.centerOfEmpire] entry. Returns no issues
 * if the `BLDG` section is absent from [file].
 *
 * Every real official file has at most one building with this effect (the Palace) — the Rules
 * Editor's own "Center of Empire" checkbox has no mechanism to support more than one true capital
 * per civilization.
 */
fun validateBldgSingleCenterOfEmpire(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<BldgSection>().singleOrNull() ?: return emptyList()
    val count = section.entries.count { it.centerOfEmpire }
    if (count <= 1) return emptyList()
    return listOf(
        ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.BLDG,
            null,
            "centerOfEmpire",
            "$count entries have centerOfEmpire set; at most one is expected",
        ),
    )
}

/**
 * Flags a [BldgEntry] whose Building Type is plain Improvement (neither [BldgEntry.wonder] nor
 * [BldgEntry.smallWonder]) but which still carries a Wonder or Small Wonder effect. Returns no
 * issues if the `BLDG` section is absent from [file].
 *
 * The Rules Editor only exposes the [BldgEntry.wonders]/[BldgEntry.smallWonders] checkbox grid,
 * [BldgEntry.doublesHappiness], [BldgEntry.gainInEveryCity], and
 * [BldgEntry.gainInEveryCityOnContinent] when Building Type is Wonder or Small Wonder.
 */
fun validateBldgImprovementHasNoWonderEffects(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<BldgSection>().singleOrNull() ?: return emptyList()
    return section.entries.flatMapIndexed { index, entry ->
        if (entry.wonder || entry.smallWonder) return@flatMapIndexed emptyList()

        fun issue(field: String, value: Int) = ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.BLDG,
            index,
            field,
            "$field=$value is set on a plain Improvement, which the Rules Editor never allows",
        )

        listOfNotNull(
            if (entry.wonders != 0) issue("wonders", entry.wonders) else null,
            if (entry.smallWonders != 0) issue("smallWonders", entry.smallWonders) else null,
            if (entry.doublesHappiness != -1) issue("doublesHappiness", entry.doublesHappiness) else null,
            if (entry.gainInEveryCity != -1) issue("gainInEveryCity", entry.gainInEveryCity) else null,
            if (entry.gainInEveryCityOnContinent != -1) {
                issue("gainInEveryCityOnContinent", entry.gainInEveryCityOnContinent)
            } else {
                null
            },
        )
    }
}

/**
 * Flags a [BldgEntry.smallWonder] entry that sets [BldgEntry.doublesHappiness],
 * [BldgEntry.gainInEveryCity], or [BldgEntry.gainInEveryCityOnContinent]. Returns no issues if
 * the `BLDG` section is absent from [file].
 *
 * The Rules Editor only offers these three fields on full Wonders, not Small Wonders, even though
 * both Building Types share the same Wonder/Small Wonder checkbox grid.
 */
fun validateBldgSmallWonderHasNoHappinessReference(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<BldgSection>().singleOrNull() ?: return emptyList()
    return section.entries.flatMapIndexed { index, entry ->
        if (!entry.smallWonder) return@flatMapIndexed emptyList()

        fun issue(field: String, value: Int) = ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.BLDG,
            index,
            field,
            "$field=$value is set on a Small Wonder, which the Rules Editor never allows",
        )

        listOfNotNull(
            if (entry.doublesHappiness != -1) issue("doublesHappiness", entry.doublesHappiness) else null,
            if (entry.gainInEveryCity != -1) issue("gainInEveryCity", entry.gainInEveryCity) else null,
            if (entry.gainInEveryCityOnContinent != -1) {
                issue("gainInEveryCityOnContinent", entry.gainInEveryCityOnContinent)
            } else {
                null
            },
        )
    }
}

/**
 * Flags a [BldgEntry] that produces a spaceship part ([BldgEntry.spaceshipPart] `!= -1`) but
 * doesn't otherwise match the real Spaceship Parts group's structural constraints. Returns no
 * issues if the `BLDG` section is absent from [file].
 *
 * Every real official spaceship part is a plain Improvement (not a Wonder or Small Wonder),
 * requires exactly 1 of itself ([BldgEntry.numberOfRequiredBuildings]), has every combat value at
 * `0`, carries no [BldgEntry.numberOfArmiesRequired], renders no other building obsolete, and (on
 * [Civ3FormatEra.CONQUESTS] files, where [BldgUnitsProduced.unitProduced] is real data) produces
 * no unit.
 */
fun validateBldgSpaceshipPartInvariants(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<BldgSection>().singleOrNull() ?: return emptyList()
    val era = file.header.formatEra
    return section.entries.flatMapIndexed { index, entry ->
        if (entry.spaceshipPart == -1) return@flatMapIndexed emptyList()

        fun issue(field: String, requirement: String) = ValidationIssue(
            ValidationSeverity.ERROR,
            Civ3SectionIds.BLDG,
            index,
            field,
            "spaceshipPart is set but requires $requirement",
        )

        listOfNotNull(
            if (entry.wonder || entry.smallWonder) {
                issue("spaceshipPart", "Building Type Improvement (not Wonder or Small Wonder)")
            } else {
                null
            },
            if (entry.numberOfRequiredBuildings != 1) {
                issue(
                    "numberOfRequiredBuildings",
                    "numberOfRequiredBuildings=1 (was ${entry.numberOfRequiredBuildings})",
                )
            } else {
                null
            },
            if (!(
                    entry.combatValues.bombardDefense == 0 && entry.combatValues.navalBombardDefense == 0 &&
                        entry.combatValues.defenseBonus == 0 && entry.navalDefenseBonus == 0 &&
                        entry.combatValues.airPower == 0 && entry.combatValues.navalPower == 0
                    )
            ) {
                issue(
                    "combat values",
                    "bombardDefense=0 (${entry.combatValues.bombardDefense}), navalBombardDefense=0 " +
                        "(${entry.combatValues.navalBombardDefense}), defenseBonus=0 (${entry.combatValues.defenseBonus}), " +
                        "navalDefenseBonus=0 (${entry.navalDefenseBonus}), airPower=0 (${entry.combatValues.airPower}), " +
                        "navalPower=0 (${entry.combatValues.navalPower})",
                )
            } else {
                null
            },
            if (entry.wonders != 0 || entry.smallWonders != 0) {
                issue(
                    "wonders/smallWonders",
                    "wonders=0 (${entry.wonders}), smallWonders=0 (${entry.smallWonders})",
                )
            } else {
                null
            },
            if (entry.numberOfArmiesRequired != 0) {
                issue("numberOfArmiesRequired", "numberOfArmiesRequired=0 (was ${entry.numberOfArmiesRequired})")
            } else {
                null
            },
            if (entry.renderedObsoleteBy != -1) {
                issue("renderedObsoleteBy", "renderedObsoleteBy=-1 (was ${entry.renderedObsoleteBy})")
            } else {
                null
            },
            if (era == Civ3FormatEra.CONQUESTS && entry.unitsProduced?.unitProduced != -1) {
                issue("unitProduced", "unitProduced=-1 (was ${entry.unitsProduced?.unitProduced})")
            } else {
                null
            },
        )
    }
}

/**
 * Flags a [BldgEntry] that produces a spaceship part ([BldgEntry.spaceshipPart] `!= -1`) but
 * carries stats or flags a spaceship part conventionally doesn't. Returns no issues if the `BLDG`
 * section is absent from [file].
 *
 * Every real official spaceship part has `0` maintenance/culture/production/pollution, no
 * happiness effect, no [BldgEntry.improvements] flags, and no [BldgEntry.otherCharacteristics]
 * flags other than [BldgEntry.agricultural]/[BldgEntry.seafaring] (which the Rules Editor lets a
 * spaceship part carry despite them being meaningless there — likely a Conquests editor quirk).
 * Unlike [validateBldgSpaceshipPartInvariants]'s structural checks, this is only a
 * [ValidationSeverity.WARNING]: one real official PTW scenario repurposes several spaceship-part
 * buildings with genuine stats and flags of their own, set before the Rules Editor locked those
 * fields against further editing. Whether the game engine actually honors those stats on a
 * spaceship-part building at runtime, rather than silently ignoring them, is unconfirmed and
 * needs further verification — no community documentation of this specific case is known.
 */
fun validateBldgSpaceshipPartConventionalStats(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<BldgSection>().singleOrNull() ?: return emptyList()
    return section.entries.flatMapIndexed { index, entry ->
        if (entry.spaceshipPart == -1) return@flatMapIndexed emptyList()
        val allowedCharacteristicBits = (1 shl 10) or (1 shl 11) // agricultural, seafaring

        fun issue(field: String, requirement: String) = ValidationIssue(
            ValidationSeverity.WARNING,
            Civ3SectionIds.BLDG,
            index,
            field,
            "spaceshipPart is set but conventionally has $requirement",
        )

        listOfNotNull(
            if (!(entry.maintenanceCost == 0 && entry.culture == 0 && entry.production == 0 && entry.pollution == 0)) {
                issue(
                    "maintenanceCost/culture/production/pollution",
                    "maintenanceCost=0 (${entry.maintenanceCost}), culture=0 (${entry.culture}), " +
                        "production=0 (${entry.production}), pollution=0 (${entry.pollution})",
                )
            } else {
                null
            },
            if (!(
                    entry.happiness.contentFacesAllCities == 0 && entry.happiness.contentFaces == 0 &&
                        entry.happiness.unhappyFacesAllCities == 0 && entry.happiness.unhappyFaces == 0 &&
                        entry.doublesHappiness == -1 && entry.gainInEveryCity == -1 &&
                        entry.gainInEveryCityOnContinent == -1
                    )
            ) {
                issue(
                    "happiness settings",
                    "no happiness effect (contentFacesAllCities=${entry.happiness.contentFacesAllCities}, " +
                        "contentFaces=${entry.happiness.contentFaces}, " +
                        "unhappyFacesAllCities=${entry.happiness.unhappyFacesAllCities}, " +
                        "unhappyFaces=${entry.happiness.unhappyFaces}, doublesHappiness=${entry.doublesHappiness}, " +
                        "gainInEveryCity=${entry.gainInEveryCity}, " +
                        "gainInEveryCityOnContinent=${entry.gainInEveryCityOnContinent})",
                )
            } else {
                null
            },
            if ((entry.otherCharacteristics and allowedCharacteristicBits.inv()) != 0) {
                issue(
                    "otherCharacteristics",
                    "no characteristic flags other than agricultural/seafaring " +
                        "(otherCharacteristics=${entry.otherCharacteristics})",
                )
            } else {
                null
            },
            if (entry.improvements != 0) {
                issue("improvements", "no improvement flags (improvements=${entry.improvements})")
            } else {
                null
            },
        )
    }
}
