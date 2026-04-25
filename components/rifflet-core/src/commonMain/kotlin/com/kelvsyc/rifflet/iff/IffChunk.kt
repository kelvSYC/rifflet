package com.kelvsyc.rifflet.iff

/**
 * Generic representation of an IFF Chunk. There are two subtypes of IFF Chunks used by Rifflet: the
 * implementation-specific [LocalChunk], and the [StandardIffChunk] specified by the Interchange File Format standard.
 */
sealed interface IffChunk
