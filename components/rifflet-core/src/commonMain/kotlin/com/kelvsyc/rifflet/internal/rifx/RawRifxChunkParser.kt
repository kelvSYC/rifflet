package com.kelvsyc.rifflet.internal.rifx

import com.kelvsyc.collections.toListMultimap
import com.kelvsyc.rifflet.core.RawChunk
import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.internal.core.BufferedRawChunk
import com.kelvsyc.rifflet.internal.core.readChunkId
import com.kelvsyc.rifflet.riff.RiffChunk
import com.kelvsyc.rifflet.riff.RiffChunkIds
import com.kelvsyc.rifflet.riff.RiffFormChunk
import com.kelvsyc.rifflet.riff.RiffListChunk
import com.kelvsyc.rifflet.riff.RiffLocalChunk
import com.kelvsyc.rifflet.rifx.RifxChunkIds

internal object RawRifxChunkParser {
    fun parse(raw: BufferedRawChunk): RiffChunk {
        return when (raw.type) {
            RifxChunkIds.RIFX -> parseContainer(raw)
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
                val sub = RifxBufferedChunkParser.parse(raw.data)
                // RIFX chunks must not appear nested inside another container.
                if (sub.type == RifxChunkIds.RIFX)
                    throw RiffletParseException("Nested RIFX chunk found inside ${raw.type.name} container")
                // Padding chunks are silently dropped.
                if (sub.type == RiffChunkIds.JUNK || sub.type == RiffChunkIds.PAD) continue
                add(parse(sub))
            }
        }.map { it.chunkId to it }.toListMultimap()
        return if (raw.type == RifxChunkIds.RIFX) RiffFormChunk(raw.type, type, chunks)
        else RiffListChunk(raw.type, type, chunks)
    }
}
