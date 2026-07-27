package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity

/**
 * Flags `RACE` entry 0 (the barbarian placeholder — see [RaceSection]'s own KDoc) if it deviates
 * from the fixed shape the Rules Editor locks it to. Returns no issues if the `RACE` section is
 * absent or empty in [file].
 *
 * Every real file's barbarian entry has no traits ([RaceEntry.bonuses]), no Flavor membership
 * ([RaceEntry.flavors]), no free techs, no leader (name, title, or Great/Scientific Leader name
 * pools), default personality settings (aggression level, leader/civilization gender), no era
 * animation filenames, no Culture Group, and [RaceGovernor.settings] set to exactly
 * [manageCitizens] and [manageProduction] — every other governor setting is locked off. On
 * [Civ3FormatEra.PTW]/[Civ3FormatEra.CONQUESTS] files, where the King unit mechanic exists, it
 * also has no [RaceEntry.unitTypeForKing]; [Civ3FormatEra.VANILLA] predates King units entirely
 * (Regicide was introduced in PTW), so that specific check is skipped there.
 */
fun validateRaceBarbarianPlaceholder(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<RaceSection>().singleOrNull() ?: return emptyList()
    val entry = section.entries.firstOrNull() ?: return emptyList()
    val era = file.header.formatEra

    fun issue(field: String, requirement: String) = ValidationIssue(
        ValidationSeverity.ERROR,
        Civ3SectionIds.RACE,
        0,
        field,
        "the barbarian placeholder is expected to have $requirement",
    )

    return listOfNotNull(
        if (entry.bonuses != 0) issue("bonuses", "no traits (bonuses=0, was ${entry.bonuses})") else null,
        if (entry.flavors != 0) issue("flavors", "no Flavor membership (flavors=0, was ${entry.flavors})") else null,
        if (entry.freeTechs.any { it != -1 }) {
            issue("freeTechs", "no free techs (freeTechs=${entry.freeTechs})")
        } else {
            null
        },
        if (entry.leader.name.isNotBlank() ||
            entry.leader.title.isNotBlank() ||
            entry.greatLeaderNames.any { it.isNotBlank() } ||
            entry.scientificLeaderNames.any { it.isNotBlank() }
        ) {
            issue(
                "leaderName/leaderTitle/greatLeaderNames/scientificLeaderNames",
                "no leader (leaderName='${entry.leader.name}', leaderTitle='${entry.leader.title}', " +
                    "greatLeaderNames=${entry.greatLeaderNames}, scientificLeaderNames=${entry.scientificLeaderNames})",
            )
        } else {
            null
        },
        if (entry.personality.aggressionLevel != 0 || entry.leader.gender != 0 || entry.civilizationGender != 0) {
            issue(
                "aggressionLevel/leaderGender/civilizationGender",
                "default personality settings (aggressionLevel=${entry.personality.aggressionLevel}, " +
                    "leaderGender=${entry.leader.gender}, civilizationGender=${entry.civilizationGender})",
            )
        } else {
            null
        },
        if (entry.eras.any { it.forwardFilename.isNotBlank() || it.reverseFilename.isNotBlank() }) {
            issue("eras", "no era animation filenames (eras=${entry.eras})")
        } else {
            null
        },
        if (entry.cultureGroupEnum != RaceCultureGroup.NONE) {
            issue("cultureGroup", "no Culture Group (cultureGroup=-1, was ${entry.cultureGroup})")
        } else {
            null
        },
        if (entry.governor.settings != ((1 shl 0) or (1 shl 4))) {
            issue(
                "governorSettings",
                "only manageCitizens and manageProduction enabled (governorSettings=17, " +
                    "was ${entry.governor.settings})",
            )
        } else {
            null
        },
        if (era != Civ3FormatEra.VANILLA && entry.unitTypeForKing != -1) {
            issue("unitTypeForKing", "no King unit (unitTypeForKing=-1, was ${entry.unitTypeForKing})")
        } else {
            null
        },
    )
}

/**
 * Flags a [RaceEntry] whose [RaceEntry.cultureGroup] doesn't decode into a [RaceCultureGroup].
 * Returns no issues if the `RACE` section is absent from [file].
 *
 * Every real official file's civilizations (barbarian placeholder included) have a `cultureGroup`
 * value in the documented -1..4 range, with zero exceptions.
 */
fun validateRaceCultureGroup(file: Civ3File): List<ValidationIssue> {
    val section = file.sections.filterIsInstance<RaceSection>().singleOrNull() ?: return emptyList()
    return section.entries.mapIndexedNotNull { index, entry ->
        if (entry.cultureGroupEnum != null) {
            null
        } else {
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.RACE,
                index,
                "cultureGroup",
                "cultureGroup=${entry.cultureGroup} is not a valid RaceCultureGroup index (-1..4)",
            )
        }
    }
}
