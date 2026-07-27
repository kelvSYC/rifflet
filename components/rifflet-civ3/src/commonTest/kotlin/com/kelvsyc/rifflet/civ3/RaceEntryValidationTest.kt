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
    leaderGender: Int = 0,
    civilizationGender: Int = 0,
    eras: List<RaceEraFilenames> = emptyList(),
    cultureGroup: Int = -1,
    governorSettings: Int = (1 shl 0) or (1 shl 4),
    unitTypeForKing: Int = -1,
): RaceEntry = RaceEntry(
    cityNames = emptyList(),
    greatLeaderNames = greatLeaderNames,
    leaderName = leaderName,
    leaderTitle = leaderTitle,
    civilopediaEntry = "",
    adjective = "",
    name = "A Barbarian Chiefdom",
    noun = "",
    eras = eras,
    cultureGroup = cultureGroup,
    leaderGender = leaderGender,
    civilizationGender = civilizationGender,
    aggressionLevel = aggressionLevel,
    uniqueCivilizationCounter = 0,
    shunnedGovernment = -1,
    favoriteGovernment = -1,
    defaultColor = 0,
    uniqueColor = 0,
    freeTech1 = freeTech1,
    freeTech2 = freeTech2,
    freeTech3 = freeTech3,
    freeTech4 = freeTech4,
    bonuses = bonuses,
    governorSettings = governorSettings,
    buildNever = 0,
    buildOften = 0,
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
                "freeTech1/freeTech2/freeTech3/freeTech4",
                "the barbarian placeholder is expected to have no free techs (freeTech1=5, freeTech2=-1, " +
                    "freeTech3=-1, freeTech4=-1)",
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
                    "(aggressionLevel=5, leaderGender=0, civilizationGender=0)",
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
        val file = fileWithRaces(listOf(barbarianRaceEntry(cultureGroup = 3)))

        validateRaceBarbarianPlaceholder(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.RACE,
                0,
                "cultureGroup",
                "the barbarian placeholder is expected to have no Culture Group (cultureGroup=-1, was 3)",
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

    test("returns no issues for every documented cultureGroup value (-1..4)") {
        val file = fileWithRaces((-1..4).map { barbarianRaceEntry(cultureGroup = it) })

        validateRaceCultureGroup(file) shouldBe emptyList()
    }

    test("flags a cultureGroup value outside the documented -1..4 range") {
        val file = fileWithRaces(listOf(barbarianRaceEntry(cultureGroup = 5)))

        validateRaceCultureGroup(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.RACE,
                0,
                "cultureGroup",
                "cultureGroup=5 is not a valid RaceCultureGroup index (-1..4)",
            ),
        )
    }

    test("returns no issues for cultureGroup bounds when RACE is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateRaceCultureGroup(file) shouldBe emptyList()
    }
})
