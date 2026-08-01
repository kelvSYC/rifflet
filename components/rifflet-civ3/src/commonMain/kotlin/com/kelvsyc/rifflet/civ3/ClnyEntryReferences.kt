package com.kelvsyc.rifflet.civ3

/**
 * Resolves [ClnyEntry.ownerType]/[ClnyEntry.owner] against [races]. See [Owner] for what each
 * case means.
 */
fun ClnyEntry.resolveOwner(races: List<RaceEntry>): Owner = resolveOwner(ownerType, owner, races)

/**
 * The 4 documented values of [ClnyEntry.improvementType], per existing reverse-engineering
 * documentation of the BIX/BIQ format.
 *
 * Ordinal position deliberately matches the documented file values (0=colony, 1=airfield,
 * 2=radar tower, 3=outpost) — do not reorder these constants.
 */
enum class ClnyImprovementType { COLONY, AIRFIELD, RADAR_TOWER, OUTPOST }

/**
 * Decodes [ClnyEntry.improvementType] into [ClnyImprovementType], or `null` if the raw value is
 * outside the documented 0-3 range.
 */
val ClnyEntry.improvementTypeEnum: ClnyImprovementType?
    get() = ClnyImprovementType.entries.getOrNull(improvementType)
