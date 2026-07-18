package com.kelvsyc.rifflet.internal.civ3

import com.kelvsyc.rifflet.core.RiffletParseException
import okio.BufferedSource

/**
 * Validates that [count] — a dynamic-array size read directly from an untrusted file — is
 * plausible given the bytes actually available in [this], before [count] is used to size any
 * collection. Throws [RiffletParseException] if not: either [count] is negative, or fewer than
 * `count * minBytesPerElement` bytes remain.
 *
 * This exists because Kotlin's `List(n) { init }` builder eagerly allocates an `n`-sized backing
 * array before invoking `init` even once — a corrupt or malicious file declaring a count near
 * `Int.MAX_VALUE` would otherwise trigger a multi-gigabyte allocation attempt
 * (`OutOfMemoryError`) before a single element is ever read, regardless of whether the
 * subsequent read would itself eventually fail. Every dynamic-array count in this codebase must
 * be passed through this function immediately after being read, before it is used to size
 * anything.
 *
 * [minBytesPerElement] should be the smallest number of bytes a single element could possibly
 * consume — for fixed-width elements, their exact width; for nested variable-width element
 * parsers, the smallest total of their own always-present fixed-size sub-fields.
 *
 * [BufferedSource.request] is safe to call with an arbitrarily large requested size regardless
 * of [this]'s true remaining length: it reads incrementally from the real underlying data and
 * returns `false` the moment that data is exhausted, without buffering anywhere near the
 * requested amount if the source doesn't actually have it.
 */
internal fun BufferedSource.requireSaneCount(count: Int, minBytesPerElement: Long, fieldName: String): Int {
    if (count < 0 || !request(minBytesPerElement * count)) {
        throw RiffletParseException(
            "$fieldName declares $count elements, requiring at least ${minBytesPerElement * count} bytes, " +
                "but insufficient data remains",
        )
    }
    return count
}
