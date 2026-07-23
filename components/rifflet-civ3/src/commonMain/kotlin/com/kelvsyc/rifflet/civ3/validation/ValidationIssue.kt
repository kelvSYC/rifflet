// components/rifflet-civ3/src/commonMain/kotlin/com/kelvsyc/rifflet/civ3/validation/ValidationIssue.kt
package com.kelvsyc.rifflet.civ3.validation

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The severity of a [ValidationIssue].
 */
enum class ValidationSeverity { ERROR, WARNING }

/**
 * One violation of an editor-confirmed constraint found by a [ValidationRule].
 *
 * @param section The section the offending entry belongs to, reusing the section's own
 *   [ChunkId] (e.g. `Civ3SectionIds.TERR`) rather than a hand-typed string.
 * @param index The offending entry's position within [section]'s entry list.
 * @param field The name of the specific field the issue concerns.
 */
data class ValidationIssue(
    val severity: ValidationSeverity,
    val section: ChunkId,
    val index: Int,
    val field: String,
    val message: String,
)
