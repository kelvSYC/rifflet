package com.kelvsyc.rifflet.core

/**
 * Thrown when an encode operation cannot complete because the encoder is misconfigured — for
 * example, when a disassembler produces a child type that has no registered encoder in the core.
 */
class RiffletEncodeException(message: String, cause: Throwable? = null) : Exception(message, cause)
