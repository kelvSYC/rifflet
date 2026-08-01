package com.kelvsyc.rifflet.civ3

/**
 * One entry of the `CTZN` section: a citizen type's naming and economic-output parameters.
 *
 * @param prerequisite A `TECH` section index, per the Conquests Rules Editor — disabled there
 *   for the default citizen type, which needs no prerequisite.
 */
data class CtznEntry(
    val defaultCitizen: Int,
    val singularName: String,
    val civilopediaEntry: String,
    val pluralName: String,
    val prerequisite: Int,
    val luxuries: Int,
    val research: Int,
    val taxes: Int,
    val corruption: Int,
    val construction: Int,
)
