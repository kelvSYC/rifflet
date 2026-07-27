package com.kelvsyc.rifflet.civ3

/**
 * The scenario's technology cost and research time bounds.
 *
 * Corresponds to the Conquests Rules Editor's `General Settings` tab's "Technology" groupbox, in
 * its entirety. Does not include `RuleEntry.goldenAgeDuration` — a separate, unrelated "Golden
 * Age" groupbox sits between this group's members in both the editor and the file, and stays a
 * loose field on [RuleEntry].
 *
 * @param futureTechCost The beaker cost of each Future Technology research — the repeatable
 *   research a civilization can keep doing once it has learned every advance in the tech tree.
 * @param maximumResearchTime The maximum number of turns any technology takes to research,
 *   regardless of its beaker cost.
 * @param minimumResearchTime The minimum number of turns any technology takes to research,
 *   regardless of its beaker cost.
 */
data class RuleTechnology(
    val futureTechCost: Int,
    val maximumResearchTime: Int,
    val minimumResearchTime: Int,
)
