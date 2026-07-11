package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.core.ChunkId

/**
 * The complete, closed set of block type IDs defined by the T3 VM image file format spec.
 */
object T3BlockIds {
    val ENTP = ChunkId("ENTP")
    val OBJS = ChunkId("OBJS")
    val CPDF = ChunkId("CPDF")
    val CPPG = ChunkId("CPPG")
    val MRES = ChunkId("MRES")
    val MREL = ChunkId("MREL")
    val MCLD = ChunkId("MCLD")
    val FNSD = ChunkId("FNSD")
    val SYMD = ChunkId("SYMD")
    val SRCF = ChunkId("SRCF")
    val GSYM = ChunkId("GSYM")
    val MHLS = ChunkId("MHLS")
    val MACR = ChunkId("MACR")
    val SINI = ChunkId("SINI")
    val EOF = ChunkId("EOF ")

    /** Bit 0 of a block header's flags field: block must be recognized, not silently skipped. */
    const val MANDATORY_FLAG: Int = 0x0001
}
