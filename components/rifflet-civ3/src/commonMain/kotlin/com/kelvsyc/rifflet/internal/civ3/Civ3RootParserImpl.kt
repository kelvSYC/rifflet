package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.BldgSection
import com.kelvsyc.rifflet.civ3.CitySection
import com.kelvsyc.rifflet.civ3.Civ3File
import com.kelvsyc.rifflet.civ3.Civ3RawSection
import com.kelvsyc.rifflet.civ3.Civ3Section
import com.kelvsyc.rifflet.civ3.Civ3SectionIds
import com.kelvsyc.rifflet.civ3.ClnySection
import com.kelvsyc.rifflet.civ3.ContSection
import com.kelvsyc.rifflet.civ3.CtznSection
import com.kelvsyc.rifflet.civ3.CultSection
import com.kelvsyc.rifflet.civ3.DiffSection
import com.kelvsyc.rifflet.civ3.ErasSection
import com.kelvsyc.rifflet.civ3.EspnSection
import com.kelvsyc.rifflet.civ3.ExprSection
import com.kelvsyc.rifflet.civ3.FlavSection
import com.kelvsyc.rifflet.civ3.GameSection
import com.kelvsyc.rifflet.civ3.GoodSection
import com.kelvsyc.rifflet.civ3.GovtSection
import com.kelvsyc.rifflet.civ3.LeadSection
import com.kelvsyc.rifflet.civ3.PrtoSection
import com.kelvsyc.rifflet.civ3.RaceSection
import com.kelvsyc.rifflet.civ3.RuleSection
import com.kelvsyc.rifflet.civ3.SlocSection
import com.kelvsyc.rifflet.civ3.TechSection
import com.kelvsyc.rifflet.civ3.TerrSection
import com.kelvsyc.rifflet.civ3.TfrmSection
import com.kelvsyc.rifflet.civ3.TileSection
import com.kelvsyc.rifflet.civ3.UnitSection
import com.kelvsyc.rifflet.civ3.WchrSection
import com.kelvsyc.rifflet.civ3.WmapSection
import com.kelvsyc.rifflet.civ3.WsizSection
import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.internal.core.readChunkId
import okio.Buffer
import okio.BufferedSource

/**
 * Parses the flat, non-nested sequence of Civ3 sections that follows the `VER#` header: each
 * section is a 4-byte marker, a little-endian 4-byte item count, then that many length-prefixed
 * items. Items are read as zero-copy-transferred [Buffer]s (`source.readFully`), matching this
 * codebase's existing T3 block-parsing discipline — [okio.ByteString] is materialized only for
 * markers with no dedicated type, where [Civ3RawSection]'s immutable domain value genuinely
 * requires one. The caller is expected to have already consumed the leading file magic.
 *
 * `RACE` sections need the entry count of the most recently parsed `ERAS` section — a genuine
 * cross-section parse-order dependency, since `ERAS` always precedes `RACE` in the file's fixed
 * section order — [parse] tracks this in a local variable across the section loop and passes it
 * to [parseSection].
 *
 * `PRTO` sections likewise need the entry count of the file's `TERR` section, but the file's
 * fixed, non-alphabetical section order always places `PRTO` *before* `TERR` — the opposite of
 * the `ERAS`/`RACE` relationship.
 * [parseSection] cannot resolve `PRTO` immediately, so [parse] defers it: it stashes `PRTO`'s raw
 * items and its position in the eventual [Civ3Section] list, keeps scanning the rest of the file
 * (so `TERR`, wherever it falls, is still reached), and only parses the stashed items into a
 * [PrtoSection] — inserted back at its original position — once the loop finishes and `TERR`'s
 * count is known. This preserves single-pass, streaming-friendly I/O: no seeking backward or
 * buffering the whole file up front, only holding onto `PRTO`'s already-read raw item buffers a
 * little longer than usual.
 *
 * `FLAV` is the sole exception to the length-prefixed-item framing described above — its items
 * have no length field of their own in the file format (confirmed by both existing
 * reverse-engineering documentation and a separate reverse-engineered reference implementation's
 * equivalent struct, the only section struct without a leading `Length` field), so [parseSection]
 * reads it as a special case directly off the shared [BufferedSource],
 * bypassing the generic zero-copy-[Buffer] item slicing entirely. Each FLAV item is itself a
 * "flavor group" containing a nested dynamic list of flavors — see [FlavGroupEntryParser].
 *
 * Every section's own item count (and `FLAV`'s group count) is passed through
 * [requireSaneCount] before it sizes any allocation — see that function's KDoc for why.
 */
internal object Civ3RootParserImpl {
    fun parse(source: BufferedSource, magic: ChunkId): Civ3File {
        val header = Civ3HeaderParser.parse(source)
        val sections = mutableListOf<Civ3Section>()
        var erasCount: Int? = null
        var terrCount: Int? = null
        var pendingPrtoItems: List<Buffer>? = null
        var pendingPrtoIndex = -1
        while (!source.exhausted()) {
            when (val parsed = parseSection(source, erasCount, magic, header.major)) {
                is ParsedSection.Ready -> {
                    val section = parsed.section
                    if (section is ErasSection) erasCount = section.entries.size
                    if (section is TerrSection) terrCount = section.entries.size
                    sections += section
                }
                is ParsedSection.DeferredPrto -> {
                    pendingPrtoItems = parsed.items
                    pendingPrtoIndex = sections.size
                }
            }
        }
        if (pendingPrtoItems != null) {
            val terr = terrCount
                ?: throw RiffletParseException("PRTO section requires a TERR section to appear somewhere in the file")
            sections.add(pendingPrtoIndex, PrtoSection(pendingPrtoItems.map { PrtoEntryParser.parse(it, terr) }))
        }
        return Civ3File(header, sections)
    }

