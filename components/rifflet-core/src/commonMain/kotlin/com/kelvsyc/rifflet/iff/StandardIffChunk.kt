package com.kelvsyc.rifflet.iff

/**
 * Base interface for chunk types that are defined by the Interchange File Format.
 *
 * The IFF standard defines two broad types of chunks: the [BlankChunk] and the [GroupChunk].
 */
sealed interface StandardIffChunk : IffChunk
