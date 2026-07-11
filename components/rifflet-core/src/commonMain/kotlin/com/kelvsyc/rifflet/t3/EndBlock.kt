package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `EOF ` block terminating a T3 image file's block sequence. Carries no data.
 */
data object EndBlock : T3Block {
    override val chunkId: ChunkId get() = T3BlockIds.EOF
}
