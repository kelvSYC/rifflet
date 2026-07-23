// components/rifflet-civ3/src/commonTest/kotlin/com/kelvsyc/rifflet/civ3/validation/ValidationRuleTest.kt
package com.kelvsyc.rifflet.civ3.validation

import com.kelvsyc.rifflet.civ3.Civ3File
import com.kelvsyc.rifflet.civ3.Civ3Header
import com.kelvsyc.rifflet.civ3.Civ3SectionIds
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ValidationRuleTest : FunSpec({
    test("a lambda satisfies ValidationRule and validate() invokes it with the given file") {
        val file = Civ3File(
            Civ3Header(major = 12, minor = 0, description = "", title = ""),
            sections = emptyList(),
        )
        val issue = ValidationIssue(ValidationSeverity.WARNING, Civ3SectionIds.TERR, 0, "field", "message")
        val rule = ValidationRule { candidate -> if (candidate.sections.isEmpty()) listOf(issue) else emptyList() }

        rule.validate(file) shouldBe listOf(issue)
    }
})
