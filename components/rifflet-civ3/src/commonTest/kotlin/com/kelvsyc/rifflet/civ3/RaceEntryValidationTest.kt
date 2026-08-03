package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okio.ByteString

private fun barbarianRaceEntry(
    bonuses: Int = 0,
    flavors: Int = 0,
    freeTech1: Int = -1,
    freeTech2: Int = -1,
    freeTech3: Int = -1,
    freeTech4: Int = -1,
    leaderName: String = "",
    leaderTitle: String = "",
    greatLeaderNames: List<String> = emptyList(),
    scientificLeaderNames: List<String> = emptyList(),
    aggressionLevel: Int = 0,
    leaderGender: Gender = Gender.MALE,
    civilizationGender: Gender = Gender.MALE,
    eras: List<RaceEraFilenames> = emptyList(),
    cultureGroup: RaceCultureGroup = RaceCultureGroup.NONE,
    governorSettings: Int = (1 shl 0) or (1 shl 4),
    unitTypeForKing: Int = -1,
): RaceEntry = RaceEntry(
    cityNames = emptyList(),
    greatLeaderNames = greatLeaderNames,
    leader = RaceLeader(name = leaderName, title = leaderTitle, gender = leaderGender),
    civilopediaEntry = "",
    adjective = "",
    name = "A Barbarian Chiefdom",
    noun = "",
    eras = eras,
    cultureGroup = cultureGroup,
    civilizationGender = civilizationGender,
    personality = RacePersonality(favoriteGovernment = -1, shunnedGovernment = -1, aggressionLevel = aggressionLevel),
    uniqueCivilizationCounter = 0,
    defaultColor = 0,
    uniqueColor = 0,
    freeTechs = listOf(freeTech1, freeTech2, freeTech3, freeTech4),
    bonuses = bonuses,
    governor = RaceGovernor(settings = governorSettings, buildNever = 0, buildOften = 0),
    plurality = 1,
    unitTypeForKing = unitTypeForKing,
    flavors = flavors,
    unknown = ByteString.of(0, 0, 0, 0),
    diplomacyTextIndex = 0,
    scientificLeaderNames = scientificLeaderNames,
)

private fun fileWithRaces(entries: List<RaceEntry>, major: Int = 12): Civ3File = Civ3File(
    Civ3Header(major = major, minor = 0, description = "", title = ""),
    listOf(RaceSection(entries)),
)

