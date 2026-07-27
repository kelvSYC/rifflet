package com.kelvsyc.rifflet.civ3

/**
 * The scenario's technology cost and research time bounds.
 *
 * Corresponds to the Conquests Rules Editor's `General Settings` tab's "Technology" groupbox, in
 * its entirety. Does not include `RuleEntry.goldenAgeDuration` — a separate, unrelated "Golden
 * Age" groupbox sits between this group's members in both the editor and the file, and stays a
 * loose field on [RuleEntry].
 *
 * @param futureTechCost The "Future Tech Cost" field.
 * @param maximumResearchTime The "Maximum Research Time (turns)" field.
 * @param minimumResearchTime The "Minimum Research Time (turns)" field.
 */
data class RuleTechnology(
    val futureTechCost: Int,
    val maximumResearchTime: Int,
    val minimumResearchTime: Int,
)
