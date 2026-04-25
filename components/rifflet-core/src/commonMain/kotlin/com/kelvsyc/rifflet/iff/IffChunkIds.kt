package com.kelvsyc.rifflet.iff

import com.kelvsyc.rifflet.core.ChunkId

object IffChunkIds {
    val FORM = ChunkId("FORM")
    val LIST = ChunkId("LIST")
    val PROP = ChunkId("PROP")
    val CAT = ChunkId("CAT ")
    val blank = ChunkId("    ")

    val formIds = buildSet {
        add(FORM)
        addAll((1 .. 9).map { ChunkId("FOR$it") })
    }
    val listIds = buildSet {
        add(LIST)
        addAll((1 .. 9).map { ChunkId("LIS$it") })
    }
    val catIds = buildSet {
        add(CAT)
        addAll((1 .. 9).map { ChunkId("CAT$it") })
    }

    val reservedIds = buildSet {
        addAll(formIds)
        addAll(listIds)
        addAll(catIds)
        add(PROP)
        add(blank)
    }
}
