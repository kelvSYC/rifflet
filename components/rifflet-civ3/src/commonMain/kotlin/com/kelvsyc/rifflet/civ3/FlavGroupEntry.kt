package com.kelvsyc.rifflet.civ3

/**
 * One flavor group of the `FLAV` section: a list of [FlavorEntry] records. In practice there is
 * always exactly one flavor group per file, with exactly 7 [flavors] — the Civ3 Editor does not
 * appear to allow either count to be changed — but the file format itself declares both
 * dynamically, so this type models them as such rather than assuming fixed sizes.
 */
data class FlavGroupEntry(val flavors: List<FlavorEntry>)
