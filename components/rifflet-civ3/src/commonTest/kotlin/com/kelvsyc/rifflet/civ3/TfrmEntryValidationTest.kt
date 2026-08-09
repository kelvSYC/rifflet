package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationSeverity
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private fun goodEntryForBonusCheck(type: GoodResourceType): GoodEntry = GoodEntry(
    name = "", civilopediaEntry = "", type = type, appearanceRatio = 0,
    disappearanceProbability = 0, icon = 0, prerequisite = -1,
    foodBonus = 0, shieldsBonus = 0, commerceBonus = 0,
)

private fun tfrmEntryForBonusCheck(requiredResource1: Int = -1, requiredResource2: Int = -1): TfrmEntry = TfrmEntry(
    name = "", civilopediaEntry = "", turnsToComplete = 0, required = -1,
    requiredResource1 = requiredResource1, requiredResource2 = requiredResource2, order = "",
)

private fun fileWithGoodsAndTfrm(goods: List<GoodEntry>, tfrmEntries: List<TfrmEntry>): Civ3File = Civ3File(
    Civ3Header(major = 12, minor = 0, description = "", title = ""),
    listOf(GoodSection(goods), TfrmSection(tfrmEntries)),
)

class TfrmEntryValidationTest : FunSpec({

    test("validateTfrmRequiredResourceNotBonus returns no issues when both required resources are Luxury/Strategic") {
        val goods = listOf(goodEntryForBonusCheck(GoodResourceType.LUXURY), goodEntryForBonusCheck(GoodResourceType.STRATEGIC))
        val entry = tfrmEntryForBonusCheck(requiredResource1 = 0, requiredResource2 = 1)
        val file = fileWithGoodsAndTfrm(goods, listOf(entry))

        validateTfrmRequiredResourceNotBonus(file) shouldBe emptyList()
    }

    test("validateTfrmRequiredResourceNotBonus flags a Bonus-type requiredResource1") {
        val goods = listOf(goodEntryForBonusCheck(GoodResourceType.BONUS))
        val entry = tfrmEntryForBonusCheck(requiredResource1 = 0, requiredResource2 = -1)
        val file = fileWithGoodsAndTfrm(goods, listOf(entry))

        validateTfrmRequiredResourceNotBonus(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TFRM,
                0,
                "requiredResource1",
                "a required resource must be Luxury or Strategic, not Bonus (requiredResource1)",
            ),
        )
    }

    test("validateTfrmRequiredResourceNotBonus flags both fields when both are Bonus-type (including the same resource twice)") {
        val goods = listOf(goodEntryForBonusCheck(GoodResourceType.BONUS))
        val entry = tfrmEntryForBonusCheck(requiredResource1 = 0, requiredResource2 = 0)
        val file = fileWithGoodsAndTfrm(goods, listOf(entry))

        validateTfrmRequiredResourceNotBonus(file) shouldBe listOf(
            ValidationIssue(
                ValidationSeverity.ERROR,
                Civ3SectionIds.TFRM,
                0,
                "requiredResource1, requiredResource2",
                "a required resource must be Luxury or Strategic, not Bonus (requiredResource1, requiredResource2)",
            ),
        )
    }

    test("validateTfrmRequiredResourceNotBonus returns no issues when the GOOD section is absent") {
        val file = Civ3File(
            Civ3Header(major = 12, minor = 0, description = "", title = ""),
            sections = listOf(TfrmSection(listOf(tfrmEntryForBonusCheck(requiredResource1 = 0)))),
        )

        validateTfrmRequiredResourceNotBonus(file) shouldBe emptyList()
    }

    test("validateTfrmRequiredResourceNotBonus returns no issues when the TFRM section is absent") {
        val file = Civ3File(Civ3Header(major = 12, minor = 0, description = "", title = ""), sections = emptyList())

        validateTfrmRequiredResourceNotBonus(file) shouldBe emptyList()
    }
})
