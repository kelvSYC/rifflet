package com.kelvsyc.rifflet.civ3

/**
 * Resolves [PrtoEntry.required] against [techs].
 */
fun PrtoEntry.requiredTech(techs: List<TechEntry>): TechEntry? = techs.getOrNull(required)

/**
 * Resolves [PrtoUnitStatistics.upgradeTo] against [prtos] (the same `PRTO` section this entry
 * came from).
 */
fun PrtoEntry.upgradeToPrto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(unitStatistics.upgradeTo)

/**
 * Resolves [PrtoEntry.otherStrategy] against [prtos] (the same `PRTO` section this entry came
 * from). See [PrtoEntry.otherStrategy]'s KDoc for why this is a self-reference rather than an AI
 * strategy despite the field's inherited name.
 */
fun PrtoEntry.otherStrategyPrto(prtos: List<PrtoEntry>): PrtoEntry? = prtos.getOrNull(otherStrategy)

/**
 * The AI Strategy bits ([PrtoEntry.aiStrategies]) this entry has in effect, per the real Units
 * editor's merged display: this entry's own bits OR'd with any linked entry's bits (see
 * [PrtoEntry.otherStrategy] for how a unit's second AI Strategy can live on a separate `PRTO`
 * entry). The link is checked in both directions, since [PrtoEntry.otherStrategy] only points from
 * the duplicate entry back to the canonical one — computing this on the canonical entry needs the
 * reverse direction to find its duplicate.
 */
fun PrtoEntry.effectiveAiStrategies(prtos: List<PrtoEntry>): Int {
    val selfIndex = prtos.indexOf(this)
    val reverseLinked = if (selfIndex >= 0) prtos.filter { it.otherStrategy == selfIndex } else emptyList()
    val linked = reverseLinked + listOfNotNull(otherStrategyPrto(prtos))
    return linked.fold(aiStrategies) { acc, entry -> acc or entry.aiStrategies }
}

/**
 * Resolves [PrtoEntry.requiredResource1] against [goods]. Same treatment applies to
 * [PrtoEntry.requiredResource2] and [PrtoEntry.requiredResource3].
 */
fun PrtoEntry.requiredResource1Good(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(requiredResource1)

/**
 * Resolves [PrtoEntry.requiredResource2] against [goods].
 */
fun PrtoEntry.requiredResource2Good(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(requiredResource2)

/**
 * Resolves [PrtoEntry.requiredResource3] against [goods].
 */
fun PrtoEntry.requiredResource3Good(goods: List<GoodEntry>): GoodEntry? =
    goods.getOrNull(requiredResource3)

/**
 * Resolves each id in [PrtoEntry.stealthTargetUnitTypes] against [prtos] (the same `PRTO`
 * section this entry came from), preserving position: the result is the same length as
 * [PrtoEntry.stealthTargetUnitTypes], with `null` at any position whose id doesn't resolve. See
 * [PrtoEntry.stealthTargetUnitTypes]'s KDoc: these are the units Stealth Attack CANNOT target,
 * not a list of valid targets.
 */
fun PrtoEntry.stealthTargetUnitTypesPrto(prtos: List<PrtoEntry>): List<PrtoEntry?> =
    stealthTargetUnitTypes.map { prtos.getOrNull(it) }

/**
 * Resolves [PrtoEntry.availableTo] against [races] (the file's own `RACE` section): the
 * [RaceEntry] values whose index this unit is available to, in `RACE` index order.
 */
fun PrtoEntry.availableToRaces(races: List<RaceEntry>): List<RaceEntry> =
    races.filterIndexed { index, _ -> availableTo and (1 shl index) != 0 }

/**
 * The 3 documented values of [PrtoEntry.type], per the Conquests Rules Editor's "Class" control.
 * Ordinal position deliberately matches the raw file values (0=land, 1=sea, 2=air) — do not
 * reorder these constants.
 */
enum class PrtoDomain { LAND, SEA, AIR }

/**
 * Decodes [PrtoEntry.type] into [PrtoDomain], or `null` if the raw value is outside the
 * documented 0-2 range.
 */
val PrtoEntry.domainEnum: PrtoDomain?
    get() = PrtoDomain.entries.getOrNull(type)
