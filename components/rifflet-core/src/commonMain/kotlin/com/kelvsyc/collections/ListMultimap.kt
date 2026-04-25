package com.kelvsyc.collections

/**
 * A `ListMultimap` is a multimap that holds duplicate key-value pairs, maintaining the insertion order of values for
 * a given key.
 */
interface ListMultimap<K, out V> {
    /**
     * Returns the number of key-value pairs in this multimap.
     */
    val size: Int

    /**
     * Returns a read-only [Collection] of all key-value pairs in this multimap.
     */
    val entries: Collection<Pair<K, V>>

    /**
     * Returns a read-only [Set] of all distinct keys in this multimap.
     */
    val keys: Set<K>

    /**
     * Returns a read-only [Collection] of values in this multimap. This collection may contain duplicate values.
     */
    val values: Collection<V>

    /**
     * Returns a read-only view of this multimap as a [Map] mapping distinct keys to a non-empty list of values.
     */
    val asMap: Map<K, List<V>>

    /**
     * Returns `true` if the map contains no key-value pairs, `false` otherwise.
     */
    fun isEmpty(): Boolean = size == 0

    /**
     * Returns `true` if this multimap contains the specified key.
     */
    fun containsKey(key: K): Boolean

    /**
     * Returns `true` if this multimap maps one or more keys to the specified value.
     */
    fun containsValue(value: @UnsafeVariance V): Boolean

    /**
     * Returns `true` if this multimap contains at least one key-value pair mapping [key] to [value].
     */
    fun containsEntry(key: K, value: @UnsafeVariance V): Boolean

    /**
     * Returns a view collection of the values of the supplied [key] in this multimap. If the key is not present in
     * this multimap, this function returns an empty list.
     *
     * The resulting values are ordered following the order of insertion.
     */
    operator fun get(key: K): List<V>

    /**
     * Compares the specified object with this multimap for equality. Two multimaps are equal if and only if [asMap]
     * returns [Map]s that are also equal.
     */
    override fun equals(other: Any?): Boolean

    /**
     * Returns the hash code for this multimap. The hashcode of this map is guaranteed to be the same as
     * [asMap][asMap]`.`[hashCode]`()`.
     */
    override fun hashCode(): Int
}
