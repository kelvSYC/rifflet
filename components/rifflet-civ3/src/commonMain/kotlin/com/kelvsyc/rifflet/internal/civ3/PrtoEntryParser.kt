package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3FormatEra
import com.kelvsyc.rifflet.civ3.PrtoEntry
import com.kelvsyc.rifflet.civ3.PrtoUnitStatistics
import okio.Buffer
import okio.ByteString

/**
 * Parses one `PRTO` item. Reads directly off [item], a zero-copy-transferred [Buffer] already
 * stripped of its own length prefix by the generic section loop.
 *
 * [terrCount] comes from the already-parsed `TERR` section (see `Civ3RootParserImpl`'s
 * cross-section threading, the same pattern `RaceEntryParser` uses for `erasCount`) and sizes
 * [PrtoEntry.ignoreMovementCost]: real [Civ3FormatEra.VANILLA] and [Civ3FormatEra.PTW] files
 * always have 12 `TERR` entries, real [Civ3FormatEra.CONQUESTS] files always have 14 (Conquests
 * added 2 new terrain types, marshes and volcanoes). [terrCount] is validated via
 * [requireSaneCount] immediately upon entry, before it sizes [PrtoEntry.ignoreMovementCost] — see
 * that function's KDoc for why.
 *
 * [PrtoEntry.numberOfStealthTargets] (not stored on [PrtoEntry] directly —
 * `stealthTargetUnitTypes.size` is already that count) is likewise validated via
 * [requireSaneCount] immediately after being read, before it sizes
 * [PrtoEntry.stealthTargetUnitTypes] in either of its two branches.
 *
 * Every field from [PrtoEntry.standardOrders] onward is read defensively: real
 * [Civ3FormatEra.VANILLA] files end immediately after [PrtoUnitStatistics.hpBonus]; real
 * [Civ3FormatEra.PTW] files (only confirmed for `VER#` header `minor=18`) include everything
 * through [PrtoUnitStatistics.requireSupport] but omit everything from [PrtoEntry.unknown]
 * onward — the entire unit-behavior tail ([PrtoEntry.unknown] through
 * [PrtoUnitStatistics.airDefense]) was introduced together as a [Civ3FormatEra.CONQUESTS]-era
 * `PRTO` expansion, mirroring `GAME`'s own Conquests-era expansion. [PrtoEntry.unitStatistics]'
 * 5 defensively-read members ([PrtoUnitStatistics.bombardEffects] through
 * [PrtoUnitStatistics.airDefense]) are read into nullable locals at their original positions and
 * assembled into the always-constructed group alongside its 13 unconditional members — the same
 * nullable-members-within-a-required-group pattern used for `RuleDefaultUnits.flagUnitType` and
 * `TerrAllowances`.
 */
internal object PrtoEntryParser {
    fun parse(item: Buffer, terrCount: Int): PrtoEntry {
        val terrCount = item.requireSaneCount(terrCount, 1L, "PrtoEntry.ignoreMovementCost")
        val zoneOfControl = item.readIntLe()
        val name = item.readByteString(32L).truncateAtFirstNull()
        val civilopediaEntry = item.readByteString(32L).truncateAtFirstNull()
        val bombardStrength = item.readIntLe()
        val bombardRange = item.readIntLe()
        val capacity = item.readIntLe()
        val shieldCost = item.readIntLe()
        val defense = item.readIntLe()
        val iconIndex = item.readIntLe()
        val attack = item.readIntLe()
        val operationalRange = item.readIntLe()
        val populationCost = item.readIntLe()
        val rateOfFire = item.readIntLe()
        val movement = item.readIntLe()
        val required = item.readIntLe()
        val upgradeTo = item.readIntLe()
        val requiredResource1 = item.readIntLe()
        val requiredResource2 = item.readIntLe()
        val requiredResource3 = item.readIntLe()
        val abilities = item.readIntLe()
        val aiStrategies = item.readIntLe()
        val availableTo = item.readIntLe()
        val flags2 = item.readByteString(8L)
        val type = item.readIntLe()
        val otherStrategy = item.readIntLe()
        val hpBonus = item.readIntLe()
        val hasFlags3 = item.size >= 20L
        val standardOrders = if (hasFlags3) item.readIntLe() else 0
        val specialActions = if (hasFlags3) item.readIntLe() else 0
        val workerActions = if (hasFlags3) item.readIntLe() else 0
        val airMissions = if (hasFlags3) item.readIntLe() else 0
        val flags4 = if (hasFlags3) item.readByteString(4L) else ByteString.of(*ByteArray(4))
        val bombardEffects = if (item.size >= 4L) item.readIntLe() else null
        val ignoreMovementCost = if (item.size >= terrCount.toLong()) {
            item.readByteString(terrCount.toLong())
        } else {
            ByteString.of(*ByteArray(terrCount))
        }
        val requireSupport = if (item.size >= 4L) item.readIntLe() else null
        val unknown = if (item.size >= 16L) item.readByteString(16L) else ByteString.of(*ByteArray(16))
        val enslaveResults = if (item.size >= 4L) item.readIntLe() else 0
        val unknown2 = if (item.size >= 4L) item.readByteString(4L) else ByteString.of(0, 0, 0, 0)
        val numberOfStealthTargets = item.requireSaneCount(
            if (item.size >= 4L) item.readIntLe() else 0,
            4L,
            "PrtoEntry.stealthTargetUnitTypes",
        )
        val stealthTargetUnitTypes = if (item.size >= 4L * numberOfStealthTargets) {
            List(numberOfStealthTargets) { item.readIntLe() }
        } else {
            List(numberOfStealthTargets) { 0 }
        }
        val unknown3 = if (item.size >= 8L) item.readByteString(8L) else ByteString.of(*ByteArray(8))
        val createCraters = if (item.size >= 1L) item.readByte() else null
        val workerStrength = if (item.size >= 4L) Float.fromBits(item.readIntLe()) else null
        val unknown4 = if (item.size >= 4L) item.readByteString(4L) else ByteString.of(0, 0, 0, 0)
        val airDefense = if (item.size >= 4L) item.readIntLe() else null
        val unitStatistics = PrtoUnitStatistics(
            zoneOfControl,
            bombardStrength,
            bombardRange,
            capacity,
            shieldCost,
            defense,
            attack,
            operationalRange,
            populationCost,
            rateOfFire,
            movement,
            upgradeTo,
            hpBonus,
            bombardEffects,
            requireSupport,
            createCraters,
            workerStrength,
            airDefense,
        )
        return PrtoEntry(
            unitStatistics,
            name,
            civilopediaEntry,
            iconIndex,
            required,
            requiredResource1,
            requiredResource2,
            requiredResource3,
            abilities,
            aiStrategies,
            availableTo,
            flags2,
            type,
            otherStrategy,
            standardOrders,
            specialActions,
            workerActions,
            airMissions,
            flags4,
            ignoreMovementCost,
            unknown,
            enslaveResults,
            unknown2,
            stealthTargetUnitTypes,
            unknown3,
            unknown4,
        )
    }
}
