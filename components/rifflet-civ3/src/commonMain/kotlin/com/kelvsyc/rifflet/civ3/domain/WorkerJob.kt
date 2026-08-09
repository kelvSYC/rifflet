package com.kelvsyc.rifflet.civ3.domain

/**
 * A worker job (e.g. build road, clear forest), mutable — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.TfrmEntry].
 *
 * @param name This job's name, per the Rules Editor's Worker Jobs page.
 * @param civilopediaEntry This job's Civilopedia entry key.
 * @param turnsToComplete Relative to terrain with a movement cost of 2 (e.g. Forest); other
 *   terrain's actual duration is scaled by its own movement cost.
 * @param required A prerequisite advance, per the Worker Jobs page's own dropdown.
 * @param requiredResources This job's 2 required natural resources. A fixed-size list (like
 *   [com.kelvsyc.rifflet.civ3.domain.Prto.requiredResources], sized 2 instead of 3) rather than 2
 *   flat fields or a `Set`: the Worker Jobs page tolerates requiring the same resource in both
 *   dropdowns, which a `Set` would silently collapse. Use [tfrmRequiredResourcesOf] to build a
 *   canonical, front-packed list by hand.
 * @param order The in-game command label shown when directing a worker to perform this job (e.g.
 *   "Build Mine", "Irrigate") — distinct from [name].
 */
data class WorkerJob(
    var name: String,
    var civilopediaEntry: String = "",
    var turnsToComplete: Int = 0,
    var required: Tech? = null,
    var requiredResources: MutableList<Resource?> = MutableList(2) { null },
    var order: String = "",
) {
    init {
        require(requiredResources.size == 2) {
            "WorkerJob.requiredResources must be exactly 2 elements, was ${requiredResources.size}"
        }
    }
}

/**
 * Builds a canonical, front-packed [WorkerJob.requiredResources] list by hand — the domain-layer
 * counterpart to [requiredResourcesOf], sized 2 instead of 3 (`TFRM` has only 2 required-resource
 * slots, vs. `PRTO`'s 3).
 */
fun tfrmRequiredResourcesOf(vararg resources: Resource): MutableList<Resource?> {
    require(resources.size <= 2) { "tfrmRequiredResourcesOf accepts at most 2 resources, was ${resources.size}" }
    return (resources.toList() + List(2 - resources.size) { null }).toMutableList()
}
