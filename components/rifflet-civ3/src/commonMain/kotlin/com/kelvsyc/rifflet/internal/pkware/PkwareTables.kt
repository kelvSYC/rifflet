package com.kelvsyc.rifflet.internal.pkware

/**
 * Fixed Huffman code-length and extra-bits tables for PKWare DCL "Implode", transcribed
 * verbatim from the reference decompressor `blast.c` (Mark Adler, zlib license,
 * https://github.com/madler/zlib/blob/master/contrib/blast/blast.c). Each entry in a
 * `*_CODE_LENGTHS` table packs a repeat count (high nibble, plus one) and a code length (low
 * nibble); see [constructHuffmanTable].
 */
internal val LITERAL_CODE_LENGTHS = intArrayOf(
    11, 124, 8, 7, 28, 7, 188, 13, 76, 4, 10, 8, 12, 10, 12, 10, 8, 23, 8,
    9, 7, 6, 7, 8, 7, 6, 55, 8, 23, 24, 12, 11, 7, 9, 11, 12, 6, 7, 22, 5,
    7, 24, 6, 11, 9, 6, 7, 22, 7, 11, 38, 7, 9, 8, 25, 11, 8, 11, 9, 12,
    8, 12, 5, 38, 5, 38, 5, 11, 7, 5, 6, 21, 6, 10, 53, 8, 7, 24, 10, 27,
    44, 253, 253, 253, 252, 252, 252, 13, 12, 45, 12, 45, 12, 61, 12, 45,
    44, 173,
)

internal val LENGTH_CODE_LENGTHS = intArrayOf(2, 35, 36, 53, 38, 23)

internal val DISTANCE_CODE_LENGTHS = intArrayOf(2, 20, 53, 230, 247, 151, 248)

/** Base value added to the extra bits read for each length code (index = decoded length symbol). */
internal val LENGTH_BASE = intArrayOf(3, 2, 4, 5, 6, 7, 8, 9, 10, 12, 16, 24, 40, 72, 136, 264)

/** Number of extra bits to read following each length code (index = decoded length symbol). */
internal val LENGTH_EXTRA_BITS = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8)
