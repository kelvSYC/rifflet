package com.kelvsyc.rifflet.civ3

import com.kelvsyc.rifflet.core.ChunkId
import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.internal.civ3.Civ3RootParserImpl
import com.kelvsyc.rifflet.internal.core.readChunkId
import com.kelvsyc.rifflet.internal.pkware.explode
import okio.Buffer
import okio.BufferedSource
import okio.IOException
import okio.Source
import okio.buffer

internal val BIC_MAGIC = ChunkId("BIC ")
internal val BICX_MAGIC = ChunkId("BICX")

/**
 * Parses a Civ3 BIC/BIX/BIQ file — compressed with PKWare DCL "Implode" or not — into a
 * [Civ3File].
 *
 * Compression is auto-detected: if the leading 4 bytes are not a recognized file magic, the whole
 * source is treated as a PKWare Implode stream and decompressed before parsing.
 *
 * This only checks whether the bytes obey the wire format's own shape — never whether the
 * resulting [Civ3File]'s contents make sense as a Civ3 scenario (a building whose required
 * government points nowhere still parses cleanly). For that,
 * see [com.kelvsyc.rifflet.civ3.validation.ValidationRule].
 *
 * @throws RiffletParseException if the source is not a well-formed Civ3 file, compressed or not.
 */
object Civ3RootParser {
    fun parse(source: Source): Civ3File {
        val buffered = (source as? BufferedSource) ?: source.buffer()
        return buffered.use {
            try {
                if (hasCiv3Magic(buffered)) {
                    val magic = buffered.readChunkId()
                    Civ3RootParserImpl.parse(buffered, magic)
                } else {
                    val decompressed = Buffer().write(explode(buffered))
                    if (!hasCiv3Magic(decompressed)) {
                        throw RiffletParseException(
                            "Not a Civ3 BIC/BIX/BIQ file: bad magic signature after decompression",
                        )
                    }
                    val magic = decompressed.readChunkId()
                    Civ3RootParserImpl.parse(decompressed, magic)
                }
            } catch (e: RiffletParseException) {
                throw e
            } catch (e: IOException) {
                throw RiffletParseException("I/O error while parsing: ${e.message}", e)
            }
        }
    }

    private fun hasCiv3Magic(source: BufferedSource): Boolean {
        val magic = source.peek().readChunkId()
        return magic == BIC_MAGIC || magic == BICX_MAGIC
    }
}
