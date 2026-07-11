package com.kelvsyc.rifflet.t3

/**
 * A named multimedia resource, found via [T3Image.findResource] regardless of whether it came
 * from an embedded [MresEntry] or a linked [MrelEntry].
 */
sealed interface T3Resource {
    val name: String
}
