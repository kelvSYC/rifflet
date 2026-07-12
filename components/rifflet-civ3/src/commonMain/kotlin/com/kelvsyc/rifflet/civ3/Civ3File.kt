package com.kelvsyc.rifflet.civ3

/**
 * The result of parsing a complete Civ3 BIC/BIX/BIQ file.
 *
 * @param sections Every section encountered after the `VER#` header, in file order.
 */
data class Civ3File(val header: Civ3Header, val sections: List<Civ3Section>)
