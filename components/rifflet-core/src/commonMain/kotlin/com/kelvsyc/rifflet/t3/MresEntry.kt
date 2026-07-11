package com.kelvsyc.rifflet.t3

import okio.ByteString

/**
 * A single resource entry from an `MRES` block's table of contents.
 *
 * @param offset The offset of this resource's binary data, relative to the start of the `MRES`
 *   block's own data (the start of the table of contents), per the T3 spec.
 * @param body The whole `MRES` block's data, shared (by reference, not copied) across every entry
 *   parsed from the same block, so that [data] can slice out just this entry's bytes on demand.
 */
data class MresEntry(override val name: String, val offset: UInt, val size: UInt, private val body: ByteString) : T3Resource {
    /** This resource's binary data, sliced from the shared block body only when called. */
    fun data(): ByteString = body.substring(offset.toInt(), (offset + size).toInt())
}
