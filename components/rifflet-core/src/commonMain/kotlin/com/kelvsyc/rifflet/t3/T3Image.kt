package com.kelvsyc.rifflet.t3

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
}
