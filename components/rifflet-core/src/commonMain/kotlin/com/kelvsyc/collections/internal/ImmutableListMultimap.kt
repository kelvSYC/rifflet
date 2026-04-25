package com.kelvsyc.collections.internal

import com.kelvsyc.collections.ListMultimap

class ImmutableListMultimap<K, out V>(private val map: Map<K, List<V>>) : ListMultimap<K, V> {
    override val asMap: Map<K, List<V>> by this::map

    override val size: Int
        get() = map.values.sumOf { it.size }

    override val keys: Set<K>
        get() = map.keys

    override val values: Collection<V>
        get() = map.values.flatMap { it }

    override val entries: Set<Pair<K, V>>
        get() = map.entries.flatMap { (key, values) ->
            values.map { key to it }
        }.toSet()

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
}
