package com.kelvsyc.rifflet.civ3

/**
 * Resolves each id in [WmapEntry.resourceIds] against [goods], preserving position: the result
 * is the same length as [WmapEntry.resourceIds], with `null` at any position whose id doesn't
 * resolve. Likely `GOOD` section indices identifying which resources are available on this map —
 * inferred from `QueryCiv3`'s comment; not confirmed by Apolyton's documentation.
 */
fun WmapEntry.resourceIdsGood(goods: List<GoodEntry>): List<GoodEntry?> =
    resourceIds.map { goods.getOrNull(it) }
