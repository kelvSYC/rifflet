package com.kelvsyc.rifflet.civ3.domain

/**
 * The scenario's default and barbarian unit assignments, mutable — the domain-layer counterpart
 * to [com.kelvsyc.rifflet.civ3.RuleDefaultUnits], with each `PRTO` index resolved.
 *
 * @param flagUnitType `null` for two different wire-layer reasons that collapse into one here:
 *   a real VANILLA-era file that was never capable of specifying a flag unit at all, or a dangling
 *   index in a later era — the same accepted "sentinel collapses into a plain dangling-reference
 *   null" gap as `LeadEntry.initialEra`'s banked "Future Era" case.
 */
data class DefaultUnits(
    var advancedBarbarianUnitType: Prto? = null,
    var basicBarbarianUnitType: Prto? = null,
    var barbarianSeaUnitType: Prto? = null,
    var battleCreatedUnit: Prto? = null,
    var buildArmyUnit: Prto? = null,
    var scout: Prto? = null,
    var slave: Prto? = null,
    var startUnit1: Prto? = null,
    var startUnit2: Prto? = null,
    var flagUnitType: Prto? = null,
)
