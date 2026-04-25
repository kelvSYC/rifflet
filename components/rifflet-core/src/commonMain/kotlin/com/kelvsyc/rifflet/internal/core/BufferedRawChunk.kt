package com.kelvsyc.rifflet.internal.core

import com.kelvsyc.rifflet.core.ChunkId
import okio.Buffer

/**
 * An intermediate representation of a raw chunk whose body is held in a [Buffer].
 *
 * Unlike [com.kelvsyc.rifflet.core.RawChunk], the body is never collapsed into a [ByteString][okio.ByteString],
 * so segment transfers from the source remain zero-copy through the structural parsing phase.
 * [data] is consumed by reading; callers must not read from it after passing it to a parser.
 *
 * [declaredSize] is the body size as declared in the chunk header. It always equals [data].size for
 * chunks whose body was transferred; for chunks whose body was skipped (e.g. blank chunks),
 * [data] will be empty while [declaredSize] still reflects the header value.
 *
 * This type is format-agnostic and can be used by any chunk-based format parser (IFF, RIFF, etc.).
 */
class BufferedRawChunk(val type: ChunkId, val data: Buffer, val declaredSize: Int)
