package rocks.clanattack.extention

import rocks.clanattack.json.JsonDocument

/**
 * Check if the iterable is empty
 *
 * @return true if the iterable is empty
 */
fun Iterable<*>.isEmpty() = !iterator().hasNext()

/**
 * Check if the iterable is not empty
 *
 * @return true if the iterable is not empty
 */
fun Iterable<*>.isNotEmpty() = !isEmpty()

/**
 * Maps the iterable to a list of the results of applying the given transform function to each element of the original collection.
 *
 * If any transformation fails, the value will be null
 *
 * @param transform the transform function to apply to each element
 * @return the list of the results of applying the given transform function to each element of the original collection
 */
inline fun <T, R> Iterable<T>.mapCatching(transform: (T) -> R) = map { runCatching { transform(it) }.getOrNull() }

/**
 * Maps the iterable to a list of [JsonDocument]s
 *
 * @param transform the transform function to apply to each element
 * @return the list of [JsonDocument]s
 * @see JsonDocument
 */
inline fun <T> Iterable<T>.mapToJson(transform: JsonDocument.(T) -> Unit) =
    map { JsonDocument.empty().apply { transform(it) } }

/**
 * Filters out any value, where the first or second value of the pair is null
 *
 * @receiver The iterable to filter
 * @return The filtered iterable
 * @see Pair
 */
fun <T, R> Iterable<Pair<T?, R?>>.filterNonNull() = filter { it.first != null && it.second != null }
    .map { it.first!! to it.second!! }