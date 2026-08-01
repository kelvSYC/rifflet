package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.civ3.Civ3EnumDecodeException
import kotlin.enums.EnumEntries

/**
 * Decodes [rawValue] — a field read directly from an untrusted file — against [entries], throwing
 * [Civ3EnumDecodeException] if it's outside the enum's range. [entries] must always be an enum's
 * own full [EnumEntries] (e.g. `SomeEnum.entries`), never a caller-narrowed subset — restricting
 * which values are valid in a given context (such as an era-gated option) is a
 * [com.kelvsyc.rifflet.civ3.validation.ValidationRule]'s job, not this function's.
 *
 * [indexOf] maps [rawValue] to the actual index to look up in [entries], for the rare field whose
 * raw value is offset from the enum's ordinals (e.g. `RaceEntry.cultureGroup`'s `-1`-based range).
 * The exception always reports the original [rawValue], never the offset-adjusted index.
 */
internal inline fun <reified E : Enum<E>> decodeEnum(
    fieldName: String,
    rawValue: Int,
    entries: EnumEntries<E>,
    indexOf: (Int) -> Int = { it },
): E = entries.getOrNull(indexOf(rawValue))
    ?: throw Civ3EnumDecodeException(fieldName, rawValue, E::class.simpleName ?: "enum", entries.size)
