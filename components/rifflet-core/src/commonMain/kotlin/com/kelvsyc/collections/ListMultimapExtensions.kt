package com.kelvsyc.collections

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
 * Returns the number of entries in this multimap.
 */
fun <K, V> ListMultimap<out K, V>.count(): Int = size

/**
 * Returns the number of entries in this multimap matching the given [predicate].
 */
fun <K, V> ListMultimap<out K, V>.count(predicate: (Pair<K, V>) -> Boolean): Int = entries.count(predicate)

/**
 * Returns a new [ListMultimap] containing only the key-value pairs matching the given [predicate].
 */
fun <K, V> ListMultimap<out K, V>.filter(predicate: (Pair<K, V>) -> Boolean): ListMultimap<K, V> =
    entries.filter(predicate).toListMultimap()

/**
 * Returns a new [ListMultimap] containing only the entries with keys matching the given [predicate].
 */
fun <K, V> ListMultimap<out K, V>.filterKeys(predicate: (K) -> Boolean): ListMultimap<K, V> =
    entries.filter { (k, _) -> predicate(k) }.toListMultimap()

/**
 * Returns a new [ListMultimap] containing only the entries with values matching the given [predicate]. Keys with no
 * remaining values are omitted from the result.
 */
fun <K, V> ListMultimap<out K, V>.filterValues(predicate: (V) -> Boolean): ListMultimap<K, V> =
    entries.filter { (_, v) -> predicate(v) }.toListMultimap()

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
 * Performs the given [action] on each key-value pair in this multimap.
 */
fun <K, V> ListMultimap<out K, V>.forEach(action: (K, V) -> Unit) = entries.forEach { (k, v) -> action(k, v) }

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
 * Returns a new [ListMultimap] with keys transformed by [transform]. If two original keys produce the same new key,
 * their value lists are concatenated in [entries] order. This differs from [Map.mapKeys], which is last-wins;
 * concatenation is used here since no values need to be discarded.
 */
fun <K, V, R> ListMultimap<out K, V>.mapKeys(transform: (K) -> R): ListMultimap<R, V> =
    entries.map { (k, v) -> transform(k) to v }.toListMultimap()

/**
 * Returns a [List] containing only the non-`null` results of applying the given [transform] function to each key-value
 * pair in the original multimap.
 */
fun <K, V, R> ListMultimap<out K, V>.mapNotNull(transform: (Pair<K, V>) -> R?): List<R> = entries.mapNotNull(transform)

/**
 * Returns a new [ListMultimap] with the same keys and values transformed by [transform].
 */
fun <K, V, R> ListMultimap<out K, V>.mapValues(transform: (V) -> R): ListMultimap<K, R> =
    entries.map { (k, v) -> k to transform(v) }.toListMultimap()

/**
 * Returns `true` if this multimap has no key-value pairs.
 */
fun <K, V> ListMultimap<out K, V>.none(): Boolean = entries.none()

/**
 * Returns a new [ListMultimap] containing all entries of the original multimap plus the given [pair].
 */
operator fun <K, V> ListMultimap<out K, V>.plus(pair: Pair<K, V>): ListMultimap<K, V> =
    (entries.toList() + pair).toListMultimap()

/**
 * Returns a new [ListMultimap] containing all entries of the original multimap plus all entries of [other]. Where keys
 * collide, value lists are concatenated in the order the original multimaps are given.
 */
operator fun <K, V> ListMultimap<out K, V>.plus(other: ListMultimap<out K, V>): ListMultimap<K, V> =
    (entries.toList() + other.entries).toListMultimap()

/**
 * Returns a new [ListMultimap] containing all entries of the original multimap plus all entries in [pairs].
 */
operator fun <K, V> ListMultimap<out K, V>.plus(pairs: Iterable<Pair<K, V>>): ListMultimap<K, V> =
    (entries.toList() + pairs).toListMultimap()

/**
 * Returns a new [ListMultimap] containing all entries of the original multimap except those with the given [key].
 */
operator fun <K, V> ListMultimap<out K, V>.minus(key: K): ListMultimap<K, V> =
    filterKeys { it != key }

/**
 * Returns a new [ListMultimap] containing all entries of the original multimap except those whose key is in [keys].
 */
operator fun <K, V> ListMultimap<out K, V>.minus(keys: Iterable<K>): ListMultimap<K, V> {
    val keySet = keys.toHashSet()
    return filterKeys { it !in keySet }
}

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
