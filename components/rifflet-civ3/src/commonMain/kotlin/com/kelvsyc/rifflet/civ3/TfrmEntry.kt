package com.kelvsyc.rifflet.civ3

/**
 * One entry of the `TFRM` section: a worker job (e.g. build road, clear forest) and its
 * requirements.
 *
 * @param turnsToComplete Relative to terrain with a movement cost of 2 (e.g. Forest); other
 *   terrain's actual duration is scaled by its own movement cost.
 * @param required A `TECH` section index, per the Conquests Rules Editor.
 * @param requiredResource1 A `GOOD` section index, per the Conquests Rules Editor. Same treatment
 *   applies to [requiredResource2].
 * @param order The in-game command label shown when directing a worker to perform this job (e.g.
 *   "Build Mine", "Irrigate") — distinct from [name], the Rules Editor's own display name for the
 *   job type. Documented on the game's own "Worker Jobs Page" help topic.
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
