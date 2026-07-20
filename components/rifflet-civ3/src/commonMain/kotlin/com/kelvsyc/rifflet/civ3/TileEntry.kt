package com.kelvsyc.rifflet.civ3

import okio.ByteString

/**
 * One entry of the `TILE` section: a single map tile's terrain, overlay, and ownership data. A
 * separate reverse-engineered reference implementation's own authors describe this as their
 * least-complete, most tangled struct.
 *
 * Has (at least) a four-step version ladder keyed by the `VER#` header's `major` value:
 * `major=2` (22 bytes, [riverConnections] through [continent]), `major=3` or `4` (23 bytes,
 * +[unknown2] — both are [Civ3FormatEra.VANILLA], a finer split than [Civ3FormatEra] itself
 * distinguishes), `major=11`/[Civ3FormatEra.PTW] (29 bytes, +[victoryPointLocation]/[ruin] on top
 * of [unknown2]), and `major=12`/[Civ3FormatEra.CONQUESTS] (45 bytes, +[c3cOverlays] through
 * [unknown5]). Each trailing field is read defensively and independently — see
 * `TileEntryParser` — so the guards are never coupled into a single all-or-nothing tier.
 *
 * @param resource Likely a `GOOD` section index (naming convention only); not confirmed by
 *   either reverse-engineering source.
 * @param overlayFlags 1 byte with 8 named booleans; see [TileEntry.road] and its sibling
 *   accessors in `TileEntryFlags.kt`.
 * @param terrain A packed nibble pair — low nibble is the base terrain (`TERR` index), high
 *   nibble is the overlay terrain (`TERR` index) — confirmed by cross-referencing real tile data
 *   against [c3cTerrain] (a near-duplicate [Civ3FormatEra.CONQUESTS]-only field with the same
 *   packing, per a separate reverse-engineered reference implementation's explicit
 *   `BaseTerrain`/`OverlayTerrain` nibble-mask accessors).
 *   Most tiles have identical base and overlay terrain; they differ only where an overlay
 *   terrain (e.g. forest) sits atop a different base terrain. Preserved raw, not decomposed into
 *   separate properties.
 * @param bonusFlags 1 byte with 4 named booleans at non-contiguous bit positions; see
 *   [TileEntry.bonusGrassland] and its sibling accessors in `TileEntryFlags.kt`.
 * @param riverConnections 1 byte with 4 named booleans; see [TileEntry.riverInNorth] and its
 *   sibling accessors in `TileEntryFlags.kt`.
 * @param riverCrossingFlags 1 byte with 8 named booleans (compass directions); see
 *   [TileEntry.crossingN] and its sibling accessors in `TileEntryFlags.kt`.
 * @param colony A `CLNY` section index. See `TileEntryParser`'s KDoc for a byte-order detail
 *   this field shares with [city].
 * @param city A reference to a placed `CITY` entry. See `TileEntryParser`'s KDoc for a
 *   byte-order detail this field shares with [colony].
 * @param continent Likely a `CONT` section index (naming convention only); not confirmed by
 *   either reverse-engineering source.
 * @param unknown2 1 byte with zero documented behavior from either reverse-engineering source;
 *   confirmed absent only in the earliest [Civ3FormatEra.VANILLA] revision (`major=2`) — present
 *   from `major=3` onward (including [Civ3FormatEra.PTW] and [Civ3FormatEra.CONQUESTS]), read
 *   defensively; preserved raw, not validated.
 * @param victoryPointLocation `0` if this tile is a Victory Point Location, `-1` otherwise, per a
 *   separate reverse-engineered reference implementation; present only from [Civ3FormatEra.PTW]
 *   (`major=11`) onward, read defensively.
 * @param ruin Present only from [Civ3FormatEra.PTW] (`major=11`) onward, read defensively.
 * @param c3cOverlays 4 bytes, present only in [Civ3FormatEra.CONQUESTS] files, read defensively;
 *   preserved raw, not decomposed. Opaque — neither reverse-engineering source names any bits for this field
 *   (see [TerrEntry.terrainFlags]'s KDoc for which sources were checked). Open question: the
 *   Conquests Rules Editor shows 4 per-tile checkboxes with no home anywhere in this type's
 *   already-named bit fields — Airfield, Outpost, Radar Tower, and Barricade (as distinct from
 *   `TerrEntry`'s allow-airfields/allow-outposts/allow-radar-towers terrain-type *permissions* —
 *   these are per-tile *built* state). All 4 are Conquests-only concepts, making this field (or
 *   [c3cBonuses]/[unknown3]/[unknown4], also [Civ3FormatEra.CONQUESTS]-only and opaque) the most
 *   likely home; unconfirmed pending byte-level validation.
 * @param unknown3 1 byte with zero documented behavior from either reverse-engineering source;
 *   present only in [Civ3FormatEra.CONQUESTS] files, read defensively; preserved raw, not
 *   validated.
 * @param c3cTerrain A near-duplicate of [terrain]'s packed nibble pair, present only in
 *   [Civ3FormatEra.CONQUESTS] files (read defensively) — numerically identical to [terrain] in
 *   the vast majority of real samples, differing only where the tile's base and overlay terrain
 *   genuinely differ.
 * @param unknown4 2 bytes with zero documented behavior from either reverse-engineering source;
 *   present only in [Civ3FormatEra.CONQUESTS] files, read defensively; preserved raw, not
 *   validated.
 * @param fogOfWar Present only in [Civ3FormatEra.CONQUESTS] files, read defensively.
 * @param c3cBonuses 4 bytes, present only in [Civ3FormatEra.CONQUESTS] files, read defensively;
 *   preserved raw, not decomposed. Opaque, same treatment as [c3cOverlays].
 * @param unknown5 2 bytes with zero documented behavior from either reverse-engineering source;
 *   present only in [Civ3FormatEra.CONQUESTS] files, read defensively; preserved raw, not
 *   validated.
 * @param unknown6 4 bytes, undocumented by any reverse-engineering source and not part of a
 *   separate reverse-engineered reference implementation's struct at all. Present only in a
 *   small minority of [Civ3FormatEra.CONQUESTS] (`major=12`)
 *   files, always zero-valued. Requires the file's `VER#` header `minor=6`, but `minor=6` alone
 *   does not predict it — most `minor=6` [Civ3FormatEra.CONQUESTS] files lack this field, and the
 *   distinguishing factor is unknown (not file modification time, header description text, or
 *   `GAME` rule flags). Read defensively; preserved raw, not validated. The only known example of
 *   minor-dependent structure *within* [Civ3FormatEra.CONQUESTS] beyond `GAME`'s own case.
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
