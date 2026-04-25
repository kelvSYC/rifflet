package com.kelvsyc.collections

import com.kelvsyc.collections.internal.ImmutableListMultimap

/**
 * Returns an empty read-only multimap of the specified type.
 */
fun <K, V> emptyListMultimap(): ListMultimap<K, V> = ImmutableListMultimap(emptyMap())

/**
 * Returns a new read-only multimap with the specified contents, given as a list of key-value pairs where the first
 * value is the key and the second is the value.
 */
fun <K, V> listMultimapOf(vararg pairs: Pair<K, V>): ListMultimap<K, V> {
    val result = mutableMapOf<K, MutableList<V>>()
    pairs.forEach {
        val value = result.getOrPut(it.first, ::mutableListOf)
        value.add(it.second)
    }
    return ImmutableListMultimap(result)
}

/**
 * Returns a new multimap containing all the key-value pairs from the given collection of [Pair]s.
 */
fun <K, V> Iterable<Pair<K, V>>.toListMultimap(): ListMultimap<K, V> {
    val result = mutableMapOf<K, MutableList<V>>()
    forEach {
        val value = result.getOrPut(it.first, ::mutableListOf)
        value.add(it.second)
    }
    return ImmutableListMultimap(result)
}

/**
 * Returns a new multimap containing all the key-value pairs from the given [Sequence] of [Pair]s.
 */
fun <K, V> Sequence<Pair<K, V>>.toListMultimap(): ListMultimap<K, V> {
    val result = mutableMapOf<K, MutableList<V>>()
    forEach {
        val value = result.getOrPut(it.first, ::mutableListOf)
        value.add(it.second)
    }
    return ImmutableListMultimap(result)
}
