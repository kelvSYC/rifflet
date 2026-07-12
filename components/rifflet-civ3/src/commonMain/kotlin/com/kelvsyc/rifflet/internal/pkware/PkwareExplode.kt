package com.kelvsyc.rifflet.internal.pkware

import com.kelvsyc.rifflet.core.RiffletParseException
import okio.Buffer
import okio.BufferedSource
import okio.ByteString

private const val END_OF_STREAM_LENGTH = 519

private val literalTable by lazy { constructHuffmanTable(LITERAL_CODE_LENGTHS, 256) }
private val lengthTable by lazy { constructHuffmanTable(LENGTH_CODE_LENGTHS, 16) }
private val distanceTable by lazy { constructHuffmanTable(DISTANCE_CODE_LENGTHS, 64) }

/**
 * Decompresses a PKWare Data Compression Library ("Implode"/"Explode") stream, as used by
 * Civilization III's compressed BIC/BIX/BIQ files. Ported from the public-domain reference
 * decompressor `blast.c` (Mark Adler, zlib license,
 * https://github.com/madler/zlib/blob/master/contrib/blast/blast.c). Unlike the reference, which
 * streams through a bounded 4096-byte circular window with an output callback, this
 * implementation materializes the entire decompressed output in memory, which simplifies the
 * "distance too far back" check to a single bounds check against the output produced so far.
 *
 * Truncated input surfaces as an `okio.EOFException`, not [RiffletParseException]; callers wrap
 * I/O errors at the root parser, matching this codebase's existing convention.
 */
internal fun explode(source: BufferedSource): ByteString {
    val reader = PkwareBitReader(source)

    val literalsCoded = reader.bits(8)
    if (literalsCoded > 1) {
        throw RiffletParseException("Invalid PKWare Implode literal-encoding flag: $literalsCoded")
    }
    val dictionaryBits = reader.bits(8)
    if (dictionaryBits < 4 || dictionaryBits > 6) {
        throw RiffletParseException("Invalid PKWare Implode dictionary size: $dictionaryBits")
    }

    val output = Buffer()
    while (true) {
        if (reader.bits(1) != 0) {
            val lengthSymbol = reader.decode(lengthTable)
            val length = LENGTH_BASE[lengthSymbol] + reader.bits(LENGTH_EXTRA_BITS[lengthSymbol])
            if (length == END_OF_STREAM_LENGTH) break

            val distanceExtraBits = if (length == 2) 2 else dictionaryBits
            val distance = (reader.decode(distanceTable) shl distanceExtraBits) + reader.bits(distanceExtraBits) + 1
            if (distance > output.size) {
                throw RiffletParseException(
                    "PKWare Implode distance $distance exceeds decompressed output so far (${output.size})",
                )
            }
            repeat(length) {
                output.writeByte(output[output.size - distance].toInt())
            }
        } else {
            val literal = if (literalsCoded == 1) reader.decode(literalTable) else reader.bits(8)
            output.writeByte(literal)
        }
    }
    return output.readByteString()
}
