package com.kelvsyc.rifflet.civ3

/**
 * One entry of the `CLNY` section: a colony (a non-city improvement, e.g. a mine or fort)
 * belonging to a civilization, player, or barbarian tribe.
 *
 * @param ownerType 0=None, 1=Barbarian, 2=Civ, 3=Player.
 * @param owner Meaning depends on [ownerType]: a `RACE` section index when Civ, a player index
 *   (0-based) when Player, or a barbarian tribe ID when Barbarian, per existing
 *   reverse-engineering documentation of the BIX/BIQ format. Same treatment as `SlocEntry.owner`.
 */
data class ClnyEntry(
    val ownerType: Int,
    val owner: Int,
    val x: Int,
    val y: Int,
    val improvementType: Int,
)
