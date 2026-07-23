package com.kelvsyc.rifflet.civ3.validation

import com.kelvsyc.rifflet.civ3.Civ3SectionIds
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ValidationIssueTest : FunSpec({
    test("ValidationIssue exposes the fields it was constructed with") {
        val issue = ValidationIssue(
            severity = ValidationSeverity.ERROR,
            section = Civ3SectionIds.TERR,
            index = 3,
            field = "pollutionEffect",
            message = "pollutionEffect=99 is not a valid TERR index",
        )

        issue.severity shouldBe ValidationSeverity.ERROR
        issue.section shouldBe Civ3SectionIds.TERR
        issue.index shouldBe 3
        issue.field shouldBe "pollutionEffect"
        issue.message shouldBe "pollutionEffect=99 is not a valid TERR index"
    }
})