class RaceEntryValidationTest : FunSpec({

    test("returns no issues for a well-formed barbarian placeholder") {
        val file = fileWithRaces(listOf(barbarianRaceEntry()))

        validateRaceBarbarianPlaceholder(file) shouldBe emptyList()
    }

    test("returns no issues when RACE is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateRaceBarbarianPlaceholder(file) shouldBe emptyList()
    }

    test("returns no issues when RACE is empty") {
        val file = fileWithRaces(emptyList())

        validateRaceBarbarianPlaceholder(file) shouldBe emptyList()
    }

    test("flags a nonzero bonuses value") {
        val file = fileWithRaces(listOf(barbarianRaceEntry(bonuses = 1)))

        validateRaceBarbarianPlaceholder(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.RACE,
                0,
                "bonuses",
                "the barbarian placeholder is expected to have no traits (bonuses=0, was 1)",
            ),
        )
    }

    test("flags a nonzero flavors value") {
        val file = fileWithRaces(listOf(barbarianRaceEntry(flavors = 1)))

        validateRaceBarbarianPlaceholder(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.RACE,
                0,
                "flavors",
                "the barbarian placeholder is expected to have no Flavor membership (flavors=0, was 1)",
            ),
        )
    }

    test("flags a free tech") {
        val file = fileWithRaces(listOf(barbarianRaceEntry(freeTech1 = 5)))

        validateRaceBarbarianPlaceholder(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.RACE,
                0,
                "freeTechs",
                "the barbarian placeholder is expected to have no free techs (freeTechs=[5, -1, -1, -1])",
            ),
        )
    }

    test("flags a leader name") {
        val file = fileWithRaces(listOf(barbarianRaceEntry(leaderName = "Attila")))

        validateRaceBarbarianPlaceholder(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.RACE,
                0,
                "leaderName/leaderTitle/greatLeaderNames/scientificLeaderNames",
                "the barbarian placeholder is expected to have no leader (leaderName='Attila', " +
                    "leaderTitle='', greatLeaderNames=[], scientificLeaderNames=[])",
            ),
        )
    }

    test("flags a nonzero personality setting") {
        val file = fileWithRaces(listOf(barbarianRaceEntry(aggressionLevel = 5)))

        validateRaceBarbarianPlaceholder(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.RACE,
                0,
                "aggressionLevel/leaderGender/civilizationGender",
                "the barbarian placeholder is expected to have default personality settings " +
                    "(aggressionLevel=5, leaderGender=MALE, civilizationGender=MALE)",
            ),
        )
    }

    test("flags a nonblank era animation filename") {
        val file = fileWithRaces(listOf(barbarianRaceEntry(eras = listOf(RaceEraFilenames("x", "")))))

        validateRaceBarbarianPlaceholder(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.RACE,
                0,
                "eras",
                "the barbarian placeholder is expected to have no era animation filenames " +
                    "(eras=[RaceEraFilenames(forwardFilename=x, reverseFilename=)])",
            ),
        )
    }

    test("flags a cultureGroup other than -1") {
        val file = fileWithRaces(listOf(barbarianRaceEntry(cultureGroup = RaceCultureGroup.MID_EAST)))

        validateRaceBarbarianPlaceholder(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.RACE,
                0,
                "cultureGroup",
                "the barbarian placeholder is expected to have no Culture Group (cultureGroup=-1, was MID_EAST)",
            ),
        )
    }

    test("flags a governorSettings other than manageCitizens|manageProduction") {
        val file = fileWithRaces(listOf(barbarianRaceEntry(governorSettings = 0)))

        validateRaceBarbarianPlaceholder(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.RACE,
                0,
                "governorSettings",
                "the barbarian placeholder is expected to have only manageCitizens and manageProduction " +
                    "enabled (governorSettings=17, was 0)",
            ),
        )
    }

    test("flags a unitTypeForKing on a Conquests file") {
        val file = fileWithRaces(listOf(barbarianRaceEntry(unitTypeForKing = 5)), major = 12)

        validateRaceBarbarianPlaceholder(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.RACE,
                0,
                "unitTypeForKing",
                "the barbarian placeholder is expected to have no King unit (unitTypeForKing=-1, was 5)",
            ),
        )
    }

    test("flags a unitTypeForKing on a PTW file") {
        val file = fileWithRaces(listOf(barbarianRaceEntry(unitTypeForKing = 5)), major = 11)

        validateRaceBarbarianPlaceholder(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.RACE,
                0,
                "unitTypeForKing",
                "the barbarian placeholder is expected to have no King unit (unitTypeForKing=-1, was 5)",
            ),
        )
    }

    test("does not check unitTypeForKing on a vanilla file") {
        val file = fileWithRaces(listOf(barbarianRaceEntry(unitTypeForKing = 0)), major = 3)

        validateRaceBarbarianPlaceholder(file) shouldBe emptyList()
    }

    test("returns no issues for exactly 32 RACE entries") {
        val file = fileWithRaces(List(32) { barbarianRaceEntry() })

        validateRaceMaxCount(file) shouldBe emptyList()
    }

    test("flags a RACE section with more than 32 entries") {
        val file = fileWithRaces(List(33) { barbarianRaceEntry() })

        validateRaceMaxCount(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.RACE,
                null,
                "entries",
                "RACE has 33 entries; the format caps this at 32",
            ),
        )
    }

    test("returns no issues for RACE max count when RACE is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateRaceMaxCount(file) shouldBe emptyList()
    }

    test("returns no issues for exactly 2 RACE entries") {
        val file = fileWithRaces(List(2) { barbarianRaceEntry() })

        validateRaceMinCount(file) shouldBe emptyList()
    }

    test("flags a RACE section with fewer than 2 entries") {
        val file = fileWithRaces(listOf(barbarianRaceEntry()))

        validateRaceMinCount(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.RACE,
                null,
                "entries",
                "RACE has 1 entries; at least 2 are needed (the barbarian placeholder plus a playable civilization)",
            ),
        )
    }

    test("returns no issues for RACE min count when RACE is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateRaceMinCount(file) shouldBe emptyList()
    }
})
