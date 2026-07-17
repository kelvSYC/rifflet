package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.PrtoEntry
import okio.Buffer

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
 */
internal object PrtoEntryParser {
    fun parse(item: Buffer): PrtoEntry {
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
        val flags3 = item.readByteString(20L)
        val bombardEffects = item.readIntLe()
        val ignoreMovementCost = item.readByteString(14L)
        val requireSupport = item.readIntLe()
        val unknown = item.readByteString(16L)
        val enslaveResults = item.readIntLe()
        val unknown2 = item.readByteString(4L)
        val numberOfStealthTargets = item.readIntLe()
        val stealthTargetUnitTypes = List(numberOfStealthTargets) { item.readIntLe() }
        val unknown3 = item.readByteString(8L)
        val createCraters = item.readByte()
        val workerStrength = Float.fromBits(item.readIntLe())
        val unknown4 = item.readByteString(4L)
        val airDefense = item.readIntLe()
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
