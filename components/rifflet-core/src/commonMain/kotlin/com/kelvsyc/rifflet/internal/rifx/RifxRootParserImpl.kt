package com.kelvsyc.rifflet.internal.rifx

import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.riff.RiffFormChunk
import com.kelvsyc.rifflet.riff.RiffFormChunkParser
import com.kelvsyc.rifflet.riff.RiffParserCore
import com.kelvsyc.rifflet.rifx.RifxRootParser
import okio.BufferedSource
import okio.IOException
import okio.Source
import okio.buffer

internal class RifxRootParserImpl<T>(
    private val root: RifxRootParser.Root,
    private val core: RiffParserCore,
) : RifxRootParser<T> {

    class Builder<T> : RifxRootParser.Builder<T> {
        override lateinit var root: RifxRootParser.Root
        private var coreInternal: RiffParserCore? = null

        override fun core(core: RiffParserCore) { coreInternal = core }
        override fun core(fn: RiffParserCore.Builder.() -> Unit) { coreInternal = RiffParserCore.newCore(fn) }

        fun build(): RifxRootParserImpl<T> {
            check(::root.isInitialized) { "root must be set" }
            val core = checkNotNull(coreInternal) { "core must be set" }
            return RifxRootParserImpl(root, core)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun parse(source: Source): T {
        val buffered = (source as? BufferedSource) ?: source.buffer()
        return buffered.use {
            try {
                val raw = RifxBufferedChunkParser.parse(buffered)
                val rootChunk = RawRifxChunkParser.parse(raw)
                if (rootChunk !is RiffFormChunk)
                    throw RiffletParseException("Expected RIFX root chunk but got '${raw.type.name}'")
                if (rootChunk.type != root.type)
                    throw RiffletParseException("Expected RIFX form-type '${root.type.name}' but got '${rootChunk.type.name}'")
                val parser = core.formParsers[root.type] as? RiffFormChunkParser<T>
                    ?: throw RiffletParseException("No registered parser for RIFX form-type '${root.type.name}'")
                parser.parse(rootChunk.chunks)
            } catch (e: RiffletParseException) {
                throw e
            } catch (e: IOException) {
                throw RiffletParseException("I/O error while parsing: ${e.message}", e)
            }
        }
    }
}
