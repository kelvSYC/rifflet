package com.kelvsyc.rifflet.t3

import okio.ByteString

/**
 * The T3 image file's fixed 69-byte preamble.
 *
 * @param buildHash The 4 tool-reserved bytes from the reserved block, preserved verbatim but not
 *   interpreted by this parser.
 */
data class T3Header(val version: Int, val buildHash: ByteString, val timestamp: String)
