package com.kelvsyc.rifflet.internal.civ3

import okio.ByteString

/**
 * Truncates this [ByteString] at its first null byte, decoding the remainder as UTF-8. Civ3's
 * fixed-width string fields terminate at the first null byte; the rest of the field is
 * unspecified padding that may contain non-zero garbage.
 */
internal fun ByteString.truncateAtFirstNull(): String {
    val end = indexOf(byteArrayOf(0))
    return (if (end == -1) this else substring(0, end)).utf8()
}
