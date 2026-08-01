package com.kelvsyc.rifflet.civ3

/**
 * One entry of the `SLOC` section: a starting location reserved for a civilization, player, or
 * barbarian tribe.
 *
 * @param ownerType 0=None, 1=Barbarian, 2=Civ, 3=Player.
 * @param owner Meaning depends on [ownerType]: a `RACE` section index when Civ, a player index
 *   (0-based) when Player, or a barbarian tribe ID when Barbarian, per existing
 *   reverse-engineering documentation of the BIX/BIQ format.
 */
data class SlocEntry(
    val ownerType: Int,
    val owner: Int,
    val x: Int,
    val y: Int,
)
