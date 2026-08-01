package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletParseException

/**
 * Thrown when a Civ3 field backed by a small, closed-set enum (per a single Conquests Rules
 * Editor dropdown) holds a raw value outside that enum's documented range.
 *
 * [section]/[index] are `null` when first thrown from deep inside an `*EntryParser`, which
 * doesn't know its own position within its section's entry list — [Civ3RootParserImpl] enriches
 * the exception with both once it's caught at the section-parsing loop that does know them.
 */
class Civ3EnumDecodeException private constructor(
    val section: ChunkId?,
    val index: Int?,
    val field: String,
    val rawValue: Int,
    message: String,
    cause: Throwable?,
) : RiffletParseException(message, cause) {

    internal constructor(field: String, rawValue: Int, enumName: String, validCount: Int) : this(
        section = null,
        index = null,
        field = field,
        rawValue = rawValue,
        message = "$field=$rawValue does not decode to a known $enumName ($validCount possible values)",
        cause = null,
    )

    internal constructor(section: ChunkId, index: Int, cause: Civ3EnumDecodeException) : this(
        section = section,
        index = index,
        field = cause.field,
        rawValue = cause.rawValue,
        message = "${section.name}[$index]: ${cause.message}",
        cause = cause,
    )
}
