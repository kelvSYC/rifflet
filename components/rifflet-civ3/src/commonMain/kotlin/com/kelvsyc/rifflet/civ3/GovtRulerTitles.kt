package com.kelvsyc.rifflet.civ3

/**
 * The government's ruler titles, one male/female pair per numbered rank.
 *
 * Corresponds to the Conquests Rules Editor's `Governments` tab's "Ruler Titles" groupbox, in its
 * entirety. See [GovtEntry.rulerTitlePairsUsed] for how many of these 4 pairs are actually used.
 *
 * @param male1 The first numbered pair's male title (e.g. "Revolutionary").
 * @param female1 The first numbered pair's female title.
 * @param male2 The second numbered pair's male title (e.g. "Leader").
 * @param female2 The second numbered pair's female title.
 * @param male3 The third numbered pair's male title.
 * @param female3 The third numbered pair's female title.
 * @param male4 The fourth numbered pair's male title.
 * @param female4 The fourth numbered pair's female title.
 */
data class GovtRulerTitles(
    val male1: String,
    val female1: String,
    val male2: String,
    val female2: String,
    val male3: String,
    val female3: String,
    val male4: String,
    val female4: String,
)
