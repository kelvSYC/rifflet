package com.kelvsyc.rifflet.internal.t3

import com.kelvsyc.rifflet.core.ChunkId
import okio.Buffer

/**
 * An intermediate representation of a raw T3 block whose body is held in a [Buffer], read via
 * zero-copy segment transfer from the source.
 *
 * Deliberately separate from [com.kelvsyc.rifflet.internal.core.BufferedRawChunk]: that shared
 * IFF/RIFF type has no flags field, and T3's block header carries one (the mandatory-block bit).
 *
 * @param flags The block header's raw flags field (bit 0 = mandatory-to-recognize).
 */
internal class T3RawBufferedBlock(val type: ChunkId, val flags: Int, val data: Buffer, val declaredSize: UInt)
