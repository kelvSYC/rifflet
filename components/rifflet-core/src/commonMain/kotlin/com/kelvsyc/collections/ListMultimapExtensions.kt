package com.kelvsyc.collections

import com.kelvsyc.collections.internal.ImmutableListMultimap

/**
 * Returns `true` if all key/value pairs in this multimap match the given [predicate].
 */
fun <K, V> ListMultimap<out K, V>.all(predicate: (Pair<K, V>) -> Boolean): Boolean = entries.all(predicate)

/**
 * Returns `true` if this multimap has at least one key-value pair.
 */
fun <K, V> ListMultimap<out K, V>.any(): Boolean = entries.any()

/**
 * Returns `true` if at least one key-value pair in this multimap matches the given [predicate].
 */
fun <K, V> ListMultimap<out K, V>.any(predicate: (Pair<K, V>) -> Boolean): Boolean = entries.any(predicate)

/**
 * Checks if this multimap contains the given [key].
 */
operator fun <K, V> ListMultimap<out K, V>.contains(key: K): Boolean = asMap.containsKey(key)

/**
 * Checks if this multimap contains the given [key].
 */
fun <K> ListMultimap<out K, *>.containsKey(key: K): Boolean = asMap.containsKey(key)

/**
 * Returns the number of entries in this multimap.
 */
fun <K, V> ListMultimap<out K, V>.count(): Int = size

/**
 * Returns the number of entries in this multimap matching the given [predicate].
 */
fun <K, V> ListMultimap<out K, V>.count(predicate: (Pair<K, V>) -> Boolean): Int = entries.count(predicate)

/**
 * Returns a single list of all elements yielded from results of [transform] being invoked on each key-value pair in
 * the original multimap.
 */
fun <K, V, R> ListMultimap<out K, V>.flatMap(transform: (Pair<K, V>) -> Iterable<R>): List<R> = entries.flatMap(transform)

/**
 * Returns a single list of all elements yielded from results of [transform] being invoked on each key-value pair in
 * the original multimap.
 */
@JvmName("flatMapSequence")
fun <K, V, R> ListMultimap<out K, V>.flatMap(transform: (Pair<K, V>) -> Sequence<R>): List<R> = entries.flatMap(transform)

/**
 * Performs the given [action] on each entry in this multimap.
 */
fun <K, V> ListMultimap<out K, V>.forEach(action: (Pair<K, V>) -> Unit) = entries.forEach(action)

/**
 * Returns `true` if this multimap has at least one key/value pair.
 */
fun <K, V> ListMultimap<out K, V>.isNotEmpty(): Boolean = entries.isNotEmpty()

/**
 * Returns `true` if this nullable multimap is either `null` or empty.
 */
fun <K, V> ListMultimap<out K, V>?.isNullOrEmpty(): Boolean = this?.entries.isNullOrEmpty()

/**
 * Returns a [List] containing the results of applying the given [transform] function to each key-value pair in the
 * original multimap.
 */
fun <K, V, R> ListMultimap<out K, V>.map(transform: (Pair<K, V>) -> R): List<R> = entries.map(transform)

/**
 * Returns a [List] containing only the non-`null` results of applying the given [transform] function to each key-value
 * pair in the original multimap.
 */
fun <K, V, R> ListMultimap<out K, V>.mapNotNull(transform: (Pair<K, V>) -> R?): List<R> = entries.mapNotNull(transform)

/**
 * Returns `true` if this multimap has no key-value pairs.
 */
fun <K, V> ListMultimap<out K, V>.none(): Boolean = entries.none()

/**
 * Returns `true` if no key-value pairs in this multimap matches the given [predicate].
 */
fun <K, V> ListMultimap<out K, V>.none(predicate: (Pair<K, V>) -> Boolean): Boolean = entries.none(predicate)

/**
 * Returns this multimap if not `null`, or an empty [ListMultimap] otherwise.
 */
fun <K, V> ListMultimap<K, V>?.orEmpty(): ListMultimap<K, V> = this ?: emptyListMultimap()

/**
 * Returns a [List] containing all the key-value pairs in this multimap.
 */
fun <K, V> ListMultimap<out K, V>.toList(): List<Pair<K, V>> = entries.toList()
