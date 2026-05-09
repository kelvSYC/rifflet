package com.kelvsyc.rifflet.internal.riff

import com.kelvsyc.kotlin.core.collections.toListMultimap
import com.kelvsyc.rifflet.core.RawChunk
import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.internal.core.BufferedRawChunk
import com.kelvsyc.rifflet.internal.core.readChunkId
import com.kelvsyc.rifflet.riff.RiffChunkIds
import com.kelvsyc.rifflet.riff.RiffChunk
import com.kelvsyc.rifflet.riff.RiffFormChunk
import com.kelvsyc.rifflet.riff.RiffListChunk
import com.kelvsyc.rifflet.riff.RiffLocalChunk

internal object RawRiffChunkParser {
    fun parse(raw: BufferedRawChunk): RiffChunk {
        return when (raw.type) {
            RiffChunkIds.RIFF -> parseContainer(raw)
            RiffChunkIds.LIST -> parseContainer(raw)
            else -> RiffLocalChunk(RawChunk(raw.type, raw.data.readByteString()))
        }
    }

    private fun parseContainer(raw: BufferedRawChunk): RiffChunk {
        if (raw.declaredSize < 4u)
            throw RiffletParseException("${raw.type.name} chunk declared size ${raw.declaredSize} is too small to contain a content type ID")
        val type = raw.data.readChunkId()
        val chunks = buildList<RiffChunk> {
            while (!raw.data.exhausted()) {
                val sub = RiffBufferedChunkParser.parse(raw.data)
                // RIFF chunks must not appear nested inside another container.
                if (sub.type == RiffChunkIds.RIFF)
                    throw RiffletParseException("Nested RIFF chunk found inside ${raw.type.name} container")
                // Padding chunks are silently dropped.
                if (sub.type == RiffChunkIds.JUNK || sub.type == RiffChunkIds.PAD) continue
                add(parse(sub))
            }
        }.map { it.chunkId to it }.toListMultimap()
        return if (raw.type == RiffChunkIds.RIFF) RiffFormChunk(raw.type, type, chunks)
        else RiffListChunk(raw.type, type, chunks)
    }
}
