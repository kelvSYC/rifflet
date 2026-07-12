package com.kelvsyc.rifflet.internal.pkware

internal const val MAX_BITS = 13

/**
 * Canonical Huffman decoding table for PKWare DCL "Implode": [counts] holds the number of codes
 * of each bit length (index 1..[MAX_BITS]; index 0 is unused), and [symbols] holds the symbol
 * values sorted by code length, retaining original symbol order within each length.
 */
internal class PkwareHuffmanTable(val counts: IntArray, val symbols: IntArray)

/**
 * Expands a PKWare-style compact repeat-encoded code length list — each entry packs a repeat
 * count (high nibble, plus one) and a code length (low nibble) — into a canonical Huffman
 * decoding table for [symbolCount] symbols.
 *
 * Ported from `construct()` in the reference decompressor `blast.c` (Mark Adler, zlib license,
 * https://github.com/madler/zlib/blob/master/contrib/blast/blast.c). [repeats] is always one of
 * this codebase's own fixed tables ([LITERAL_CODE_LENGTHS], [LENGTH_CODE_LENGTHS], or
 * [DISTANCE_CODE_LENGTHS]), never file-supplied data, so a malformed result here indicates a bug
 * in those tables rather than malformed input — hence the [check] assertions rather than
 * `RiffletParseException`.
 */
internal fun constructHuffmanTable(repeats: IntArray, symbolCount: Int): PkwareHuffmanTable {
    val lengths = IntArray(symbolCount)
    var symbol = 0
    for (rep in repeats) {
        val length = rep and 0xF
        val repeatCount = (rep shr 4) + 1
        repeat(repeatCount) {
            lengths[symbol] = length
            symbol++
        }
    }
    check(symbol == symbolCount) { "Expanded PKWare Huffman table length mismatch: $symbol != $symbolCount" }

    val counts = IntArray(MAX_BITS + 1)
    for (length in lengths) counts[length]++
    if (counts[0] == symbolCount) {
        return PkwareHuffmanTable(counts, IntArray(0))
    }

    var left = 1
    for (length in 1..MAX_BITS) {
        left = left shl 1
        left -= counts[length]
        check(left >= 0) { "Over-subscribed PKWare Huffman table" }
    }

    val offsets = IntArray(MAX_BITS + 1)
    for (length in 1 until MAX_BITS) {
        offsets[length + 1] = offsets[length] + counts[length]
    }

    val symbols = IntArray(symbolCount)
    for (sym in 0 until symbolCount) {
        val length = lengths[sym]
        if (length != 0) {
            symbols[offsets[length]] = sym
            offsets[length]++
        }
    }

    return PkwareHuffmanTable(counts, symbols)
}
