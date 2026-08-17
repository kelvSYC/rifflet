package com.kelvsyc.rifflet.civ3.domain

/**
 * One `ESPN` diplomat/spy espionage mission type, mutable — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.EspnEntry].
 */
data class EspionageMission(
    var name: String,
    var description: String = "",
    var civilopediaEntry: String = "",
    var missionFlags: Int = 0,
    var baseCost: Int = 0,
)
