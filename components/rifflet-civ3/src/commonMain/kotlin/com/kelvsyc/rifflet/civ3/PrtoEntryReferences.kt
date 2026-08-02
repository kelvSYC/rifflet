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
 *
 * Ordinal position deliberately matches the raw file values (0=land, 1=sea, 2=air) — do not
 * reorder these constants.
 */
enum class PrtoDomain { LAND, SEA, AIR }

/**
 * [PrtoEntry.skipTurn] resolved for [era]: reads [PrtoEntry.vanillaSkipTurn] for
 * [Civ3FormatEra.VANILLA] files, where real per-unit Standard Orders data lives, or the legacy
 * [PrtoEntry.skipTurn] otherwise.
 */
fun PrtoEntry.skipTurn(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaSkipTurn else skipTurn

/**
 * [PrtoEntry.wait] resolved for [era]. Same treatment as [skipTurn]'s era-resolved overload.
 */
fun PrtoEntry.wait(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaWait else wait

/**
 * [PrtoEntry.fortify] resolved for [era]. Same treatment as [skipTurn]'s era-resolved overload.
 */
fun PrtoEntry.fortify(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaFortify else fortify

/**
 * [PrtoEntry.disband] resolved for [era]. Same treatment as [skipTurn]'s era-resolved overload.
 */
fun PrtoEntry.disband(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaDisband else disband

/**
 * [PrtoEntry.goTo] resolved for [era]. Same treatment as [skipTurn]'s era-resolved overload.
 */
fun PrtoEntry.goTo(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaGoTo else goTo

/**
 * [PrtoEntry.load] resolved for [era]: reads [PrtoEntry.vanillaLoad] for [Civ3FormatEra.VANILLA]
 * files, where real per-unit Special Actions data lives, or the legacy [PrtoEntry.load] otherwise.
 */
fun PrtoEntry.load(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaLoad else load

/**
 * [PrtoEntry.unload] resolved for [era]. Same treatment as [load]'s era-resolved overload.
 */
fun PrtoEntry.unload(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaUnload else unload

/**
 * [PrtoEntry.airlift] resolved for [era]. Same treatment as [load]'s era-resolved overload.
 */
fun PrtoEntry.airlift(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaAirlift else airlift

/**
 * [PrtoEntry.pillage] resolved for [era]. Same treatment as [load]'s era-resolved overload.
 */
fun PrtoEntry.pillage(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaPillage else pillage

/**
 * [PrtoEntry.bombard] resolved for [era]. Same treatment as [load]'s era-resolved overload.
 */
fun PrtoEntry.bombard(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaBombard else bombard

/**
 * [PrtoEntry.airdrop] resolved for [era]. Same treatment as [load]'s era-resolved overload.
 */
fun PrtoEntry.airdrop(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaAirdrop else airdrop

/**
 * [PrtoEntry.buildArmy] resolved for [era]. Same treatment as [load]'s era-resolved overload.
 */
fun PrtoEntry.buildArmy(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaBuildArmy else buildArmy

/**
 * [PrtoEntry.finishImprovements] resolved for [era]. Same treatment as [load]'s era-resolved
 * overload.
 */
fun PrtoEntry.finishImprovements(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaFinishImprovements else finishImprovements

/**
 * [PrtoEntry.upgradeUnit] resolved for [era]. Same treatment as [load]'s era-resolved overload.
 */
fun PrtoEntry.upgradeUnit(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaUpgradeUnit else upgradeUnit

/**
 * [PrtoEntry.bombing] resolved for [era]: reads [PrtoEntry.vanillaBombing] for
 * [Civ3FormatEra.VANILLA] files, where real per-unit Air Missions data lives, or the legacy
 * [PrtoEntry.bombing] otherwise.
 */
fun PrtoEntry.bombing(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaBombing else bombing

/**
 * [PrtoEntry.recon] resolved for [era]. Same treatment as [bombing]'s era-resolved overload.
 */
fun PrtoEntry.recon(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaRecon else recon

/**
 * [PrtoEntry.interception] resolved for [era]. Same treatment as [bombing]'s era-resolved
 * overload.
 */
fun PrtoEntry.interception(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaInterception else interception

/**
 * [PrtoEntry.rebase] resolved for [era]. Same treatment as [bombing]'s era-resolved overload.
 */
fun PrtoEntry.rebase(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaRebase else rebase

/**
 * [PrtoEntry.precisionBombing] resolved for [era]. Same treatment as [bombing]'s era-resolved
 * overload.
 */
fun PrtoEntry.precisionBombing(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaPrecisionBombing else precisionBombing

/**
 * [PrtoEntry.buildColony] resolved for [era]: reads [PrtoEntry.vanillaBuildColony] for
 * [Civ3FormatEra.VANILLA] files, where real per-unit Worker/Engineer Actions data lives, or the
 * legacy [PrtoEntry.buildColony] otherwise.
 */
fun PrtoEntry.buildColony(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaBuildColony else buildColony

/**
 * [PrtoEntry.buildCity] resolved for [era]. Same treatment as [buildColony]'s era-resolved
 * overload.
 */
fun PrtoEntry.buildCity(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaBuildCity else buildCity

/**
 * [PrtoEntry.buildRoad] resolved for [era]. Same treatment as [buildColony]'s era-resolved
 * overload.
 */
fun PrtoEntry.buildRoad(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaBuildRoad else buildRoad

/**
 * [PrtoEntry.buildRailroad] resolved for [era]. Same treatment as [buildColony]'s era-resolved
 * overload.
 */
fun PrtoEntry.buildRailroad(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaBuildRailroad else buildRailroad

/**
 * [PrtoEntry.buildFort] resolved for [era]. Same treatment as [buildColony]'s era-resolved
 * overload.
 */
fun PrtoEntry.buildFort(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaBuildFort else buildFort

/**
 * [PrtoEntry.buildMine] resolved for [era]. Same treatment as [buildColony]'s era-resolved
 * overload.
 */
fun PrtoEntry.buildMine(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaBuildMine else buildMine

/**
 * [PrtoEntry.irrigate] resolved for [era]. Same treatment as [buildColony]'s era-resolved
 * overload.
 */
fun PrtoEntry.irrigate(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaIrrigate else irrigate

/**
 * [PrtoEntry.clearForest] resolved for [era]. Same treatment as [buildColony]'s era-resolved
 * overload.
 */
fun PrtoEntry.clearForest(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaClearForest else clearForest

/**
 * [PrtoEntry.clearJungle] resolved for [era]. Same treatment as [buildColony]'s era-resolved
 * overload.
 */
fun PrtoEntry.clearJungle(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaClearJungle else clearJungle

/**
 * [PrtoEntry.plantForest] resolved for [era]. Same treatment as [buildColony]'s era-resolved
 * overload.
 */
fun PrtoEntry.plantForest(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaPlantForest else plantForest

/**
 * [PrtoEntry.clearPollution] resolved for [era]. Same treatment as [buildColony]'s era-resolved
 * overload.
 */
fun PrtoEntry.clearPollution(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaClearPollution else clearPollution

/**
 * [PrtoEntry.automate] resolved for [era]. Same treatment as [buildColony]'s era-resolved
 * overload.
 */
fun PrtoEntry.automate(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaAutomate else automate

/**
 * [PrtoEntry.joinCity] resolved for [era]. Same treatment as [buildColony]'s era-resolved
 * overload.
 */
fun PrtoEntry.joinCity(era: Civ3FormatEra): Boolean =
    if (era == Civ3FormatEra.VANILLA) vanillaJoinCity else joinCity

