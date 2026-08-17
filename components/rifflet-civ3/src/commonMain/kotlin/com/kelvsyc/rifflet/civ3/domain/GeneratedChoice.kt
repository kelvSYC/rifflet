package com.kelvsyc.rifflet.civ3.domain

/**
 * A "Generate a Map" dialog setting's requested value ([selected]) alongside what was actually
 * rolled ([actual]) — only meaningfully different when [selected] is a "Random" sentinel. Shared
 * shape for [WorldGenerationSettings]'s 6 dropdown pairs.
 */
data class GeneratedChoice<T>(var selected: T, var actual: T)
