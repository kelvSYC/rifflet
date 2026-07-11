package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The `ENTP` block, giving the VM the code pool entry point and the sizes of several
 * globally-fixed data structures.
 *
 * @param debugTableFrameHeaderSize Present only in "v2" image files; `null` for the shorter "v1" body.
 */
data class EntryPointBlock(
    val codePoolEntryPointOffset: UInt,
    val methodHeaderSize: Int,
    val exceptionTableEntrySize: Int,
    val debuggerLineTableEntrySize: Int,
    val debugTableHeaderSize: Int,
    val localSymbolRecordHeaderSize: Int,
    val debugRecordsVersion: Int,
    val debugTableFrameHeaderSize: Int?,
) : T3Block {
    override val chunkId: ChunkId get() = T3BlockIds.ENTP
}
