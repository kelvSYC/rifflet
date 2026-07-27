package com.kelvsyc.rifflet.civ3

/**
 * The Conquests Rules Editor's `Scenario Properties` → `Scenario` tab → "Time Options" groupbox's
 * "MP Timers" sub-section — present ([GameEntry.mpTimers] non-`null`) only in
 * [Civ3FormatEra.CONQUESTS] files with `minor != 6` (absent on `minor=6`, present on
 * `minor=7`/`8`). Modeled separately from [GameTimeOptions] even though the editor nests both
 * inside the same visual groupbox: a real PTW-era editor build has no "MP Timers" sub-section in
 * its own "Time Options" groupbox at all, and this group's presence condition is strictly
 * narrower than [GameTimeOptions]'s — a PTW `minor=18` file has [GameEntry.timeOptions] but never
 * this group.
 *
 * @param mpBasetime The "Base" field.
 * @param mpCityTime The first "Per" field (per-city time allowance).
 * @param mpUnitTime The second "Per" field (per-unit time allowance).
 */
data class GameMpTimers(
    val mpBasetime: Int,
    val mpCityTime: Int,
    val mpUnitTime: Int,
)
