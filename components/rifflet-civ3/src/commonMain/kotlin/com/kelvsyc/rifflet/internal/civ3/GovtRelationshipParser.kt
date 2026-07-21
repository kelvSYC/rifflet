package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.GovtRelationship
import okio.Buffer

/**
 * Parses one government-relationship entry (byte layout per existing reverse-engineering
 * documentation of the BIX/BIQ format, second field's name corrected — see
 * [GovtRelationship]'s own KDoc), 12 bytes: `canBribe`, `propagandaModifier`,
 * `resistanceModifier`, each a little-endian `Int`. Reads directly off [item], continuing
 * whatever cursor position the caller (`GovtEntryParser`) has already reached on the shared
 * `GOVT` record `Buffer`.
 */
internal object GovtRelationshipParser {
    fun parse(item: Buffer): GovtRelationship {
        val canBribe = item.readIntLe()
        val propagandaModifier = item.readIntLe()
        val resistanceModifier = item.readIntLe()
        return GovtRelationship(canBribe, propagandaModifier, resistanceModifier)
    }
}
