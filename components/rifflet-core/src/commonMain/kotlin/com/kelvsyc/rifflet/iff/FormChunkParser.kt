package com.kelvsyc.rifflet.iff

/**
 * The `FormChunkParser` interface is used to translate a [FormChunk] into a domain object.
 */
interface FormChunkParser<T> {
    /**
     * Parses the chunks from a `FORM` chunk into a domain object.
     *
     * @param chunks The sub-chunks of the supplied `FORM` chunk
     * @param properties Any local properties passed in from outer `LIST` chunks
     */
    fun parse(chunks: List<IffChunk>, properties: List<LocalChunk> = emptyList()): T
}
