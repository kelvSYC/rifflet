package com.kelvsyc.rifflet.internal.core

import okio.ByteString

/**
 * Converts this [ByteArray] to a [ByteString].
 *
 * Allocates two byte arrays (the minimum with okio's public API; [ByteString] is immutable).
 */
fun ByteArray.toByteString(): ByteString = ByteString.of(*this)
