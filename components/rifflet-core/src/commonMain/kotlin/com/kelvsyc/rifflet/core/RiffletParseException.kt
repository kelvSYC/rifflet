package com.kelvsyc.rifflet.core

/**
 * Thrown when a parse operation encounters malformed or unexpected input that prevents successful
 * decoding of a chunk or chunk tree.
 */
class RiffletParseException(message: String, cause: Throwable? = null) : Exception(message, cause)
