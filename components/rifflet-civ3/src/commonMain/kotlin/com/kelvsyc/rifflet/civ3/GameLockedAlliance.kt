package com.kelvsyc.rifflet.civ3

/**
 * The scenario's Conquests-only locked-alliance settings.
 *
 * Corresponds to the Conquests Rules Editor's `Scenario Properties` → `Locked Alliance` tab, in
 * its entirety — present ([GameEntry.lockedAlliance] non-`null`) only in [Civ3FormatEra.CONQUESTS]
 * files, which are the only files with this tab at all.
 *
 * @param allianceNames 5 fixed alliance-name slots, index 0 conventionally "unallied"/blank.
 * @param allianceWars A flat, row-major 5×5 matrix of war-status values between alliances; index
 *   `[i, j]` is `allianceWars[i * 5 + j]`, per the tab's "At war with:" listboxes.
 * @param allianceVictoryType The "Victory Type" radio group (0=Individual, 1=Coalition).
 */
data class GameLockedAlliance(
    val allianceNames: List<String>,
    val allianceWars: List<Int>,
    val allianceVictoryType: Int,
) {
    init {
        require(allianceNames.size == 5) {
            "GameLockedAlliance.allianceNames must have exactly 5 elements, had ${allianceNames.size}"
        }
        require(allianceWars.size == 25) {
            "GameLockedAlliance.allianceWars must have exactly 25 elements, had ${allianceWars.size}"
        }
    }
}
