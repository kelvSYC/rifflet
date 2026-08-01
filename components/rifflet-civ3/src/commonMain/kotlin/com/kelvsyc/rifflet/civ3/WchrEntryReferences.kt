package com.kelvsyc.rifflet.civ3

/**
 * The 6 values of [WchrEntry.selectedBarbarianActivity]/[WchrEntry.actualBarbarianActivity], per
 * the Conquests Rules Editor's `Scenario Properties` → `Scenario` tab → "Barbarian Activity"
 * dropdown.
 *
 * Ordinal position matches the raw file value offset by 1 (raw `0` is [NO_BARBARIANS], raw `1` is
 * [SEDENTARY], etc.) — do not reorder these constants. Raw `-1` decodes to [NONE], a value not
 * offered by the dropdown itself; real files have it on both the selected and actual field, with
 * no confirmed explanation for when or why it occurs.
 */
enum class BarbarianActivity { NONE, NO_BARBARIANS, SEDENTARY, ROAMING, RESTLESS, RAGING, RANDOM }
