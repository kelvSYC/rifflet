package com.kelvsyc.rifflet.internal.riff

import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.riff.RiffFormChunk
import com.kelvsyc.rifflet.riff.RiffFormChunkParser
import com.kelvsyc.rifflet.riff.RiffParserCore
import com.kelvsyc.rifflet.riff.RiffRootParser
import okio.BufferedSource
import okio.IOException
import okio.Source
import okio.buffer

internal class RiffRootParserImpl<T>(
    private val root: RiffRootParser.Root,
    private val core: RiffParserCore,
) : RiffRootParser<T> {

    class Builder<T> : RiffRootParser.Builder<T> {
        override lateinit var root: RiffRootParser.Root
        private var coreInternal: RiffParserCore? = null

        override fun core(core: RiffParserCore) { coreInternal = core }
        override fun core(fn: RiffParserCore.Builder.() -> Unit) { coreInternal = RiffParserCore.newCore(fn) }

        fun build(): RiffRootParserImpl<T> {
            check(::root.isInitialized) { "root must be set" }
            val core = checkNotNull(coreInternal) { "core must be set" }
            return RiffRootParserImpl(root, core)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun parse(source: Source): T {
        val buffered = (source as? BufferedSource) ?: source.buffer()
        return buffered.use {
            try {
                val raw = RiffBufferedChunkParser.parse(buffered)
                val rootChunk = RawRiffChunkParser.parse(raw)
                if (rootChunk !is RiffFormChunk)
                    throw RiffletParseException("Expected RIFF root chunk but got '${raw.type.name}'")
                if (rootChunk.type != root.type)
                    throw RiffletParseException("Expected RIFF form-type '${root.type.name}' but got '${rootChunk.type.name}'")
                val parser = core.formParsers[root.type] as? RiffFormChunkParser<T>
                    ?: throw RiffletParseException("No registered parser for RIFF form-type '${root.type.name}'")
                parser.parse(rootChunk.chunks)
            } catch (e: RiffletParseException) {
                throw e
            } catch (e: IOException) {
                throw RiffletParseException("I/O error while parsing: ${e.message}", e)
            }
        }
    }
}
