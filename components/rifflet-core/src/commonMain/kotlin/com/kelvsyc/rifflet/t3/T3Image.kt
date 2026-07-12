package com.kelvsyc.rifflet.t3

import okio.Buffer
import okio.ByteString

/**
 * The result of parsing a complete T3 image file.
 *
 * @param blocks Every block encountered, in file order, including the terminal [EndBlock].
 */
data class T3Image(val header: T3Header, val blocks: List<T3Block>) {
    /**
     * Finds the first multimedia resource (in file order, across every [MresBlock] and
     * [MrelBlock]) whose name matches [name], case-insensitively — matching the reference VM's
     * combined-namespace scan across both embedded and linked resources.
     */
    fun findResource(name: String): T3Resource? =
        blocks.asSequence().firstNotNullOfOrNull { block ->
            when (block) {
                is MresBlock -> block.find(name)
                is MrelBlock -> block.find(name)
                else -> null
            }
        }

    /**
     * Returns the first [ObjsObject] whose [ObjsObject.objectId] equals [objectId],
     * scanning all [ObjsBlock] entries in file order, or null if not found.
     */
    fun findObject(objectId: UInt): ObjsObject? =
        blocks.filterIsInstance<ObjsBlock>()
            .flatMap { it.objects }
            .firstOrNull { it.objectId == objectId }

    /**
     * Reads [size] bytes from pool [poolId] (1-based) starting at byte offset [offset],
     * returning the de-masked [ByteString]. Handles reads that span page boundaries.
     * Returns null if [poolId] is invalid, no [CpdfBlock] is present, or a required
     * [CppgBlock] page is absent.
     */
    fun readFromPool(poolId: Int, offset: UInt, size: Int): ByteString? {
        if (size == 0) return ByteString.EMPTY
        val poolEntry = blocks.filterIsInstance<CpdfBlock>().firstOrNull()
            ?.poolEntry(poolId) ?: return null
        val pageSize = poolEntry.pageSize
        val buffer = Buffer()
        var remaining = size
        var currentOffset = offset
        while (remaining > 0) {
            val pageIndex = currentOffset / pageSize
            val inPageOffset = (currentOffset % pageSize).toInt()
            val page = blocks.filterIsInstance<CppgBlock>()
                .firstOrNull { it.poolId == poolId && it.pageIndex == pageIndex } ?: return null
            val bytesFromPage = minOf(remaining, (pageSize - currentOffset % pageSize).toInt())
            buffer.write(page.pageData.substring(inPageOffset, inPageOffset + bytesFromPage))
            remaining -= bytesFromPage
            currentOffset += bytesFromPage.toUInt()
        }
        return buffer.readByteString()
    }
}
