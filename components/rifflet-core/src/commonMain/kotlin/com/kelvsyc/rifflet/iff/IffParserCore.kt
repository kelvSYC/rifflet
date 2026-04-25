package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId

interface IffParserCore {
    val formParsers: Map<ChunkId, FormChunkParser<*>>
    val listParsers: Map<ChunkId, ListChunkParser<*>>
    val catParsers: Map<ChunkId, CatChunkParser<*>>
    val localParsers: Map<ChunkId, LocalChunkParser<*>>
}
