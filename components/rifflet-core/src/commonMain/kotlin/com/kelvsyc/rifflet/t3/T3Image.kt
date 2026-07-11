package com.kelvsyc.rifflet.t3

/**
 * The result of parsing a complete T3 image file.
 *
 * @param blocks Every block encountered, in file order, including the terminal [EndBlock].
 */
data class T3Image(val header: T3Header, val blocks: List<T3Block>)
