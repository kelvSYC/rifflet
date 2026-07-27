package com.kelvsyc.rifflet.civ3

/**
 * The Conquests Rules Editor's `Scenario Properties` → `Disasters!` tab's "Plague Information"
 * groupbox — present ([GameEntry.plagueSettings] non-`null`) only in [Civ3FormatEra.CONQUESTS]
 * files, which are the only files with this tab at all. Does not include the same tab's separate
 * "Volcanos" groupbox (`GameEntry.eruptionPeriod`, its own field on [GameEntry] since it has
 * nothing else to group with).
 *
 * @param plagueName The "Plague Name" field.
 * @param permitPlagues The "Permit Plagues" checkbox.
 * @param plagueEarliestStart The "Earliest Start" field.
 * @param plagueVariation The "Variance" field.
 * @param plagueDuration The "Duration" field.
 * @param plagueStrength The "Strength" field.
 * @param plagueGracePeriod The "Grace Period" field.
 * @param plagueMaxOccurrence The "Max Occurances" field.
 */
data class GamePlagueSettings(
    val plagueName: String,
    val permitPlagues: Byte,
    val plagueEarliestStart: Int,
    val plagueVariation: Int,
    val plagueDuration: Int,
    val plagueStrength: Int,
    val plagueGracePeriod: Int,
    val plagueMaxOccurrence: Int,
)
