package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.TfrmEntry
import com.kelvsyc.rifflet.civ3.WorkerJobSlot
import com.kelvsyc.rifflet.civ3.index

/**
 * Converts a parsed `TFRM` section to its domain-layer form, keyed by [WorkerJobSlot] rather than
 * returned as a flat list — see `WorkerJobSlot`'s own KDoc for why.
 *
 * [techs]/[resources] are the already domain-converted `TECH`/`GOOD` lists.
 *
 * Throws [IllegalArgumentException] if this list's size doesn't exactly match the number of slots
 * valid for [era] — the domain-layer equivalent of `validateTfrmCardinality`, since a
 * `Map<WorkerJobSlot, WorkerJob>` can't structurally guarantee completeness the way a fixed-size
 * array could.
 */
fun List<TfrmEntry>.toDomain(
    era: Civ3FormatEra,
    techs: List<Tech>,
    resources: List<Resource>,
): Map<WorkerJobSlot, WorkerJob> {
    val slots = WorkerJobSlot.entries.filter { it.index(era) != null }
    require(size == slots.size) {
        "TFRM section must have exactly ${slots.size} entries for $era, was $size"
    }
    return slots.associateWith { slot ->
        val entry = this[slot.index(era)!!]
        WorkerJob(
            name = entry.name,
            civilopediaEntry = entry.civilopediaEntry,
            turnsToComplete = entry.turnsToComplete,
            required = techs.getOrNull(entry.required),
            requiredResources = mutableListOf(
                resources.getOrNull(entry.requiredResource1),
                resources.getOrNull(entry.requiredResource2),
            ),
            order = entry.order,
        )
    }
}

/**
 * Converts a `TFRM` section's domain-layer form back to wire entries, ordered by [WorkerJobSlot]
 * wire index for [era].
 *
 * Throws [IllegalArgumentException] if this map's key set isn't exactly the slots valid for
 * [era], or if [WorkerJob.required] or either [WorkerJob.requiredResources] entry resolves to an
 * object not present in the corresponding list argument — `indexOf`-based, the same accepted
 * structural-equality limitation as every other `toWire()` in this codebase. A `null` value
 * writes back `-1`.
 */
fun Map<WorkerJobSlot, WorkerJob>.toWire(
    era: Civ3FormatEra,
    techs: List<Tech>,
    resources: List<Resource>,
): List<TfrmEntry> {
    val slots = WorkerJobSlot.entries.filter { it.index(era) != null }
    require(keys == slots.toSet()) {
        "TFRM map must have exactly the keys ${slots.toSet()} for $era, had $keys"
    }
    return slots.sortedBy { it.index(era) }.map { getValue(it) }.map { job ->
        val requiredIndex = job.required?.let {
            val index = techs.indexOf(it)
            require(index >= 0) { "WorkerJob.required references a Tech not present in techs" }
            index
        } ?: -1
        val requiredResource1 = job.requiredResources[0]?.let {
            val index = resources.indexOf(it)
            require(index >= 0) { "WorkerJob.requiredResources[0] references a Resource not present in resources" }
            index
        } ?: -1
        val requiredResource2 = job.requiredResources[1]?.let {
            val index = resources.indexOf(it)
            require(index >= 0) { "WorkerJob.requiredResources[1] references a Resource not present in resources" }
            index
        } ?: -1
        TfrmEntry(
            name = job.name,
            civilopediaEntry = job.civilopediaEntry,
            turnsToComplete = job.turnsToComplete,
            required = requiredIndex,
            requiredResource1 = requiredResource1,
            requiredResource2 = requiredResource2,
            order = job.order,
        )
    }
}

/**
 * Returns this map's [WorkerJob] values ordered by [WorkerJobSlot] wire index for [era] — the
 * shape callers resolving a wire index-based cross-reference (e.g. `Terrain.workerJobAllowed`)
 * need.
 */
fun Map<WorkerJobSlot, WorkerJob>.toOrderedList(era: Civ3FormatEra): List<WorkerJob> =
    WorkerJobSlot.entries
        .filter { it.index(era) != null }
        .sortedBy { it.index(era) }
        .map { getValue(it) }
