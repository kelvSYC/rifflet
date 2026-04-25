package com.kelvsyc.collections.internal

import com.kelvsyc.collections.ListMultimap

internal class ImmutableListMultimap<K, out V>(map: Map<K, List<V>>) : ListMultimap<K, V> {
    private val map: Map<K, List<V>> = map.mapValues { (_, v) -> v.toList() }.filterValues { it.isNotEmpty() }

    override val asMap: Map<K, List<V>> by this::map

    override val size: Int
        get() = map.values.sumOf { it.size }

    override val keys: Set<K> by lazy { map.keys.toSet() }

    override val values: Collection<V> by lazy { map.values.flatMap { it } }

    override val entries: Collection<Pair<K, V>> by lazy {
        map.entries.flatMap { (key, values) -> values.map { key to it } }
    }

    override fun containsKey(key: K): Boolean = map.containsKey(key)

    override fun containsValue(value: @UnsafeVariance V): Boolean = map.values.any { it.contains(value) }

    override fun containsEntry(key: K, value: @UnsafeVariance V): Boolean = map[key]?.contains(value) == true

    override fun get(key: K): List<V> = map[key] ?: emptyList()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ListMultimap<*, *>) return false
        return map == other.asMap
    }

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = entries.toString()
}
