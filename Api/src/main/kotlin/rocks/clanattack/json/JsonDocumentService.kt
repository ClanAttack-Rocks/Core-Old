package rocks.clanattack.json

import rocks.clanattack.entry.find
import rocks.clanattack.entry.service.Service
import java.io.File
import java.io.InputStream

/**
 * The [JsonDocumentService] is used to create [JsonDocument]s.
 */
interface JsonDocumentService : Service {

    /**
     * Creates an empty [JsonDocument].
     */
    fun empty(): JsonDocument

    /**
     * Creates a [JsonDocument] from the given [map].
     */
    fun fromMap(map: Map<String, Any?>): JsonDocument

    /**
     * Creates a [JsonDocument] from the given [obj].
     */
    fun fromObject(obj: Any): JsonDocument

    /**
     * Creates a [JsonDocument] from the given [string].
     */
    fun fromString(string: String): JsonDocument

    /**
     * Creates a [JsonDocument] from the given [stream].
     */
    fun fromStream(stream: InputStream): JsonDocument

    /**
     * Creates a [JsonDocument] from the given [file].
     */
    fun fromFile(file: File): JsonDocument

    /**
     * Creates a [JsonDocument] from the given [block].
     */
    fun fromBlock(block: JsonDocument.() -> Unit): JsonDocument

}

/**
 * Creates an empty [JsonDocument].
 */
fun json() = find<JsonDocumentService>().empty()

/**
 * Creates a [JsonDocument] from the given [map].
 */
fun json(map: Map<String, Any?>) = find<JsonDocumentService>().fromMap(map)

/**
 * Creates a [JsonDocument] from the given [stream].
 */
fun json(stream: InputStream) = find<JsonDocumentService>().fromStream(stream)

/**
 * Creates a [JsonDocument] from the given [string].
 */
fun json(string: String) = find<JsonDocumentService>().fromString(string)

/**
 * Creates a [JsonDocument] from the given [file].
 */
fun json(file: File) = find<JsonDocumentService>().fromFile(file)

/**
 * Creates a [JsonDocument] from the given [block].
 */
fun json(block: JsonDocument.() -> Unit) = find<JsonDocumentService>().fromBlock(block)

/**
 * Creates a [JsonDocument] from the given [obj].
 */
fun json(obj: Any) = find<JsonDocumentService>().fromObject(obj)