package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.GoodEntry
import com.kelvsyc.rifflet.civ3.PrtoEntry
import com.kelvsyc.rifflet.civ3.TerrEntry
import com.kelvsyc.rifflet.civ3.airdrop
import com.kelvsyc.rifflet.civ3.airlift
import com.kelvsyc.rifflet.civ3.automate
import com.kelvsyc.rifflet.civ3.bombard
import com.kelvsyc.rifflet.civ3.bombing
import com.kelvsyc.rifflet.civ3.buildAirfield
import com.kelvsyc.rifflet.civ3.buildArmy
import com.kelvsyc.rifflet.civ3.buildBarricade
import com.kelvsyc.rifflet.civ3.buildCity
import com.kelvsyc.rifflet.civ3.buildColony
import com.kelvsyc.rifflet.civ3.buildFort
import com.kelvsyc.rifflet.civ3.buildMine
import com.kelvsyc.rifflet.civ3.buildOutpost
import com.kelvsyc.rifflet.civ3.buildRadarTower
import com.kelvsyc.rifflet.civ3.buildRailroad
import com.kelvsyc.rifflet.civ3.buildRoad
import com.kelvsyc.rifflet.civ3.capture
import com.kelvsyc.rifflet.civ3.clearForest
import com.kelvsyc.rifflet.civ3.clearJungle
import com.kelvsyc.rifflet.civ3.clearPollution
import com.kelvsyc.rifflet.civ3.disband
import com.kelvsyc.rifflet.civ3.enslave
import com.kelvsyc.rifflet.civ3.explore
import com.kelvsyc.rifflet.civ3.finishImprovements
import com.kelvsyc.rifflet.civ3.fortify
import com.kelvsyc.rifflet.civ3.goTo
import com.kelvsyc.rifflet.civ3.interception
import com.kelvsyc.rifflet.civ3.irrigate
import com.kelvsyc.rifflet.civ3.joinCity
import com.kelvsyc.rifflet.civ3.load
import com.kelvsyc.rifflet.civ3.findSelfReferenceCycle
import com.kelvsyc.rifflet.civ3.pillage
import com.kelvsyc.rifflet.civ3.plantForest
import com.kelvsyc.rifflet.civ3.precisionBombing
import com.kelvsyc.rifflet.civ3.rebase
import com.kelvsyc.rifflet.civ3.recon
import com.kelvsyc.rifflet.civ3.sacrifice
import com.kelvsyc.rifflet.civ3.sentry
import com.kelvsyc.rifflet.civ3.skipTurn
import com.kelvsyc.rifflet.civ3.startsScienceAge
import com.kelvsyc.rifflet.civ3.stealthAttack
import com.kelvsyc.rifflet.civ3.unload
import com.kelvsyc.rifflet.civ3.upgradeUnit
import com.kelvsyc.rifflet.civ3.wait
import com.kelvsyc.rifflet.civ3.PrtoUnitStatistics as WirePrtoUnitStatistics
import okio.Buffer
import okio.ByteString

/**
 * Converts a parsed `PRTO` section to its domain-layer form.
 *
 * Groups each canonical wire entry (`otherStrategy == -1`) with every duplicate entry that targets
 * it (`otherStrategy` pointing back at the canonical's index) into ONE [Prto], whose [Prto.aiStrategies]
 * is the OR of the canonical's own bit and every targeting duplicate's bit — mirroring the real
 * Units editor's own merged display. A duplicate entry never produces its own [Prto]. This means
 * the returned list can be shorter than [this] — every real duplicate entry disappears into its
 * canonical's merged [Prto].
 *
 * [era] resolves every VANILLA-vs-later storage-location quirk (see [Prto]'s own KDoc).
 * [techs]/[races] are the already domain-converted `TECH`/`RACE` lists; [goods]/[terrs] stay wire
 * types (`GOOD`/`TERR` don't have domain types yet).
 *
 * Throws [IllegalArgumentException] if this list's [com.kelvsyc.rifflet.civ3.PrtoUnitStatistics.upgradeTo]
 * graph contains a cycle — checked via [findSelfReferenceCycle] before constructing any [Prto],
 * exactly like `BldgEntry.toDomain()`'s cycle guard. `enslaveResults` gets no such guard (see
 * [Prto.enslaveResults]'s KDoc).
 */
