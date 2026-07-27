package com.kelvsyc.rifflet.civ3

/**
 * The scenario's default and barbarian unit assignments.
 *
 * Corresponds to the Conquests Rules Editor's `General Settings` tab's "Default Units" groupbox,
 * in its entirety.
 *
 * @param advancedBarbarianUnitType The "Advanced Barbarian" dropdown, a `PRTO` section index.
 * @param basicBarbarianUnitType The "Basic Barbarian" dropdown, a `PRTO` section index.
 * @param barbarianSeaUnitType The "Barbarian Sea Unit" dropdown, a `PRTO` section index.
 * @param battleCreatedUnit The "Battle-Created Unit" dropdown, a `PRTO` section index.
 * @param buildArmyUnit The "Build-Army Unit" dropdown, a `PRTO` section index.
 * @param scout The "Scout" dropdown, a `PRTO` section index.
 * @param slave The "Captured Unit" dropdown, a `PRTO` section index.
 * @param startUnit1 The "Start Unit 1" dropdown, a `PRTO` section index.
 * @param startUnit2 The "Start Unit 2" dropdown, a `PRTO` section index.
 * @param flagUnitType The "Flag Unit" dropdown, a `PRTO` section index. Absent (`null`) from real
 *   [Civ3FormatEra.VANILLA] files, present in [Civ3FormatEra.PTW] and [Civ3FormatEra.CONQUESTS].
 *   Unlike this group's other 9 members,
 *   `null` here specifically means "absent from this file's era," not a real reference — `0` is
 *   itself a valid `PRTO` index, so this can't use `0` as its own "absent" sentinel the way this
 *   codebase's `-1` conventions do.
 */
data class RuleDefaultUnits(
    val advancedBarbarianUnitType: Int,
    val basicBarbarianUnitType: Int,
    val barbarianSeaUnitType: Int,
    val battleCreatedUnit: Int,
    val buildArmyUnit: Int,
    val scout: Int,
    val slave: Int,
    val startUnit1: Int,
    val startUnit2: Int,
    val flagUnitType: Int?,
)
