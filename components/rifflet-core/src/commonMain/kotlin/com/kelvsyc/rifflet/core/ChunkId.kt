package com.kelvsyc.rifflet.core

/**
 * A `ChunkId` is a four-byte structure representing the format and purpose of an individual chunk.
 * The chunk ID is commonly represented as a four-character ASCII string.
 *
 * The four bytes are packed into a single [Int] in big-endian order so that equality and hashing
 * are plain integer operations, and reading a chunk ID from a source requires no intermediate
 * allocation (a single [okio.BufferedSource.readInt] call suffices).
 *
 * @param data The four bytes of the chunk ID packed as a big-endian [Int].
 */
data class ChunkId(val data: Int) {
    /**
     * Creates a [ChunkId] from a four-character string representation.
     */
    constructor(name: String) : this(pack(name))

    /**
     * Returns the chunk ID as a four-character string representation.
     */
    val name: String
        get() = buildString(4) {
            append(((data shr 24) and 0xFF).toChar())
            append(((data shr 16) and 0xFF).toChar())
            append(((data shr 8) and 0xFF).toChar())
            append((data and 0xFF).toChar())
        }

    companion object {
        private fun pack(name: String): Int {
            check(name.length == 4) { "Chunk IDs must have 4 characters." }
            return (name[0].code shl 24) or (name[1].code shl 16) or (name[2].code shl 8) or name[3].code
        }
    }
}