fun List<PrtoEntry>.toDomain(
    era: Civ3FormatEra,
    techs: List<Tech>,
    goods: List<GoodEntry>,
    races: List<Race>,
    terrs: List<TerrEntry>,
): List<Prto> {
    val upgradeToCycle = findSelfReferenceCycle(this) { it.unitStatistics.upgradeTo }
    require(upgradeToCycle == null) {
        "PrtoEntry upgradeTo graph contains a cycle: ${upgradeToCycle?.joinToString(" -> ") { it.name }}"
    }

    val canonicalIndices = indices.filter { this[it].otherStrategy == -1 }
    val duplicatesByCanonical: Map<Int, List<Int>> = indices.filter { this[it].otherStrategy != -1 }
        .groupBy { this[it].otherStrategy }

    val mergedAiStrategies = canonicalIndices.map { canonicalIndex ->
        val duplicateBits = (duplicatesByCanonical[canonicalIndex] ?: emptyList()).map { this[it].aiStrategies }
        duplicateBits.fold(this[canonicalIndex].aiStrategies) { acc, bits -> acc or bits }
    }

    val prtos = canonicalIndices.mapIndexed { outputPosition, canonicalIndex ->
        val entry = this[canonicalIndex]
        Prto(
            name = entry.name,
            civilopediaEntry = entry.civilopediaEntry,
            iconIndex = entry.iconIndex,
            type = entry.type,
            abilities = entry.abilities,
            aiStrategies = mergedAiStrategies[outputPosition],
            unknown = entry.unknown,
            unknown2 = entry.unknown2,
            unknown3 = entry.unknown3,
            unknown4 = entry.unknown4,
            skipTurn = entry.skipTurn(era),
            wait = entry.wait(era),
            fortify = entry.fortify(era),
            disband = entry.disband(era),
            goTo = entry.goTo(era),
            load = entry.load(era),
            unload = entry.unload(era),
            airlift = entry.airlift(era),
            pillage = entry.pillage(era),
            bombard = entry.bombard(era),
            airdrop = entry.airdrop(era),
            buildArmy = entry.buildArmy(era),
            finishImprovements = entry.finishImprovements(era),
            upgradeUnit = entry.upgradeUnit(era),
            buildColony = entry.buildColony(era),
            buildCity = entry.buildCity(era),
            buildRoad = entry.buildRoad(era),
            buildRailroad = entry.buildRailroad(era),
            buildFort = entry.buildFort(era),
            buildMine = entry.buildMine(era),
            irrigate = entry.irrigate(era),
            clearForest = entry.clearForest(era),
            clearJungle = entry.clearJungle(era),
            plantForest = entry.plantForest(era),
            clearPollution = entry.clearPollution(era),
            automate = entry.automate(era),
            joinCity = entry.joinCity(era),
            bombing = entry.bombing(era),
            recon = entry.recon(era),
            interception = entry.interception(era),
            rebase = entry.rebase(era),
            precisionBombing = entry.precisionBombing(era),
            explore = entry.explore,
            sentry = entry.sentry,
            capture = entry.capture,
            stealthAttack = entry.stealthAttack,
            enslave = entry.enslave,
            sacrifice = entry.sacrifice,
            startsScienceAge = entry.startsScienceAge,
            buildAirfield = entry.buildAirfield,
            buildRadarTower = entry.buildRadarTower,
            buildOutpost = entry.buildOutpost,
            buildBarricade = entry.buildBarricade,
        )
    }

    // Maps every original wire index (canonical or duplicate) to the output Prto that absorbed it,
    // so a self-reference that happens to target a duplicate's original index still resolves.
    val prtoForWireIndex = mutableMapOf<Int, Prto>()
    canonicalIndices.forEachIndexed { outputPosition, canonicalIndex ->
        prtoForWireIndex[canonicalIndex] = prtos[outputPosition]
        duplicatesByCanonical[canonicalIndex]?.forEach { duplicateIndex ->
            prtoForWireIndex[duplicateIndex] = prtos[outputPosition]
        }
    }

    canonicalIndices.forEachIndexed { outputPosition, canonicalIndex ->
        val entry = this[canonicalIndex]
        val prto = prtos[outputPosition]
        prto.unitStatistics = PrtoUnitStatistics(
            zoneOfControl = entry.unitStatistics.zoneOfControl,
            bombardStrength = entry.unitStatistics.bombardStrength,
            bombardRange = entry.unitStatistics.bombardRange,
            capacity = entry.unitStatistics.capacity,
            shieldCost = entry.unitStatistics.shieldCost,
            defense = entry.unitStatistics.defense,
            attack = entry.unitStatistics.attack,
            operationalRange = entry.unitStatistics.operationalRange,
            populationCost = entry.unitStatistics.populationCost,
            rateOfFire = entry.unitStatistics.rateOfFire,
            movement = entry.unitStatistics.movement,
            upgradeTo = prtoForWireIndex[entry.unitStatistics.upgradeTo],
            hpBonus = entry.unitStatistics.hpBonus,
            bombardEffects = entry.unitStatistics.bombardEffects ?: 0,
            requireSupport = entry.unitStatistics.requireSupport ?: 0,
            createCraters = entry.unitStatistics.createCraters ?: 0,
            workerStrength = entry.unitStatistics.workerStrength ?: 0f,
            airDefense = entry.unitStatistics.airDefense ?: 0,
        )
        prto.required = techs.getOrNull(entry.required)
        prto.requiredResources = mutableListOf(
            goods.getOrNull(entry.requiredResource1),
            goods.getOrNull(entry.requiredResource2),
            goods.getOrNull(entry.requiredResource3),
        )
        prto.availableTo = races.filterIndexed { index, _ -> entry.availableTo and (1 shl index) != 0 }.toMutableSet()
        prto.enslaveResults = prtoForWireIndex[entry.enslaveResults]
        prto.ignoreMovementCost = terrs.filterIndexed { index, _ ->
            val byteIndex = index / 8
            val bitIndex = index % 8
            byteIndex < entry.ignoreMovementCost.size && (entry.ignoreMovementCost[byteIndex].toInt() and (1 shl bitIndex)) != 0
        }.toMutableSet()
        prto.stealthTargetUnitTypes = entry.stealthTargetUnitTypes.mapNotNull { prtoForWireIndex[it] }.toMutableSet()
    }

    return prtos
}

