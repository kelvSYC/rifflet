package com.kelvsyc.rifflet.civ3

/**
 * One entry of the `UNIT` section: a placed unit instance.
 *
 * @param legacyName [Civ3FormatEra.VANILLA]-era name field; unused in
 *   [Civ3FormatEra.PTW]/[Civ3FormatEra.CONQUESTS]-era files per both reverse-engineering sources —
 *   see [name].
 * @param experienceLevel An `EXPR` section index, per the Conquests Rules Editor (not merely a
 *   naming-based inference).
 * @param owner Meaning depends on [ownerType]: a `RACE` section index when Civ, a player index
 *   (0-based) when Player, or a barbarian tribe ID when Barbarian — same treatment as
 *   `SlocEntry.owner`/`ClnyEntry.owner`.
 * @param unitType A `PRTO#` (unit prototype) reference — explicitly documented by existing
 *   reverse-engineering work, not merely a naming-based inference.
 * @param ptwName The [Civ3FormatEra.PTW]/[Civ3FormatEra.CONQUESTS]-era name field — authoritative
 *   when present; empty both when a [Civ3FormatEra.PTW]-era file wrote it blank and when a
 *   shorter [Civ3FormatEra.VANILLA]-era item omits it entirely — see [name].
 */
data class UnitEntry(
    val legacyName: String,
    val ownerType: Int,
    val experienceLevel: Int,
    val owner: Int,
    val unitType: Int,
    val aiStrategy: Int,
    val x: Int,
    val y: Int,
    val ptwName: String,
    val useCivilizationKing: Int,
) {
    /** Resolved display name: prefers [ptwName] (authoritative for
     * [Civ3FormatEra.PTW]/[Civ3FormatEra.CONQUESTS]-era files, per both reverse-engineering sources),
     * falling back to [legacyName] for [Civ3FormatEra.VANILLA]-era files where only that field
     * is populated. Provisional heuristic, unconfirmed against real Civ3 install data. */
    val name: String get() = ptwName.ifBlank { legacyName }
}
