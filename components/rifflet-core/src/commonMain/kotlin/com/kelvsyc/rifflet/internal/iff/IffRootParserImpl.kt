package com.kelvsyc.rifflet.internal.iff

import com.kelvsyc.rifflet.core.RiffletParseException
import com.kelvsyc.rifflet.iff.CatChunk
import com.kelvsyc.rifflet.iff.CatChunkParser
import com.kelvsyc.rifflet.iff.FormChunk
import com.kelvsyc.rifflet.iff.FormChunkParser
import com.kelvsyc.rifflet.iff.IffParserCore
import com.kelvsyc.rifflet.iff.IffRootParser
import com.kelvsyc.rifflet.iff.ListChunk
import com.kelvsyc.rifflet.iff.ListChunkParser
import okio.BufferedSource
import okio.Source
import okio.buffer

internal class IffRootParserImpl<T>(
    private val root: IffRootParser.Root,
    private val core: IffParserCore,
) : IffRootParser<T> {

    class Builder<T> : IffRootParser.Builder<T> {
        override lateinit var root: IffRootParser.Root
        private var coreInternal: IffParserCore? = null

        override fun core(core: IffParserCore) { coreInternal = core }
        override fun core(fn: IffParserCore.Builder.() -> Unit) { coreInternal = IffParserCore.newCore(fn) }

        fun build(): IffRootParserImpl<T> {
            check(::root.isInitialized) { "root must be set" }
            val core = checkNotNull(coreInternal) { "core must be set" }
            return IffRootParserImpl(root, core)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun parse(source: Source): T {
        // Avoid wrapping an already-buffered source; an extra layer adds a redundant segment-move per read.
        val buffered = (source as? BufferedSource) ?: source.buffer()
        return buffered.use {
            try {
                val raw = IffBufferedChunkParser.parse(buffered)
                when (val rootChunk = RawIffChunkParser.parse(raw)) {
                    is FormChunk -> {
                        val r = root as? IffRootParser.Root.FormRoot
                            ?: throw RiffletParseException("Expected FORM root but got '${raw.type.name}'")
                        if (rootChunk.outerChunkId != r.variantId)
                            throw RiffletParseException("Expected '${r.variantId.name}' root but got '${rootChunk.outerChunkId.name}'")
                        if (rootChunk.type != r.type)
                            throw RiffletParseException("Expected FORM type '${r.type.name}' but got '${rootChunk.type.name}'")
                        val parser = core.formParsers[r.type] as? FormChunkParser<T>
                            ?: throw RiffletParseException("No registered parser for FORM type '${r.type.name}'")
                        parser.parse(rootChunk.chunks)
                    }
                    is ListChunk -> {
                        val r = root as? IffRootParser.Root.ListRoot
                            ?: throw RiffletParseException("Expected LIST root but got '${raw.type.name}'")
                        if (rootChunk.outerChunkId != r.variantId)
                            throw RiffletParseException("Expected '${r.variantId.name}' root but got '${rootChunk.outerChunkId.name}'")
                        if (rootChunk.type != r.type)
                            throw RiffletParseException("Expected LIST type '${r.type.name}' but got '${rootChunk.type.name}'")
                        val parser = core.listParsers[r.type] as? ListChunkParser<T>
                            ?: throw RiffletParseException("No registered parser for LIST type '${r.type.name}'")
                        parser.parse(rootChunk.items, rootChunk.properties)
                    }
                    is CatChunk -> {
                        val r = root as? IffRootParser.Root.CatRoot
                            ?: throw RiffletParseException("Expected CAT root but got '${raw.type.name}'")
                        if (rootChunk.outerChunkId != r.variantId)
                            throw RiffletParseException("Expected '${r.variantId.name}' root but got '${rootChunk.outerChunkId.name}'")
                        if (rootChunk.hint != r.hint)
                            throw RiffletParseException("Expected CAT hint '${r.hint.name}' but got '${rootChunk.hint.name}'")
                        val parser = core.catParsers[r.hint] as? CatChunkParser<T>
                            ?: throw RiffletParseException("No registered parser for CAT hint '${r.hint.name}'")
                        parser.parse(rootChunk.chunks)
                    }
                    else -> throw RiffletParseException("Illegal root chunk type '${raw.type.name}'")
                }
            } catch (e: RiffletParseException) {
                throw e
            } catch (e: Exception) {
                throw RiffletParseException("I/O error while parsing: ${e.message}", e)
            }
        }
    }
}