    /** [parseSection]'s internal result: either a fully-resolved [Civ3Section], or — for `PRTO`
     * only — its raw, not-yet-entry-parsed items, deferred until [parse] knows `TERR`'s count.
     * Never exposed outside this file; must never appear in [Civ3File.sections]. */
    private sealed interface ParsedSection {
        data class Ready(val section: Civ3Section) : ParsedSection
        data class DeferredPrto(val items: List<Buffer>) : ParsedSection
    }

    private fun parseSection(
        source: BufferedSource,
        erasCount: Int?,
        magic: ChunkId,
        major: Int,
    ): ParsedSection {
        val marker = source.readChunkId()
        val count = source.readIntLe()
        if (marker == Civ3SectionIds.FLAV) {
            val flavGroupCount = source.requireSaneCount(count, 4L, "FLAV")
            return ParsedSection.Ready(FlavSection(List(flavGroupCount) { FlavGroupEntryParser.parse(source) }))
        }
        val itemCount = source.requireSaneCount(count, 4L, "${marker.name} item count")
        val items = List(itemCount) {
            val length = source.readIntLe()
            val limit = Civ3ItemSizeLimits.maxSizeFor(marker, magic, major)
            if (limit != null && length > limit) {
                throw RiffletParseException(
                    "${marker.name} item is $length bytes, exceeding the confirmed maximum of " +
                        "$limit bytes for magic=${magic.name} major=$major",
                )
            }
            val data = Buffer()
            source.readFully(data, length.toLong())
            data
        }
        if (marker == Civ3SectionIds.PRTO) {
            return ParsedSection.DeferredPrto(items)
        }
        val section = when (marker) {
            Civ3SectionIds.WSIZ -> WsizSection(items.map { WsizEntryParser.parse(it) })
            Civ3SectionIds.DIFF -> DiffSection(items.map { DiffEntryParser.parse(it) })
            Civ3SectionIds.ERAS -> ErasSection(items.map { ErasEntryParser.parse(it) })
            Civ3SectionIds.GOVT -> GovtSection(items.map { GovtEntryParser.parse(it) })
            Civ3SectionIds.RACE -> {
                val eras = erasCount
                    ?: throw RiffletParseException("RACE section requires an ERAS section to appear first in the file")
                RaceSection(items.map { RaceEntryParser.parse(it, eras) })
            }
            Civ3SectionIds.EXPR -> ExprSection(items.map { ExprEntryParser.parse(it) })
            Civ3SectionIds.CULT -> CultSection(items.map { CultEntryParser.parse(it) })
            Civ3SectionIds.CTZN -> CtznSection(items.map { CtznEntryParser.parse(it) })
            Civ3SectionIds.GOOD -> GoodSection(items.map { GoodEntryParser.parse(it) })
            Civ3SectionIds.ESPN -> EspnSection(items.map { EspnEntryParser.parse(it) })
            Civ3SectionIds.SLOC -> SlocSection(items.map { SlocEntryParser.parse(it) })
            Civ3SectionIds.CONT -> ContSection(items.map { ContEntryParser.parse(it) })
            Civ3SectionIds.WCHR -> WchrSection(items.map { WchrEntryParser.parse(it) })
            Civ3SectionIds.CLNY -> ClnySection(items.map { ClnyEntryParser.parse(it) })
            Civ3SectionIds.TFRM -> TfrmSection(items.map { TfrmEntryParser.parse(it) })
            Civ3SectionIds.WMAP -> WmapSection(items.map { WmapEntryParser.parse(it) })
            Civ3SectionIds.UNIT -> UnitSection(items.map { UnitEntryParser.parse(it) })
            Civ3SectionIds.CITY -> CitySection(items.map { CityEntryParser.parse(it) })
            Civ3SectionIds.TECH -> TechSection(items.map { TechEntryParser.parse(it) })
            Civ3SectionIds.LEAD -> LeadSection(items.map { LeadEntryParser.parse(it) })
            Civ3SectionIds.RULE -> RuleSection(items.map { RuleEntryParser.parse(it) })
            Civ3SectionIds.BLDG -> BldgSection(items.map { BldgEntryParser.parse(it) })
            Civ3SectionIds.TERR -> TerrSection(items.map { TerrEntryParser.parse(it) })
            Civ3SectionIds.GAME -> GameSection(items.map { GameEntryParser.parse(it) })
            Civ3SectionIds.TILE -> TileSection(items.map { TileEntryParser.parse(it) })
            else -> Civ3RawSection(marker, count, items.map { it.readByteString() })
        }
        return ParsedSection.Ready(section)
    }
}
