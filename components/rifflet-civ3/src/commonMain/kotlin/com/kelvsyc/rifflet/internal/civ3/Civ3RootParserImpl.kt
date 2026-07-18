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
import com.kelvsyc.rifflet.civ3.UnitSection
import com.kelvsyc.rifflet.civ3.WchrSection
import com.kelvsyc.rifflet.civ3.WmapSection
import com.kelvsyc.rifflet.civ3.WsizSection
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
 * `RACE` sections need the entry count of the most recently parsed `ERAS` section, a genuine
 * cross-section parse-order dependency — [parse] tracks this in a local variable across the
 * section loop and passes it to [parseSection].
 *
 * `FLAV` is the sole exception to the length-prefixed-item framing described above — its items
 * have no length field of their own in the file format (confirmed by both Apolyton's
 * documentation and `QueryCiv3`'s `Flav.cs`, the only section struct without a leading `Length`
 * field), so [parseSection] reads it as a special case directly off the shared [BufferedSource],
 * bypassing the generic zero-copy-[Buffer] item slicing entirely. Each FLAV item is itself a
 * "flavor group" containing a nested dynamic list of flavors — see [FlavGroupEntryParser].
 */
internal object Civ3RootParserImpl {
    fun parse(source: BufferedSource): Civ3File {
        val header = Civ3HeaderParser.parse(source)
        val sections = mutableListOf<Civ3Section>()
        var erasCount: Int? = null
        while (!source.exhausted()) {
            val section = parseSection(source, erasCount)
            if (section is ErasSection) erasCount = section.entries.size
            sections += section
        }
        return Civ3File(header, sections)
    }

    private fun parseSection(source: BufferedSource, erasCount: Int?): Civ3Section {
        val marker = source.readChunkId()
        val count = source.readIntLe()
        if (marker == Civ3SectionIds.FLAV) {
            return FlavSection(List(count) { FlavGroupEntryParser.parse(source) })
        }
        val items = List(count) {
            val length = source.readIntLe()
            val data = Buffer()
            source.readFully(data, length.toLong())
            data
        }
        return when (marker) {
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
            Civ3SectionIds.PRTO -> PrtoSection(items.map { PrtoEntryParser.parse(it) })
            Civ3SectionIds.BLDG -> BldgSection(items.map { BldgEntryParser.parse(it) })
            Civ3SectionIds.TERR -> TerrSection(items.map { TerrEntryParser.parse(it) })
            Civ3SectionIds.GAME -> GameSection(items.map { GameEntryParser.parse(it) })
            else -> Civ3RawSection(marker, count, items.map { it.readByteString() })
        }
    }
}
