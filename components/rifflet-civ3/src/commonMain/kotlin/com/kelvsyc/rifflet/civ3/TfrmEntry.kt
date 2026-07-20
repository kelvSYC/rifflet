package com.kelvsyc.rifflet.civ3

/**
 * One entry of the `TFRM` section: a worker job (e.g. build road, clear forest) and its
 * requirements.
 *
 * @param turnsToComplete Relative to terrain with a movement cost of 2 (e.g. Forest); other
 *   terrain's actual duration is scaled by its own movement cost.
 * @param required Likely a `TECH` section index (naming convention shared with
 *   `CtznEntry.prerequisite`); not confirmed by either primary source.
 * @param requiredResource1 Likely a `GOOD` section index (naming convention shared with
 *   `PRTO`'s `RequiredResource1`..`3` fields); not confirmed by either primary source.
 */
data class TfrmEntry(
    val name: String,
    val civilopediaEntry: String,
    val turnsToComplete: Int,
    val required: Int,
    val requiredResource1: Int,
    val requiredResource2: Int,
    val order: String,
)
