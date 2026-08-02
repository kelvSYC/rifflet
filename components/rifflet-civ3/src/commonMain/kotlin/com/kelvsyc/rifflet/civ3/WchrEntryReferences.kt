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

/**
 * The 4 values of [WchrEntry.selectedClimate]/[WchrEntry.actualClimate], per the "Generate a Map"
 * dialog's "Climate" dropdown. Ordinal position matches the raw file value directly — do not
 * reorder these constants.
 */
enum class Climate { ARID, NORMAL, WET, RANDOM }

/**
 * The 4 values of [WchrEntry.selectedLandform]/[WchrEntry.actualLandform], per the "Generate a
 * Map" dialog's "Landform" dropdown. Ordinal position matches the raw file value directly — do
 * not reorder these constants.
 */
enum class Landform { ARCHIPELAGO, CONTINENTS, PANGAEA, RANDOM }

/**
 * The 4 values of [WchrEntry.selectedOceanCoverage]/[WchrEntry.actualOceanCoverage], per the
 * "Generate a Map" dialog's "% Water Coverage" dropdown. Ordinal position matches the raw file
 * value directly — do not reorder these constants.
 */
enum class OceanCoverage { EIGHTY_PERCENT, SEVENTY_PERCENT, SIXTY_PERCENT, RANDOM }

/**
 * The 4 values of [WchrEntry.selectedTemperature]/[WchrEntry.actualTemperature], per the
 * "Generate a Map" dialog's "Temperature" dropdown. Ordinal position matches the raw file value
 * directly — do not reorder these constants.
 */
enum class Temperature { COOL, TEMPERATE, WARM, RANDOM }

/**
 * The 4 values of [WchrEntry.selectedAge]/[WchrEntry.actualAge], per the "Generate a Map"
 * dialog's "Age" dropdown. Ordinal position matches the raw file value directly — do not reorder
 * these constants.
 */
enum class Age { THREE_BILLION_YEARS, FOUR_BILLION_YEARS, FIVE_BILLION_YEARS, RANDOM }

/**
 * Resolves [WchrEntry.worldSize] against [worldSizes] (a file's `WSIZ` section, always exactly 5
 * entries in a real file). The raw value is a genuine index into this list — e.g. index `0`
 * resolves to the entry named "Tiny" — not an independent value of its own.
 *
 * An out-of-range raw value (past `WSIZ`'s last valid index) resolves to `null` here. The real map
 * editor, when loading such a file, shows its own "Select World Size" control as blank — matching
 * none of the 5 named presets — consistent with `null` being the right resolution, though the file
 * format's own reason for allowing this value isn't otherwise confirmed.
 */
fun WchrEntry.worldSizeWsiz(worldSizes: List<WsizEntry>): WsizEntry? = worldSizes.getOrNull(worldSize)
