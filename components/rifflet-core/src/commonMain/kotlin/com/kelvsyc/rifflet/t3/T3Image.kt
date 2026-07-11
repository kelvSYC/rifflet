package com.kelvsyc.rifflet.t3

/**
 * The result of parsing a complete T3 image file.
 *
 * @param blocks Every block encountered, in file order, including the terminal [EndBlock].
 */
data class T3Image(val header: T3Header, val blocks: List<T3Block>) {
    /**
     * Finds the first multimedia resource (in file order, across every [MresBlock]) whose name
     * matches [name], case-insensitively — matching the reference VM's cross-block scan (see
     * [MresBlock.find]). Does not search `MREL` (external resource link) blocks, which are not
     * yet modeled.
     */
    fun findResource(name: String): MresEntry? =
        blocks.asSequence().filterIsInstance<MresBlock>().firstNotNullOfOrNull { it.find(name) }
}
