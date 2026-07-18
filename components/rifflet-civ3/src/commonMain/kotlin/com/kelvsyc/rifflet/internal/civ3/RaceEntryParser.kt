package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.RaceEntry
import okio.Buffer

/**
 * Parses one `RACE` item, per the Apolyton BIX/BIQ format documentation. Reads directly off
 * [item], a zero-copy-transferred [Buffer] already stripped of its own length prefix by the
 * generic section loop. [erasCount] comes from the already-parsed `ERAS` section (see
 * `Civ3RootParserImpl`'s cross-section threading) and sizes the embedded `eras` array; none of
 * the four dynamic-array counts (`numberOfCities`, `numberOfGreatLeaders`, `erasCount`,
 * `numberOfScientificLeaders`) are stored on [RaceEntry] — each list's own `.size` is already
 * that count. `scientificLeaderNames` is the record's last field; nothing follows it.
 *
 * All four counts are validated via [requireSaneCount] before sizing their respective lists —
 * see that function's KDoc for why. `520L` is [RaceEraFilenames]' fixed width (two 260-byte
 * fields).
 */
internal object RaceEntryParser {
    fun parse(item: Buffer, erasCount: Int): RaceEntry {
        val numberOfCities = item.requireSaneCount(item.readIntLe(), 24L, "RaceEntry.cityNames")
        val cityNames = List(numberOfCities) { item.readByteString(24L).truncateAtFirstNull() }
        val numberOfGreatLeaders = item.requireSaneCount(item.readIntLe(), 32L, "RaceEntry.greatLeaderNames")
        val greatLeaderNames = List(numberOfGreatLeaders) { item.readByteString(32L).truncateAtFirstNull() }
        val leaderName = item.readByteString(32L).truncateAtFirstNull()
        val leaderTitle = item.readByteString(24L).truncateAtFirstNull()
        val civilopediaEntry = item.readByteString(32L).truncateAtFirstNull()
        val adjective = item.readByteString(40L).truncateAtFirstNull()
        val name = item.readByteString(40L).truncateAtFirstNull()
        val noun = item.readByteString(40L).truncateAtFirstNull()
        val validatedErasCount = item.requireSaneCount(erasCount, 520L, "RaceEntry.eras")
        val eras = List(validatedErasCount) { RaceEraFilenamesParser.parse(item) }
        val cultureGroup = item.readIntLe()
        val leaderGender = item.readIntLe()
        val civilizationGender = item.readIntLe()
        val aggressionLevel = item.readIntLe()
        val uniqueCivilizationCounter = item.readIntLe()
        val shunnedGovernment = item.readIntLe()
        val favoriteGovernment = item.readIntLe()
        val defaultColor = item.readIntLe()
        val uniqueColor = item.readIntLe()
        val freeTech1 = item.readIntLe()
        val freeTech2 = item.readIntLe()
        val freeTech3 = item.readIntLe()
        val freeTech4 = item.readIntLe()
        val bonuses = item.readIntLe()
        val governorSettings = item.readIntLe()
        val buildNever = item.readIntLe()
        val buildOften = item.readIntLe()
        val plurality = item.readIntLe()
        val unitTypeForKing = item.readIntLe()
        val flavors = item.readIntLe()
        val unknown = item.readByteString(4L)
        val diplomacyTextIndex = item.readIntLe()
        val numberOfScientificLeaders = item.requireSaneCount(
            item.readIntLe(),
            32L,
            "RaceEntry.scientificLeaderNames",
        )
        val scientificLeaderNames = List(numberOfScientificLeaders) { item.readByteString(32L).truncateAtFirstNull() }
        return RaceEntry(
            cityNames,
            greatLeaderNames,
            leaderName,
            leaderTitle,
            civilopediaEntry,
            adjective,
            name,
            noun,
            eras,
            cultureGroup,
            leaderGender,
            civilizationGender,
            aggressionLevel,
            uniqueCivilizationCounter,
            shunnedGovernment,
            favoriteGovernment,
            defaultColor,
            uniqueColor,
            freeTech1,
            freeTech2,
            freeTech3,
            freeTech4,
            bonuses,
            governorSettings,
            buildNever,
            buildOften,
            plurality,
            unitTypeForKing,
            flavors,
            unknown,
            diplomacyTextIndex,
            scientificLeaderNames,
        )
    }
}
