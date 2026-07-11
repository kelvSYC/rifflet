package com.kelvsyc.rifflet.t3

data class SrcfFileRecord(
    val masterFileIndex: Int,
    val filename: String,
    val lineRecords: List<SrcfLineRecord>,
)
