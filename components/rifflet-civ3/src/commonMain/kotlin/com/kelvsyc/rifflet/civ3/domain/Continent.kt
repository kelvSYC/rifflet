package com.kelvsyc.rifflet.civ3.domain

import com.kelvsyc.rifflet.civ3.ContType

/**
 * A contiguous body of land or water — the domain-layer counterpart to
 * [com.kelvsyc.rifflet.civ3.ContEntry]. Read-only: unlike every other domain type in this series,
 * nothing about a continent is an independently settable property — [type]/[numberOfTiles] are
 * both fully computed summaries of which [Tile]s point to this continent, not data a caller edits
 * directly. A mutable field here could produce an incoherent state no amount of local editing
 * could ever reconcile (e.g. a [ContType.WATER]-labeled continent that real [Tile]s still
 * reference with genuine [ContType.LAND] terrain) — there is no legitimate "change this
 * continent" operation that doesn't also require rewriting every tile that belongs to it.
 *
 * @param type [ContType.WATER] or [ContType.LAND].
 * @param numberOfTiles How many tiles belong to this continent.
 */
data class Continent(
    val type: ContType,
    val numberOfTiles: Int,
)
