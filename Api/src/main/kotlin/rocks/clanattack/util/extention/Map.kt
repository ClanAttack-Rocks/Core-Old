package rocks.clanattack.util.extention

import rocks.clanattack.util.json.JsonDocument

/**
 * Maps the values of the map to a list of the results of applying the given transform function to each value of the original map.
 *
 * If any transformation fails, the value will be null
 *
 * @param transform the transform function to apply to each value
 * @return the list of the results of applying the given transform function to each value of the original map
 */
inline fun <K, V, R> Map<out K, V>.mapValuesCatching(transform: (Map.Entry<K, V>) -> R) =
    mapValues { runCatching { transform(it) }.getOrNull() }

/**
 * Maps the [Map] to a list of [JsonDocument]s
 *
 * @param transform the transform function to apply to each value
 * @receiver The map to map
 * @return the list of [JsonDocument]s
 */
inline fun <K, V> Map<out K, V>.mapToJson(crossinline transform: JsonDocument.(Map.Entry<K, V>) -> Unit) =
    map { JsonDocument.fromBlock { transform(it) } }