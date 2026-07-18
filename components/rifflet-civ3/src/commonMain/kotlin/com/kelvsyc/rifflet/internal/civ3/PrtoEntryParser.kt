package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.PrtoEntry
import okio.Buffer
import okio.ByteString

/**
 * Parses one `PRTO` item, per the Apolyton BIX/BIQ format documentation cross-validated against
 * `QueryCiv3`'s grouped flags regions (the two sources' byte counts for the flags/orders region
 * between `requiredResource3` and `bombardEffects` reconcile exactly at 52 bytes, confirming
 * `QueryCiv3`'s consolidated `Flags1`/`AvailableTo`/`Flags2`/`Type`/`OtherStrategy`/`HPBonus`/
 * `Flags3` grouping matches Apolyton's more granular older field list for the same region).
 * Reads directly off [item], a zero-copy-transferred [Buffer] already stripped of its own length
 * prefix by the generic section loop.
 *
 * [PrtoEntry.workerStrength] is the first `Float` field in this codebase: read via
 * `Float.fromBits` bit-reinterpretation of an ordinary little-endian `Int` read, since neither
 * okio nor this codebase has a dedicated little-endian float reader.
 *
 * [terrCount] comes from the already-parsed `TERR` section (see `Civ3RootParserImpl`'s
 * cross-section threading, the same pattern `RaceEntryParser` uses for `erasCount`) and sizes
 * [PrtoEntry.ignoreMovementCost] — confirmed real-data-dependent, not a fixed constant: real
 * vanilla and PTW files always have 12 `TERR` entries, real Conquests files always have 14
 * (Conquests added 2 new terrain types, marshes and volcanoes). This codebase originally
 * modeled `ignoreMovementCost` as a hardcoded 14 bytes, which happened to be correct only for
 * Conquests; cross-checking [PrtoEntry.requireSupport]'s value at the terrCount-corrected offset
 * against a real Conquests file's value for the same-named unit matched on 100 of 101 real
 * units, confirming the fix. [terrCount] is validated via [requireSaneCount] immediately upon
 * entry, before it sizes [PrtoEntry.ignoreMovementCost] — see that function's KDoc for why.
 *
 * [PrtoEntry.numberOfStealthTargets] (not stored on [PrtoEntry] directly —
 * `stealthTargetUnitTypes.size` is already that count) is likewise validated via
 * [requireSaneCount] immediately after being read, before it sizes
 * [PrtoEntry.stealthTargetUnitTypes] in either of its two branches.
 *
 * Every field from [PrtoEntry.flags3] onward is read defensively: real vanilla files end
 * immediately after [PrtoEntry.hpBonus]; real PTW files (confirmed only at `VER#` header
 * `minor=18`, the only PTW sub-tier with a real `PRTO` sample) include everything through
 * [PrtoEntry.requireSupport] but omit everything from [PrtoEntry.unknown] onward — evidently the
 * entire unit-behavior tail ([PrtoEntry.unknown] through [PrtoEntry.airDefense]) was introduced
 * together as a Conquests-era `PRTO` expansion, mirroring `GAME`'s own Conquests-era expansion.
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
        val flags1 = item.readByteString(8L)
        val availableTo = item.readIntLe()
        val flags2 = item.readByteString(8L)
        val type = item.readIntLe()
        val otherStrategy = item.readIntLe()
        val hpBonus = item.readIntLe()
        val flags3 = if (item.size >= 20L) item.readByteString(20L) else ByteString.of(*ByteArray(20))
        val bombardEffects = if (item.size >= 4L) item.readIntLe() else 0
        val ignoreMovementCost = if (item.size >= terrCount.toLong()) {
            item.readByteString(terrCount.toLong())
        } else {
            ByteString.of(*ByteArray(terrCount))
        }
        val requireSupport = if (item.size >= 4L) item.readIntLe() else 0
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
        val createCraters = if (item.size >= 1L) item.readByte() else 0.toByte()
        val workerStrength = if (item.size >= 4L) Float.fromBits(item.readIntLe()) else 0f
        val unknown4 = if (item.size >= 4L) item.readByteString(4L) else ByteString.of(0, 0, 0, 0)
        val airDefense = if (item.size >= 4L) item.readIntLe() else 0
        return PrtoEntry(
            zoneOfControl,
            name,
            civilopediaEntry,
            bombardStrength,
            bombardRange,
            capacity,
            shieldCost,
            defense,
            iconIndex,
            attack,
            operationalRange,
            populationCost,
            rateOfFire,
            movement,
            required,
            upgradeTo,
            requiredResource1,
            requiredResource2,
            requiredResource3,
            flags1,
            availableTo,
            flags2,
            type,
            otherStrategy,
            hpBonus,
            flags3,
            bombardEffects,
            ignoreMovementCost,
            requireSupport,
            unknown,
            enslaveResults,
            unknown2,
            stealthTargetUnitTypes,
            unknown3,
            createCraters,
            workerStrength,
            unknown4,
            airDefense,
        )
    }
}
