package com.kelvsyc.rifflet.civ3

/**
 * One entry of the `SLOC` section: a starting location reserved for a civilization, player, or
 * barbarian tribe.
 *
 * @param ownerType 0=None, 1=Barbarian, 2=Civ, 3=Player.
 * @param owner Meaning depends on [ownerType]: a `RACE` section index when Civ, a player index
 *   (0-based) when Player, or a barbarian tribe ID when Barbarian — explicitly documented by
 *   Apolyton's BIX/BIQ format reference (not a naming-based inference, unlike similar
 *   index-shaped fields elsewhere in this codebase).
 */
data class SlocEntry(
    val ownerType: Int,
    val owner: Int,
    val x: Int,
    val y: Int,
)
