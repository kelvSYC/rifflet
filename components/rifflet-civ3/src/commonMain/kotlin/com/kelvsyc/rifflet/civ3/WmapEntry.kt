package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `WMAP` section: the file's world-map generation settings.
 *
 * In practice there is always exactly one `WMAP` entry per file, per existing
 * reverse-engineering documentation's own "(1)" annotation on the section's item count.
 *
 * @param resourceIds Likely `GOOD` section indices identifying which resources are available on
 *   this map — inferred from a separate reverse-engineered reference implementation's comment;
 *   not confirmed by existing reverse-engineering documentation.
 * @param unknown1 8 bytes with zero documented behavior from either reverse-engineering source;
 *   preserved raw, not validated. Same treatment as `RaceEntry.unknown`.
 * @param unknown2 128 bytes with zero documented behavior from either reverse-engineering source;
 *   preserved raw, not validated. Same treatment as `RaceEntry.unknown`.
 * @param flags 4 bytes of packed boolean flags; see [xWrapping], [yWrapping], [polarIceCaps] for
 *   the named per-bit accessors.
 */
data class WmapEntry(
    val resourceIds: List<Int>,
    val numberOfContinents: Int,
    val height: Int,
    val distanceBetweenCivs: Int,
    val numberOfCivs: Int,
    val unknown1: ByteString,
    val width: Int,
    val unknown2: ByteString,
    val mapSeed: Int,
    val flags: Int,
) {
    init {
        require(unknown1.size == 8) { "WmapEntry.unknown1 must be exactly 8 bytes, was ${unknown1.size}" }
        require(unknown2.size == 128) { "WmapEntry.unknown2 must be exactly 128 bytes, was ${unknown2.size}" }
    }
}
