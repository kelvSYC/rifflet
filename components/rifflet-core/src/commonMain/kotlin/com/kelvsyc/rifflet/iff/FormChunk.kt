package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId

data class FormChunk(val type: ChunkId, val chunks: List<IffChunk>) : GroupChunk
