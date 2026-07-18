package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.BICX_MAGIC
import com.kelvsyc.rifflet.civ3.BIC_MAGIC
import com.kelvsyc.rifflet.civ3.Civ3SectionIds
import com.kelvsyc.rifflet.core.ChunkId

/**
 * Maximum real-data-confirmed item byte size for sections known to have a version-dependent
 * layout, keyed by the exact `(file magic, VER# header major)` pair a real sample was found at —
 * never a collapsed vanilla/PTW/Conquests bucket. This distinction is load-bearing: `TILE` items
 * are 22 bytes at major=2 but 23 bytes at major=3/4, all three of which are "vanilla" under the
 * coarser era grouping used elsewhere in this codebase's KDoc.
 *
 * Only combinations with a directly-observed real sample are listed. Any other combination —
 * including every major=2/3 case for `DIFF`/`ERAS`/`BLDG`/`CTZN`/`GOVT`, which this project has
 * zero real samples for — intentionally has no entry, so [maxSizeFor] returns `null` and no limit
 * is enforced. `TECH`/`UNIT`/`RULE` also have defensive parsing but documentation-derived (not
 * real-data-confirmed) tier sizes, so they are deliberately excluded here entirely.
 */
internal object Civ3ItemSizeLimits {
    private val LIMITS: Map<ChunkId, Map<Pair<ChunkId, Int>, Int>> = mapOf(
        Civ3SectionIds.BLDG to mapOf(
            (BIC_MAGIC to 4) to 252,
            (BICX_MAGIC to 11) to 252,
            (BICX_MAGIC to 12) to 268,
        ),
        Civ3SectionIds.CTZN to mapOf(
            (BIC_MAGIC to 4) to 116,
            (BICX_MAGIC to 11) to 116,
            (BICX_MAGIC to 12) to 124,
        ),
        Civ3SectionIds.TILE to mapOf(
            (BIC_MAGIC to 2) to 22,
            (BIC_MAGIC to 3) to 23,
            (BIC_MAGIC to 4) to 23,
            (BICX_MAGIC to 11) to 29,
            (BICX_MAGIC to 12) to 45,
        ),
        Civ3SectionIds.DIFF to mapOf(
            (BIC_MAGIC to 4) to 116,
            (BICX_MAGIC to 11) to 120,
            (BICX_MAGIC to 12) to 120,
        ),
        Civ3SectionIds.ERAS to mapOf(
            (BIC_MAGIC to 4) to 260,
            (BICX_MAGIC to 11) to 260,
            (BICX_MAGIC to 12) to 264,
        ),
        Civ3SectionIds.GOVT to mapOf(
            (BIC_MAGIC to 4) to 536,
            (BICX_MAGIC to 11) to 536,
            (BICX_MAGIC to 12) to 568,
        ),
    )

    fun maxSizeFor(marker: ChunkId, magic: ChunkId, major: Int): Int? = LIMITS[marker]?.get(magic to major)
}
