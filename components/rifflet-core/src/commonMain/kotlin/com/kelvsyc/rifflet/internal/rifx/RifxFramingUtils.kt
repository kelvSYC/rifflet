package com.kelvsyc.rifflet.internal.rifx

import com.kelvsyc.rifflet.core.ChunkId
import okio.Buffer

/**
 * Writes a complete RIFX chunk — outer type ID (big-endian), big-endian body size, body bytes,
 * and an even-alignment padding byte when the body length is odd — to [destination].
 *
 * The [body] buffer is consumed (emptied) by this call.
 */
internal fun writeRifxChunk(typeId: ChunkId, body: Buffer, destination: Buffer) {
    val bodySize = body.size
    destination.writeInt(typeId.data)
    destination.writeInt(bodySize.toUInt().toInt())
    destination.writeAll(body)
    if (bodySize % 2L == 1L) destination.writeByte(0)
}

/**
 * Writes a complete RIFX group chunk (`RIFX` or `LIST`) to [destination].
 *
 * Prepends [innerTypeId] to [innerBody] to form the full group body, then writes the outer chunk
 * via [writeRifxChunk]. [innerBody] is consumed by this call.
 */
internal fun writeRifxGroupChunk(outerTypeId: ChunkId, innerTypeId: ChunkId, innerBody: Buffer, destination: Buffer) {
    val fullBody = Buffer()
    fullBody.writeInt(innerTypeId.data)
    fullBody.writeAll(innerBody)
    writeRifxChunk(outerTypeId, fullBody, destination)
}
