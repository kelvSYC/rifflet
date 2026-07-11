package com.kelvsyc.rifflet.internal.t3

import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.t3.T3Header
import okio.BufferedSource
import okio.ByteString
import okio.ByteString.Companion.decodeHex

/**
 * Parses the T3 image file's fixed 69-byte preamble: an 11-byte magic signature, a 2-byte
 * little-endian version, a 32-byte reserved block (28 bytes that must be zero, followed by a
 * 4-byte tool-reserved build hash), and a 24-byte ASCII timestamp.
 */
internal object T3HeaderParser {
    private val MAGIC = "54332d696d6167650d0a1a".decodeHex()
    private const val RESERVED_ZERO_SIZE = 28L
    private const val BUILD_HASH_SIZE = 4L
    private const val TIMESTAMP_SIZE = 24L
    private val RESERVED_ZERO_BLOCK = ByteString.of(*ByteArray(RESERVED_ZERO_SIZE.toInt()))

    fun parse(source: BufferedSource): T3Header {
        val magic = source.readByteString(MAGIC.size.toLong())
        if (magic != MAGIC) throw RiffletParseException("Not a T3 image file: bad magic signature")
        val version = source.readShortLe().toInt() and 0xFFFF
        val reservedZero = source.readByteString(RESERVED_ZERO_SIZE)
        if (reservedZero != RESERVED_ZERO_BLOCK)
            throw RiffletParseException("T3 header reserved bytes must be zero")
        val buildHash = source.readByteString(BUILD_HASH_SIZE)
        val timestamp = source.readUtf8(TIMESTAMP_SIZE)
        return T3Header(version, buildHash, timestamp)
    }
}
