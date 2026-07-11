package com.kelvsyc.rifflet.internal.t3

import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.t3.EndBlock
import com.kelvsyc.rifflet.t3.T3Image
import okio.BufferedSource
import okio.IOException
import okio.Source
import okio.buffer

internal object T3RootParserImpl {
    fun parse(source: Source): T3Image {
        // Avoid wrapping an already-buffered source; an extra layer adds a redundant segment-move per read.
        val buffered = (source as? BufferedSource) ?: source.buffer()
        return buffered.use {
            try {
                val header = T3HeaderParser.parse(buffered)
                val blocks = buildList {
                    while (true) {
                        val raw = T3BufferedBlockParser.parse(buffered)
                        val block = T3BlockParser.parse(raw)
                        add(block)
                        if (block is EndBlock) break
                    }
                }
                T3Image(header, blocks)
            } catch (e: RiffletParseException) {
                throw e
            } catch (e: IOException) {
                throw RiffletParseException("I/O error while parsing: ${e.message}", e)
            }
        }
    }
}
