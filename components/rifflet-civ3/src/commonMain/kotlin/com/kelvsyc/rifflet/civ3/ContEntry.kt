package com.kelvsyc.rifflet.civ3

/**
 * One entry of the `CONT` section: a contiguous body of land or water and its tile count.
 *
 * Every tile on the map belongs to exactly one `CONT` entry: [TileEntry.continent] always
 * resolves to an entry here, and the sum of every entry's [numberOfTiles] always equals the
 * map's total tile count. A [ContType.LAND] entry always corresponds to a single physically
 * contiguous chunk of land — two adjacent land tiles are never split across two entries, and no
 * entry's land tiles are ever split into two disconnected regions. A [ContType.WATER] entry is
 * always contiguous too, but the reverse doesn't hold: a single physically-connected body of
 * water is often subdivided into several entries (distinct named seas/oceans, or a small
 * enclosed lake, which always gets its own entry distinct from the surrounding land) with no
 * terrain-visible boundary between them. Every water entry touches land somewhere; none are
 * landlocked by other water entries.
 *
 * @param type [ContType.WATER] or [ContType.LAND].
 */
data class ContEntry(
    val type: ContType,
    val numberOfTiles: Int,
)
