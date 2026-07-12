package com.kelvsyc.rifflet.internal.pkware

import okio.BufferedSource

/**
 * Reads bits least-significant-bit first from a byte stream, matching the PKWare DCL "Implode"
 * bitstream convention. Multi-bit fields are read directly from the low end of each byte; the
 * canonical Huffman decoding in [decode] has its own bit-reversal handling.
 *
 * Ported from the reference decompressor `blast.c`'s `bits()`/`decode()` functions (Mark Adler,
 * zlib license, https://github.com/madler/zlib/blob/master/contrib/blast/blast.c). Truncated
 * input surfaces as an `okio.EOFException` from the underlying [source] reads, not a Rifflet
 * exception — callers wrap I/O errors at the root parser, matching this codebase's existing
 * convention (see `T3RootParserImpl`).
 */
internal class PkwareBitReader(private val source: BufferedSource) {
    private var bitBuf: Int = 0
    private var bitCount: Int = 0

    fun bits(need: Int): Int {
        var value = bitBuf
        while (bitCount < need) {
            value = value or ((source.readByte().toInt() and 0xFF) shl bitCount)
            bitCount += 8
        }
        bitBuf = value shr need
        bitCount -= need
        return value and ((1 shl need) - 1)
    }
}
