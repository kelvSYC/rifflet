package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.civ3.validation.ValidationIssue
import com.kelvsyc.rifflet.civ3.validation.ValidationRule

/**
 * Every registered [ValidationRule], applied by [Civ3File.validate]. Grows by one entry per
 * confirmed rule; not a generic registry/auto-discovery mechanism — see the design spec for why
 * that's deferred.
 */
private val civ3ValidationRules: List<ValidationRule> = listOf(
    ValidationRule { file -> validatePollutionEffect(file) }
)

/**
 * Checks this file against every editor-confirmed constraint this library knows about. Never
 * throws; a rule whose required section(s) are absent from [Civ3File.sections] simply
 * contributes no issues.
 */
fun Civ3File.validate(): List<ValidationIssue> = civ3ValidationRules.flatMap { it.validate(this) }
