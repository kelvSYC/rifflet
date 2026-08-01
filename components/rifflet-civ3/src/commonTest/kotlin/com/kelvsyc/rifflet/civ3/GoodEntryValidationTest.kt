package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun goodEntry(
    type: Int = 0,
    appearanceRatio: Int = 0,
    disappearanceProbability: Int = 0,
): GoodEntry = GoodEntry(
    name = "",
    civilopediaEntry = "",
    type = type,
    appearanceRatio = appearanceRatio,
    disappearanceProbability = disappearanceProbability,
    icon = 0,
    prerequisite = -1,
    foodBonus = 0,
    shieldsBonus = 0,
    commerceBonus = 0,
)

private fun fileWithGoods(entries: List<GoodEntry>): Civ3File = Civ3File(
    Civ3Header(major = 12, minor = 0, description = "", title = ""),
    listOf(GoodSection(entries)),
)

class GoodEntryValidationTest : FunSpec({

    test("returns no issues for a Bonus Resource with both fields disabled") {
        val file = fileWithGoods(listOf(goodEntry(type = 0, appearanceRatio = 0, disappearanceProbability = 0)))

        validateGoodBonusResourceDisabledFields(file) shouldBe emptyList()
    }

    test("returns no issues for Luxury/Strategic entries regardless of appearance/disappearance values") {
        val file = fileWithGoods(
            listOf(
                goodEntry(type = 1, appearanceRatio = 100, disappearanceProbability = 250),
                goodEntry(type = 2, appearanceRatio = 0, disappearanceProbability = 0),
            ),
        )

        validateGoodBonusResourceDisabledFields(file) shouldBe emptyList()
    }

    test("flags a Bonus Resource with a nonzero appearanceRatio") {
        val file = fileWithGoods(listOf(goodEntry(type = 0, appearanceRatio = 100)))

        validateGoodBonusResourceDisabledFields(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.GOOD,
                0,
                "appearanceRatio/disappearanceProbability",
                "a Bonus Resource is expected to have both fields disabled (appearanceRatio=0, " +
                    "disappearanceProbability=0), was appearanceRatio=100, disappearanceProbability=0",
            ),
        )
    }

    test("flags a Bonus Resource with a nonzero disappearanceProbability") {
        val file = fileWithGoods(listOf(goodEntry(type = 0, disappearanceProbability = 250)))

        validateGoodBonusResourceDisabledFields(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.GOOD,
                0,
                "appearanceRatio/disappearanceProbability",
                "a Bonus Resource is expected to have both fields disabled (appearanceRatio=0, " +
                    "disappearanceProbability=0), was appearanceRatio=0, disappearanceProbability=250",
            ),
        )
    }

    test("returns no issues when GOOD is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateGoodBonusResourceDisabledFields(file) shouldBe emptyList()
    }
})
