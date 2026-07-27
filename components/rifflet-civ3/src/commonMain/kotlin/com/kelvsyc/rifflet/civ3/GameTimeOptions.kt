package com.kelvsyc.rifflet.civ3

/**
 * The scenario's calendar and turn-timing configuration.
 *
 * Corresponds to the Conquests Rules Editor's `Scenario Properties` → `Scenario` tab → "Time
 * Options" groupbox, read as one contiguous run of fields.
 *
 * Present ([GameEntry.timeOptions] non-`null`) only for [Civ3FormatEra.PTW] files with
 * `minor=18` (the dominant PTW tier) or any [Civ3FormatEra.CONQUESTS] file; absent on
 * [Civ3FormatEra.VANILLA] and every other [Civ3FormatEra.PTW] `minor` tier — see
 * `GameEntryParser`'s KDoc for the full cutoff-tier breakdown. [GameEntry.mpTimers] is
 * deliberately excluded even though the editor visually nests an "MP Timers" sub-section inside
 * this same groupbox: that sub-section has its own, stricter presence condition (see
 * [GameMpTimers]'s KDoc) that this group's fields do not share — a real PTW-era editor build has
 * no "MP Timers" sub-section in its "Time Options" groupbox at all.
 *
 * @param useTimeLimit The "Time Limit" checkbox.
 * @param baseTimeUnit The "Base unit of time" dropdown (e.g. Years, Turns).
 * @param startMonth The "Start Date" group's Month field.
 * @param startWeek The "Start Date" group's Week field.
 * @param startYear The "Start Date" group's Year field.
 * @param minuteTimeLimit The "Time Limit" group's Minutes field.
 * @param turnTimeLimit The "Time Limit" group's Turns field.
 * @param timescaleNumberOfTurns The "Time Scale" group's 7 "turns =" values, in display order.
 * @param turnNumberOfTimeUnits The "Time Scale" group's 7 "units each" values, in display order,
 *   parallel to [timescaleNumberOfTurns].
 */
data class GameTimeOptions(
    val useTimeLimit: Int,
    val baseTimeUnit: Int,
    val startMonth: Int,
    val startWeek: Int,
    val startYear: Int,
    val minuteTimeLimit: Int,
    val turnTimeLimit: Int,
    val timescaleNumberOfTurns: List<Int>,
    val turnNumberOfTimeUnits: List<Int>,
) {
    init {
        require(timescaleNumberOfTurns.size == 7) {
            "GameTimeOptions.timescaleNumberOfTurns must have exactly 7 elements, " +
                "had ${timescaleNumberOfTurns.size}"
        }
        require(turnNumberOfTimeUnits.size == 7) {
            "GameTimeOptions.turnNumberOfTimeUnits must have exactly 7 elements, " +
                "had ${turnNumberOfTimeUnits.size}"
        }
    }
}
