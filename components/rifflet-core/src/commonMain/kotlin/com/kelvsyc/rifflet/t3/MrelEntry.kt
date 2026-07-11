package com.kelvsyc.rifflet.t3

/**
 * A single resource entry from an `MREL` block: a mapping from a resource name to a local
 * filename, for a resource stored as an external file rather than embedded in the image file.
 *
 * Rifflet does not attempt to resolve or read [filename] — it may not even exist locally, and
 * doing so is outside a format parser's responsibility. This type only exposes the parsed
 * mapping.
 */
data class MrelEntry(override val name: String, val filename: String) : T3Resource
