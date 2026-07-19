package com.kelvsyc.rifflet.civ3

/**
 * The three released Civ III format eras. Civ III is no longer in active development, so this
 * mapping from [Civ3Header.major] is closed and final:
 *
 * - `major` 2-4 (`BIC ` magic): [VANILLA] — the original release.
 * - `major` 11 (`BICX` magic): [PTW] — Play the World, which added multiplayer and regicide.
 * - `major` 12 (`BICX` magic): [CONQUESTS] — Conquests, which added scientific leaders, tile
 *   landmarks, and terrain disease, among other features.
 *
 * See individual section parsers' KDoc for exactly which fields are absent in which era. This
 * mapping does not capture every real-data structural variation on its own — some sections have
 * confirmed sub-tiers *within* an era (e.g. `GAME`'s PTW-minor-dependent cutoffs, or `TILE`'s
 * `major=2` vs. `major=3`/`4` distinction, both within [VANILLA]) — see those sections' own KDoc
 * for the finer-grained detail where it exists.
 */
enum class Civ3FormatEra {
    VANILLA,
    PTW,
    CONQUESTS,
}
