package com.kelvsyc.rifflet.t3

import com.kelvsyc.rifflet.internal.t3.T3RootParserImpl
import okio.Source

/**
 * Parses a binary T3 VM image file source into a [T3Image].
 *
 * Unlike [com.kelvsyc.rifflet.iff.IffRootParser]/[com.kelvsyc.rifflet.riff.RiffRootParser], there is
 * no configurable root variant or parser registry: T3's block-type vocabulary is closed and fully
 * enumerated by the format spec, so dispatch is fixed rather than caller-supplied.
 *
 * @throws com.kelvsyc.rifflet.core.RiffletParseException if the source is not a well-formed T3
 *   image file.
 */
object T3RootParser {
    fun parse(source: Source): T3Image = T3RootParserImpl.parse(source)
}
