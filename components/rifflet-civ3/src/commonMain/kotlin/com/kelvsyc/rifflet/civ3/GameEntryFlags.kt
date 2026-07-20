package com.kelvsyc.rifflet.civ3

import okio.ByteString
import com.kelvsyc.kotlin.core.traits.integral.BitCollection
import com.kelvsyc.kotlin.core.traits.integral.extensionBitFlag
import com.kelvsyc.kotlin.core.traits.integral.int

private fun ByteString.toIntLe(): Int =
    (this[0].toInt() and 0xFF) or
        ((this[1].toInt() and 0xFF) shl 8) or
        ((this[2].toInt() and 0xFF) shl 16) or
        ((this[3].toInt() and 0xFF) shl 24)

/**
 * Named accessors for [GameEntry.flags]'s 16 documented bits, per Apolyton's "Civilization III
 * BIX/BIQ file format" thread.
 */
val GameEntry.dominationVictoryEnabled: Boolean by BitCollection.int.extensionBitFlag({ flags.toIntLe() }, 0)
val GameEntry.spaceRaceVictoryEnabled: Boolean by BitCollection.int.extensionBitFlag({ flags.toIntLe() }, 1)
val GameEntry.diplomaticVictoryEnabled: Boolean by BitCollection.int.extensionBitFlag({ flags.toIntLe() }, 2)
val GameEntry.victoryByConquestEnabled: Boolean by BitCollection.int.extensionBitFlag({ flags.toIntLe() }, 3)
val GameEntry.culturalVictoryEnabled: Boolean by BitCollection.int.extensionBitFlag({ flags.toIntLe() }, 4)
val GameEntry.civSpecificAbilitiesEnabled: Boolean by BitCollection.int.extensionBitFlag({ flags.toIntLe() }, 5)
val GameEntry.culturallyLinkedStart: Boolean by BitCollection.int.extensionBitFlag({ flags.toIntLe() }, 6)
val GameEntry.restartPlayers: Boolean by BitCollection.int.extensionBitFlag({ flags.toIntLe() }, 7)
val GameEntry.preserveRandomSeed: Boolean by BitCollection.int.extensionBitFlag({ flags.toIntLe() }, 8)
val GameEntry.acceleratedProduction: Boolean by BitCollection.int.extensionBitFlag({ flags.toIntLe() }, 9)
val GameEntry.eliminationEnabled: Boolean by BitCollection.int.extensionBitFlag({ flags.toIntLe() }, 10)
val GameEntry.regicideEnabled: Boolean by BitCollection.int.extensionBitFlag({ flags.toIntLe() }, 11)
val GameEntry.massRegicideEnabled: Boolean by BitCollection.int.extensionBitFlag({ flags.toIntLe() }, 12)
val GameEntry.victoryLocationsEnabled: Boolean by BitCollection.int.extensionBitFlag({ flags.toIntLe() }, 13)
val GameEntry.captureTheFlagEnabled: Boolean by BitCollection.int.extensionBitFlag({ flags.toIntLe() }, 14)
val GameEntry.allowCulturalConversions: Boolean by BitCollection.int.extensionBitFlag({ flags.toIntLe() }, 15)

/**
 * [GameEntry.allianceWars] restructured from its flat, row-major 5x5 storage into a genuine
 * `List<List<Int>>`; `result[allianceA][allianceB]` is the war status between the two alliances,
 * per Apolyton's nested "for each alliance: war with alliance #0..#4" documentation. Indexing
 * outside `0..4` throws [IndexOutOfBoundsException] like any `List` access — an out-of-range
 * alliance number is a caller error, not a data-quality concern, since [GameEntry.allianceWars]'s
 * size is already a structural invariant enforced by [GameEntry]'s own `init` block.
 */
fun GameEntry.allianceWarMatrix(): List<List<Int>> = allianceWars.chunked(5)
