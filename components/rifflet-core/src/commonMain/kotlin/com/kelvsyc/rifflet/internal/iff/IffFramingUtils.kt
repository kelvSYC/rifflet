package com.kelvsyc.rifflet.internal.iff

import com.kelvsyc.rifflet.core.ChunkId
import okio.Buffer

/**
 * Writes a complete IFF chunk — outer type ID, big-endian body size, body bytes, and an even-alignment
 * padding byte when the body length is odd — to [destination].
 *
 * The [body] buffer is consumed (emptied) by this call.
 */
internal fun writeIffChunk(typeId: ChunkId, body: Buffer, destination: Buffer) {
    val bodySize = body.size
    destination.writeInt(typeId.data)
    destination.writeInt(bodySize.toInt())
    destination.writeAll(body)
    if (bodySize % 2L == 1L) destination.writeByte(0)
}

/**
 * Writes a complete IFF group chunk to [destination].
 *
 * Prepends [innerTypeId] to [innerBody] to form the full group body, then writes the outer chunk via
 * [writeIffChunk]. Handles even-alignment padding. [innerBody] is consumed by this call.
 */
internal fun writeGroupChunk(outerTypeId: ChunkId, innerTypeId: ChunkId, innerBody: Buffer, destination: Buffer) {
    val fullBody = Buffer()
    fullBody.writeInt(innerTypeId.data)
    fullBody.writeAll(innerBody)
    writeIffChunk(outerTypeId, fullBody, destination)
}
