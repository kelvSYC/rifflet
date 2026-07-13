package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.GovtRelationship
import okio.Buffer

/**
 * Parses one government-relationship entry (per the Apolyton BIX/BIQ format documentation),
 * 12 bytes: `canBribe`, `briberyModifier`, `resistanceModifier`, each a little-endian `Int`.
 * Reads directly off [item], continuing whatever cursor position the caller (`GovtEntryParser`)
 * has already reached on the shared `GOVT` record `Buffer`.
 */
internal object GovtRelationshipParser {
    fun parse(item: Buffer): GovtRelationship {
        val canBribe = item.readIntLe()
        val briberyModifier = item.readIntLe()
        val resistanceModifier = item.readIntLe()
        return GovtRelationship(canBribe, briberyModifier, resistanceModifier)
    }
}
