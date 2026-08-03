package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.EspnEntry
import com.kelvsyc.rifflet.civ3.ExprEntry
import com.kelvsyc.rifflet.civ3.GovtCorruption
import com.kelvsyc.rifflet.civ3.GovtHurrying
import com.kelvsyc.rifflet.civ3.GovtRelationship
import com.kelvsyc.rifflet.civ3.GovtRulerTitles
import com.kelvsyc.rifflet.civ3.GovtUnitSupportCosts
import com.kelvsyc.rifflet.civ3.GovtWarWeariness
import com.kelvsyc.rifflet.civ3.TechEntry
import okio.ByteString

/**
 * A government type, mutable and cross-referenced by real object references — the domain-layer
 * counterpart to [com.kelvsyc.rifflet.civ3.GovtEntry].
 *
 * Unlike [com.kelvsyc.rifflet.civ3.GovtEntry], this is a plain class, not a `data class`:
 * [relationships] can hold a reference back to this same instance (or to a sibling whose own
 * [relationships] map points back here), and a generated structural `equals`/`hashCode`/
 * `toString` would recurse through that cycle. Reference identity is also the correct semantics
 * here — two [Government]s with identical field values are still distinct entities.
 *
 * @param name The government's name.
 * @param civilopediaEntry Encyclopedia/Civilopedia entry text.
 * @param rulerTitles This government's ruler titles.
 * @param corruption This government's Corruption and Waste severity.
 * @param hurrying How this government allows hurrying production.
 * @param unitSupportCosts This government's unit support costs.
 * @param warWeariness This government's war weariness severity.
 * @param defaultType Marks this as the Default government (the one a civ starts with before
 *   researching any government tech).
 * @param transitionType Marks this as the Transition government (the one a civ falls into after
 *   a revolution).
 * @param requiresMaintenance Whether this government requires maintenance.
 * @param toggle1 Vestigial — carries leftover, uninitialized memory content rather than real
 *   government data. Stays `Int`, not `Boolean`: unlike [defaultType]/[transitionType], there is
 *   no real 0/1 semantic to convert.
 * @param tilePenalty This government's tile penalty.
 * @param tradeBonus This government's trade bonus.
 * @param assimilationChance This government's assimilation chance.
 * @param draftLimit This government's draft limit.
 * @param militaryPoliceLimit This government's military police limit.
 * @param rulerTitlePairsUsed How many of [rulerTitles]'s 4 pairs are actually used.
 * @param scienceRateCap This government's maximum science rate.
 * @param workerRate This government's worker rate.
 * @param toggle2 Vestigial, matching [toggle1].
 * @param toggle3 Vestigial, matching [toggle1].
 * @param unknown 4 bytes with zero documented behavior. Preserved raw, not validated.
 * @param xenophobic Whether this government is xenophobic.
 * @param forceResettle Whether this government forces resettlement.
 */
class Government(
    var name: String,
    var civilopediaEntry: String,
    var rulerTitles: GovtRulerTitles,
    var corruption: GovtCorruption,
    var hurrying: GovtHurrying,
    var unitSupportCosts: GovtUnitSupportCosts,
    var warWeariness: GovtWarWeariness,
    var defaultType: Boolean = false,
    var transitionType: Boolean = false,
    var requiresMaintenance: Boolean = false,
    var toggle1: Int = 0,
    var tilePenalty: Int = 0,
    var tradeBonus: Int = 0,
    var assimilationChance: Int = 0,
    var draftLimit: Int = 0,
    var militaryPoliceLimit: Int = 0,
    var rulerTitlePairsUsed: Int = 0,
    var scienceRateCap: Int = 0,
    var workerRate: Int = 0,
    var toggle2: Int = 0,
    var toggle3: Int = 0,
    var unknown: ByteString = ByteString.of(0, 0, 0, 0),
    var xenophobic: Boolean = false,
    var forceResettle: Boolean = false,
) {
    /**
     * References the wire `TechEntry` — `TECH` doesn't have its own domain type yet.
     */
    var prerequisiteTechnology: TechEntry? = null

    /**
     * References the wire `EspnEntry` — `ESPN` doesn't have its own domain type yet.
     */
    var immuneTo: EspnEntry? = null

    /**
     * References the wire `ExprEntry` — `EXPR` doesn't have its own domain type yet.
     */
    var diplomatsAre: ExprEntry? = null

    /**
     * References the wire `ExprEntry` — `EXPR` doesn't have its own domain type yet.
     */
    var spiesAre: ExprEntry? = null

    /**
     * How this government relates to every other government it will be encoded alongside,
     * keyed by real [Government] references rather than position. Entries for governments
     * outside a particular encode's roster are ignored; missing entries for governments inside
     * it default to zero.
     */
    val relationships: MutableMap<Government, GovtRelationship> = mutableMapOf()
}
