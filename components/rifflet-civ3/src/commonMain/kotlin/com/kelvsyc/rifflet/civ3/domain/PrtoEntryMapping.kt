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
 * exactly like `BldgEntry.toDomain()`'s cycle guard. `enslaveResults` gets no such guard: unlike
 * `upgradeTo`, self-reference there is common and meaningful (see [Prto.enslaveResults]'s KDoc).
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