/**
 * Converts a `PRTO` section's domain-layer form back to wire entries, resolving each [Prto]'s
 * object references back into indices, and splitting any [Prto] whose [Prto.aiStrategies] has more
 * than 1 bit set into 1 canonical wire entry (the lowest set bit) plus 1 duplicate wire entry per
 * remaining bit in ascending order — the exact reverse of `toDomain()`'s merge. This means the
 * returned list can be longer than [this].
 *
 * [era] decides which storage location every action-boolean field is written into (see [Prto]'s
 * own KDoc) and recomputes `flags4`'s fully game-computed "actions mix" bits from the final
 * standard-orders/special-actions/worker-actions/air-missions values.
 *
 * Throws [IllegalArgumentException] if any cross-reference resolves to an object not present in
 * the corresponding list argument. Since every [Prto] is a `data class`, self-referencing
 * `indexOf` lookups are structural-equality matches, not true reference identity — the same
 * accepted limitation already documented on GOVT's/TECH's/BLDG's `toWire()`.
 */
fun List<Prto>.toWire(
    era: Civ3FormatEra,
    techs: List<Tech>,
    goods: List<GoodEntry>,
    races: List<Race>,
    terrs: List<TerrEntry>,
): List<PrtoEntry> {
    val roster = this
    val entryCounts = roster.map { maxOf(1, Integer.bitCount(it.aiStrategies)) }
    val canonicalWireIndex = IntArray(roster.size)
    var runningIndex = 0
    roster.forEachIndexed { i, _ ->
        canonicalWireIndex[i] = runningIndex
        runningIndex += entryCounts[i]
    }

    fun resolvePrto(field: String, target: Prto?): Int = target?.let {
        val index = roster.indexOf(it)
        require(index >= 0) { "Prto.$field references a Prto not present in this list" }
        canonicalWireIndex[index]
    } ?: -1

    fun resolveTech(target: Tech?): Int = target?.let {
        val index = techs.indexOf(it)
        require(index >= 0) { "Prto.required references a Tech not present in techs" }
        index
    } ?: -1

    fun resolveGood(field: String, target: GoodEntry?): Int = target?.let {
        val index = goods.indexOf(it)
        require(index >= 0) { "Prto.$field references a GoodEntry not present in goods" }
        index
    } ?: -1

    fun resolveRace(target: Race): Int {
        val index = races.indexOf(target)
        require(index >= 0) { "Prto.availableTo references a Race not present in races" }
        return index
    }

    fun resolveTerr(target: TerrEntry): Int {
        val index = terrs.indexOf(target)
        require(index >= 0) { "Prto.ignoreMovementCost references a TerrEntry not present in terrs" }
        return index
    }

    fun buildStandardOrders(prto: Prto): Int =
        (if (prto.skipTurn) 1 shl 0 else 0) or (if (prto.wait) 1 shl 1 else 0) or
            (if (prto.fortify) 1 shl 2 else 0) or (if (prto.disband) 1 shl 3 else 0) or
            (if (prto.goTo) 1 shl 4 else 0) or (if (prto.explore) 1 shl 5 else 0) or
            (if (prto.sentry) 1 shl 6 else 0)

    fun buildSpecialActions(prto: Prto): Int =
        (if (prto.load) 1 shl 0 else 0) or (if (prto.unload) 1 shl 1 else 0) or
            (if (prto.airlift) 1 shl 2 else 0) or (if (prto.pillage) 1 shl 3 else 0) or
            (if (prto.bombard) 1 shl 4 else 0) or (if (prto.airdrop) 1 shl 5 else 0) or
            (if (prto.buildArmy) 1 shl 6 else 0) or (if (prto.finishImprovements) 1 shl 7 else 0) or
            (if (prto.upgradeUnit) 1 shl 8 else 0) or (if (prto.capture) 1 shl 9 else 0) or
            (if (prto.stealthAttack) 1 shl 16 else 0) or (if (prto.enslave) 1 shl 18 else 0) or
            (if (prto.sacrifice) 1 shl 20 else 0) or (if (prto.startsScienceAge) 1 shl 21 else 0)

    fun buildWorkerActions(prto: Prto): Int =
        (if (prto.buildColony) 1 shl 0 else 0) or (if (prto.buildCity) 1 shl 1 else 0) or
            (if (prto.buildRoad) 1 shl 2 else 0) or (if (prto.buildRailroad) 1 shl 3 else 0) or
            (if (prto.buildFort) 1 shl 4 else 0) or (if (prto.buildMine) 1 shl 5 else 0) or
            (if (prto.irrigate) 1 shl 6 else 0) or (if (prto.clearForest) 1 shl 7 else 0) or
            (if (prto.clearJungle) 1 shl 8 else 0) or (if (prto.plantForest) 1 shl 9 else 0) or
            (if (prto.clearPollution) 1 shl 10 else 0) or (if (prto.automate) 1 shl 11 else 0) or
            (if (prto.joinCity) 1 shl 12 else 0) or (if (prto.buildAirfield) 1 shl 13 else 0) or
            (if (prto.buildRadarTower) 1 shl 14 else 0) or (if (prto.buildOutpost) 1 shl 15 else 0) or
            (if (prto.buildBarricade) 1 shl 16 else 0)

    fun buildAirMissions(prto: Prto): Int =
        (if (prto.bombing) 1 shl 0 else 0) or (if (prto.recon) 1 shl 1 else 0) or
            (if (prto.interception) 1 shl 2 else 0) or (if (prto.rebase) 1 shl 3 else 0) or
            (if (prto.precisionBombing) 1 shl 4 else 0)

    fun buildVanillaFlags2(prto: Prto): ByteString {
        val low = (if (prto.skipTurn) 1 shl 0 else 0) or (if (prto.wait) 1 shl 1 else 0) or
            (if (prto.fortify) 1 shl 2 else 0) or (if (prto.disband) 1 shl 3 else 0) or
            (if (prto.goTo) 1 shl 4 else 0) or (if (prto.load) 1 shl 5 else 0) or
            (if (prto.unload) 1 shl 6 else 0) or (if (prto.airlift) 1 shl 7 else 0) or
            (if (prto.pillage) 1 shl 8 else 0) or (if (prto.bombard) 1 shl 9 else 0) or
            (if (prto.airdrop) 1 shl 10 else 0) or (if (prto.buildArmy) 1 shl 11 else 0) or
            (if (prto.finishImprovements) 1 shl 12 else 0) or (if (prto.upgradeUnit) 1 shl 13 else 0) or
            (if (prto.buildColony) 1 shl 14 else 0) or (if (prto.buildCity) 1 shl 15 else 0) or
            (if (prto.buildRoad) 1 shl 16 else 0) or (if (prto.buildRailroad) 1 shl 17 else 0) or
            (if (prto.buildFort) 1 shl 18 else 0) or (if (prto.buildMine) 1 shl 19 else 0) or
            (if (prto.irrigate) 1 shl 20 else 0) or (if (prto.clearForest) 1 shl 21 else 0) or
            (if (prto.clearJungle) 1 shl 22 else 0) or (if (prto.plantForest) 1 shl 23 else 0) or
            (if (prto.clearPollution) 1 shl 24 else 0) or (if (prto.automate) 1 shl 25 else 0) or
            (if (prto.joinCity) 1 shl 26 else 0)
        val high = (if (prto.bombing) 1 shl 0 else 0) or (if (prto.recon) 1 shl 1 else 0) or
            (if (prto.interception) 1 shl 2 else 0) or (if (prto.rebase) 1 shl 3 else 0) or
            (if (prto.precisionBombing) 1 shl 4 else 0)
        val buffer = Buffer()
        buffer.writeIntLe(low)
        buffer.writeIntLe(high)
        return buffer.readByteString()
    }

    fun buildFlags4(prto: Prto): ByteString {
        val bits = (if (prto.sentry) 1 shl 0 else 0) or (if (prto.bombard) 1 shl 1 else 0) or
            (if (prto.clearForest && prto.clearJungle) 1 shl 2 else 0) or (if (prto.buildRoad) 1 shl 3 else 0) or
            (if (prto.buildRailroad) 1 shl 4 else 0) or (if (prto.buildRoad) 1 shl 5 else 0) or
            (if (prto.irrigate) 1 shl 6 else 0) or (if (prto.clearForest) 1 shl 7 else 0) or
            (if (prto.clearJungle) 1 shl 8 else 0) or (if (prto.clearPollution) 1 shl 9 else 0) or
            (if (prto.automate) 1 shl 10 else 0) or (if (prto.automate) 1 shl 11 else 0) or
            (if (prto.bombing) 1 shl 12 else 0) or (if (prto.precisionBombing) 1 shl 13 else 0) or
            (if (prto.automate) 1 shl 14 else 0) or (if (prto.goTo || prto.rebase) 1 shl 15 else 0) or
            (1 shl 16)
        val buffer = Buffer()
        buffer.writeIntLe(bits)
        return buffer.readByteString()
    }

    return roster.flatMapIndexed { i, prto ->
        val bits = (0 until Int.SIZE_BITS).filter { (prto.aiStrategies shr it) and 1 == 1 }
        val entriesToEmit: List<Int?> = if (bits.isEmpty()) listOf(null) else bits

        val requiredIndex = resolveTech(prto.required)
        val requiredResource1 = resolveGood("requiredResources[0]", prto.requiredResources[0])
        val requiredResource2 = resolveGood("requiredResources[1]", prto.requiredResources[1])
        val requiredResource3 = resolveGood("requiredResources[2]", prto.requiredResources[2])
        val availableTo = prto.availableTo.fold(0) { acc, race -> acc or (1 shl resolveRace(race)) }
        val enslaveResultsIndex = resolvePrto("enslaveResults", prto.enslaveResults)
        val stealthTargetUnitTypesIndices = prto.stealthTargetUnitTypes.map { resolvePrto("stealthTargetUnitTypes", it) }
        val ignoreMovementCostBytes = ByteArray((terrs.size + 7) / 8)
        prto.ignoreMovementCost.forEach { terr ->
            val index = resolveTerr(terr)
            ignoreMovementCostBytes[index / 8] = (ignoreMovementCostBytes[index / 8].toInt() or (1 shl (index % 8))).toByte()
        }
        val ignoreMovementCost = ByteString.of(*ignoreMovementCostBytes)

        val standardOrders = if (era == Civ3FormatEra.VANILLA) 0 else buildStandardOrders(prto)
        val specialActions = if (era == Civ3FormatEra.VANILLA) 0 else buildSpecialActions(prto)
        val workerActions = if (era == Civ3FormatEra.VANILLA) 0 else buildWorkerActions(prto)
        val airMissions = if (era == Civ3FormatEra.VANILLA) 0 else buildAirMissions(prto)
        val flags2 = if (era == Civ3FormatEra.VANILLA) buildVanillaFlags2(prto) else ByteString.of(*ByteArray(8))
        val flags4 = if (era == Civ3FormatEra.VANILLA) ByteString.of(0, 0, 0, 0) else buildFlags4(prto)

        entriesToEmit.mapIndexed { bitPosition, bit ->
            PrtoEntry(
                unitStatistics = WirePrtoUnitStatistics(
                    zoneOfControl = prto.unitStatistics.zoneOfControl,
                    bombardStrength = prto.unitStatistics.bombardStrength,
                    bombardRange = prto.unitStatistics.bombardRange,
                    capacity = prto.unitStatistics.capacity,
                    shieldCost = prto.unitStatistics.shieldCost,
                    defense = prto.unitStatistics.defense,
                    attack = prto.unitStatistics.attack,
                    operationalRange = prto.unitStatistics.operationalRange,
                    populationCost = prto.unitStatistics.populationCost,
                    rateOfFire = prto.unitStatistics.rateOfFire,
                    movement = prto.unitStatistics.movement,
                    upgradeTo = resolvePrto("unitStatistics.upgradeTo", prto.unitStatistics.upgradeTo),
                    hpBonus = prto.unitStatistics.hpBonus,
                    bombardEffects = prto.unitStatistics.bombardEffects,
                    requireSupport = prto.unitStatistics.requireSupport,
                    createCraters = prto.unitStatistics.createCraters,
                    workerStrength = prto.unitStatistics.workerStrength,
                    airDefense = prto.unitStatistics.airDefense,
                ),
                name = prto.name,
                civilopediaEntry = prto.civilopediaEntry,
                iconIndex = prto.iconIndex,
                required = requiredIndex,
                requiredResource1 = requiredResource1,
                requiredResource2 = requiredResource2,
                requiredResource3 = requiredResource3,
                abilities = prto.abilities,
                aiStrategies = bit?.let { 1 shl it } ?: 0,
                availableTo = availableTo,
                flags2 = flags2,
                type = prto.type,
                otherStrategy = if (bitPosition == 0) -1 else canonicalWireIndex[i],
                standardOrders = standardOrders,
                specialActions = specialActions,
                workerActions = workerActions,
                airMissions = airMissions,
                flags4 = flags4,
                ignoreMovementCost = ignoreMovementCost,
                unknown = prto.unknown,
                enslaveResults = enslaveResultsIndex,
                unknown2 = prto.unknown2,
                stealthTargetUnitTypes = stealthTargetUnitTypesIndices,
                unknown3 = prto.unknown3,
                unknown4 = prto.unknown4,
            )
        }
    }
}
