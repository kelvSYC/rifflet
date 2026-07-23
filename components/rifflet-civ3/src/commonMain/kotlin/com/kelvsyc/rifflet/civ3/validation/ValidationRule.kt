// components/rifflet-civ3/src/commonMain/kotlin/com/kelvsyc/rifflet/civ3/validation/ValidationRule.kt
package com.kelvsyc.rifflet.civ3.validation

import com.kelvsyc.rifflet.civ3.Civ3File

/**
 * A single editor-confirmed constraint checked against a parsed [Civ3File]. Implementations
 * never throw; a rule whose required section(s) are absent from [Civ3File.sections] returns an
 * empty list rather than failing.
 */
fun interface ValidationRule {
    fun validate(file: Civ3File): List<ValidationIssue>
}
