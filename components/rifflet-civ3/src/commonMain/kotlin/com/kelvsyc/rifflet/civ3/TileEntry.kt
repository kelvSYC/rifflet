package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `TILE` section: a single map tile's terrain, overlay, and ownership data.
 * The last of the 27 Civ3 section types modeled in this codebase, deliberately deferred until
 * real Civ3 data was available — `QueryCiv3`'s own authors flag this as their least-complete,
 * most tangled struct.
 *
 * Confirmed against real files to have (at least) a four-step version ladder keyed by the exact
 * `VER#` header `major` value: major=2 (22 bytes, [riverConnections] through [continent]),
 * major=3 or 4 (23 bytes, +[unknown2] only), major=11/PTW (29 bytes, +[victoryPointLocation]/
 * [ruin] on top of [unknown2]), and major=12/Conquests (45 bytes, +[c3cOverlays] through
 * [unknown5]). Each trailing field is read defensively and independently — see
 * `TileEntryParser` — which is why the major=3/4 intermediate step (undiscovered until a later
 * real-data investigation covering all 120 files in a real Civ III install) parses correctly
 * without any code change: the guards were never coupled into a single all-or-nothing tier.
 *
 * @param resource Likely a `GOOD` section index (naming convention only); not confirmed by
 *   either cross-referenced source.
 * @param terrain A packed nibble pair — low nibble is the base terrain (`TERR` index), high
 *   nibble is the overlay terrain (`TERR` index) — confirmed by cross-referencing real tile data
 *   against [c3cTerrain] (a near-duplicate Conquests-only field with the same packing, per
 *   `QueryCiv3`'s explicit `BaseTerrain`/`OverlayTerrain` nibble-mask accessors). Most tiles have
 *   identical base and overlay terrain; they differ only where an overlay terrain (e.g. forest)
 *   sits atop a different base terrain. Preserved raw, not decomposed into separate properties.
 * @param colony Likely a `CLNY` section index (naming convention only); not confirmed by either
 *   cross-referenced source.
 * @param city Likely a reference to a placed city (naming convention only); not confirmed by
 *   either cross-referenced source.
 * @param continent Likely a `CONT` section index (naming convention only); not confirmed by
 *   either cross-referenced source.
 * @param unknown2 1 byte with zero documented behavior from either cross-referenced source;
 *   confirmed absent only in the earliest vanilla revision (major=2) — present from major=3
 *   onward (including PTW and Conquests), read defensively; preserved raw, not validated.
 * @param victoryPointLocation `0` if this tile is a Victory Point Location, `-1` otherwise, per
 *   `QueryCiv3`; present only from PTW (major=11) onward, read defensively.
 * @param ruin Present only from PTW (major=11) onward, read defensively.
 * @param c3cOverlays 4 bytes with ~13 named booleans across both cross-referenced sources
 *   (roads, railroads, improvements, barbarian camps, craters, etc.); present only in Conquests
 *   files, read defensively; preserved raw, not decomposed.
 * @param unknown3 1 byte with zero documented behavior from either cross-referenced source;
 *   present only in Conquests files, read defensively; preserved raw, not validated.
 * @param c3cTerrain A near-duplicate of [terrain]'s packed nibble pair, present only in
 *   Conquests files (read defensively) — numerically identical to [terrain] in the vast majority
 *   of real samples, differing only where the tile's base and overlay terrain genuinely differ.
 * @param unknown4 2 bytes with zero documented behavior from either cross-referenced source;
 *   present only in Conquests files, read defensively; preserved raw, not validated.
 * @param fogOfWar Present only in Conquests files, read defensively.
 * @param c3cBonuses 4 bytes with ~9 named booleans across both cross-referenced sources (bonus
 *   grassland, player start, snow-capped mountain, river directions, landmark, etc.); present
 *   only in Conquests files, read defensively; preserved raw, not decomposed.
 * @param unknown5 2 bytes with zero documented behavior from either cross-referenced source;
 *   present only in Conquests files, read defensively; preserved raw, not validated.
 * @param unknown6 4 bytes, undocumented by any cross-referenced source and not part of
 *   `QueryCiv3`'s struct at all. Confirmed present in exactly 2 of 21 sampled real Conquests
 *   (`major=12`) files — `7 Sengoku - Sword of the Shogun.biq` and `Intro3 New Alliances.biq` —
 *   always exactly zero-valued across every one of 11,040 sampled tiles in both files. Requires
 *   the file's `VER#` header `minor=6`, but `minor=6` alone is not sufficient (most `minor=6`
 *   Conquests files do NOT have this field) — genuinely unexplained beyond that partial
 *   correlation despite extensive investigation (ruled out: file modification time, header
 *   description text, `GAME` rule flags, and direct cross-reference against Apolyton's actual
 *   published `TILE` field documentation, which matches this codebase's model exactly through 45
 *   bytes with no further fields). Read defensively; preserved raw, not validated.
 */
data class TileEntry(
    val riverConnections: Byte,
    val border: Byte,
    val resource: Int,
    val textureLocation: Byte,
    val textureFile: Byte,
    val unknown: ByteString,
    val overlayFlags: Byte,
    val terrain: Byte,
    val bonusFlags: Byte,
    val riverCrossingFlags: Byte,
    val barbarianTribe: Short,
    val colony: Short,
    val city: Short,
    val continent: Short,
    val unknown2: ByteString,
    val victoryPointLocation: Short,
    val ruin: Int,
    val c3cOverlays: ByteString,
    val unknown3: ByteString,
    val c3cTerrain: Byte,
    val unknown4: ByteString,
    val fogOfWar: Short,
    val c3cBonuses: ByteString,
    val unknown5: ByteString,
    val unknown6: ByteString,
) {
    init {
        require(unknown.size == 2) { "TileEntry.unknown must be exactly 2 bytes, was ${unknown.size}" }
        require(unknown2.size == 1) { "TileEntry.unknown2 must be exactly 1 byte, was ${unknown2.size}" }
        require(c3cOverlays.size == 4) { "TileEntry.c3cOverlays must be exactly 4 bytes, was ${c3cOverlays.size}" }
        require(unknown3.size == 1) { "TileEntry.unknown3 must be exactly 1 byte, was ${unknown3.size}" }
        require(unknown4.size == 2) { "TileEntry.unknown4 must be exactly 2 bytes, was ${unknown4.size}" }
        require(c3cBonuses.size == 4) { "TileEntry.c3cBonuses must be exactly 4 bytes, was ${c3cBonuses.size}" }
        require(unknown5.size == 2) { "TileEntry.unknown5 must be exactly 2 bytes, was ${unknown5.size}" }
        require(unknown6.size == 4) { "TileEntry.unknown6 must be exactly 4 bytes, was ${unknown6.size}" }
    }
}
