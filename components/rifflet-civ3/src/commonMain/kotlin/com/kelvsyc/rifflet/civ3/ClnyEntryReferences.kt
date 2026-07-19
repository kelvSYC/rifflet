package com.kelvsyc.rifflet.civ3

/**
 * Resolves [ClnyEntry.ownerType]/[ClnyEntry.owner] against [races]. See [Owner] for what each
 * case means.
 */
fun ClnyEntry.resolveOwner(races: List<RaceEntry>): Owner = resolveOwner(ownerType, owner, races)
