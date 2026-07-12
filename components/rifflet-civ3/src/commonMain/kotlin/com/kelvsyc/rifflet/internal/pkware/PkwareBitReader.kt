package com.kelvsyc.rifflet.internal.pkware

import com.kelvsyc.rifflet.core.RiffletParseException
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

    /**
     * Decodes one symbol using canonical Huffman [table]. Ported from `decode()` in `blast.c`:
     * codes as stored are bit-reversed relative to a simple integer ordering of codes of the same
     * length, so bits are pulled one at a time and the running `code` value is built up inverted,
     * to permit direct integer comparison against the running `first`/`count` bookkeeping per
     * length.
     */
    fun decode(table: PkwareHuffmanTable): Int {
        var localBitBuf = bitBuf
        var left = bitCount
        var code = 0
        var first = 0
        var index = 0
        var len = 1
        var next = 1
        while (true) {
            while (left > 0) {
                left--
                code = code or ((localBitBuf and 1) xor 1)
                localBitBuf = localBitBuf shr 1
                val count = table.counts[next]
                next++
                if (code < first + count) {
                    bitBuf = localBitBuf
                    bitCount = (bitCount - len) and 7
                    return table.symbols[index + (code - first)]
                }
                index += count
                first += count
                first = first shl 1
                code = code shl 1
                len++
            }
            left = (MAX_BITS + 1) - len
            if (left == 0) {
                throw RiffletParseException("Invalid PKWare Implode Huffman code")
            }
            localBitBuf = source.readByte().toInt() and 0xFF
            if (left > 8) left = 8
        }
    }
}
