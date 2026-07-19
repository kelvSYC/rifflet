package com.kelvsyc.rifflet.civ3

/**
 * Resolves [SlocEntry.ownerType]/[SlocEntry.owner] against [races]. See [Owner] for what each
 * case means.
 */
fun SlocEntry.resolveOwner(races: List<RaceEntry>): Owner = resolveOwner(ownerType, owner, races)
