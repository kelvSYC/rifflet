package com.kelvsyc.rifflet.civ3.validation

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The severity of a [ValidationIssue].
 *
 * Both levels report the same kind of thing — a value the real Civ3 editors would never let you
 * build — so neither implies anything about whether the game engine itself would tolerate it at
 * runtime, which is a separate, generally unconfirmed question. [ERROR] means every real official
 * file matches the constraint without exception. [WARNING] means the constraint holds in general
 * but at least one real official file is a confirmed exception to it.
 */
enum class ValidationSeverity { ERROR, WARNING }

/**
 * One violation of an editor-confirmed constraint found by a [ValidationRule].
 *
 * @param section The section the issue concerns, reusing the section's own [ChunkId] (e.g.
 *   `Civ3SectionIds.TERR`) rather than a hand-typed string.
 * @param index The offending entry's position within [section]'s entry list, or `null` when the
 *   issue concerns [section] as a whole (e.g. its entry count) rather than one specific entry.
 * @param field The name of the specific field the issue concerns.
 */
data class ValidationIssue(
    val severity: ValidationSeverity,
    val section: ChunkId,
    val index: Int?,
    val field: String,
    val message: String,
)
