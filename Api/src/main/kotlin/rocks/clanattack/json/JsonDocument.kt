package rocks.clanattack.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.convertValue
import rocks.clanattack.extention.letCatching
import rocks.clanattack.extention.mapCatching
import rocks.clanattack.extention.mapValuesCatching
import java.io.InputStream
import java.io.OutputStream
import kotlin.reflect.KClass

/**
 * A [JsonDocument] is a document, that can be serialized to a json string.
 */
class JsonDocument private constructor(private val data: MutableMap<String, JsonNode>) {

    private val listeners = mutableListOf<(JsonDocument) -> Unit>()

    /**
     * The origin of the [JsonDocument].
     *
     * If the [JsonDocument] was gotten from another [JsonDocument] with a key, this will be the origin.
     * If the [JsonDocument] was not gotten from another [JsonDocument] with a key, this will be null.
     */
    var origin: JsonDocument? = null
        private set

    private var originKey: String? = null

    /**
     * Add a listener to this [JsonDocument].
     *
     * The listener will be called, when the [JsonDocument] is changed.
     *
     * Changes are:
     * - a set of a value
     * - a remove of a value
     * - the clear of the [JsonDocument]
     *
     * @param listener The listener to add.
     * @return The [JsonDocument] itself.
     */
    fun addListener(listener: (JsonDocument) -> Unit) = apply { listeners.add(listener) }

    /**
     * Removes a listener from the [JsonDocument].
     *
     * @param listener The listener to remove.
     * @return The [JsonDocument] itself.
     */
    fun removeListener(listener: (JsonDocument) -> Unit) = apply { listeners.remove(listener) }

    private fun notifyListeners() = listeners.forEach { it(this) }

    /**
     * Get the [JsonDocument] as a [Map] of [String]s to [JsonNode]s.
     */
    val map: Map<String, JsonNode>
        get() = data

    /**
     * The keys of the [JsonDocument].
     */
    val keys: Set<String> = data.keys

    /**
     * The size of the [JsonDocument].
     */
    val size = data.size


    /* Helpers */
    /**
     * Prints the [JsonDocument] to an output stream.
     *
     * The [JsonDocument] will be printed as a (pretty) json string, to the output stream.
     *
     * Whether the json string should be pretty or not, can be specified with the [pretty] parameter.
     *
     * @param outputStream The output stream to print the [JsonDocument] to.
     * @param pretty Whether the json string should be pretty or not.
     * @return The [JsonDocument] itself.
     */
    fun printToStream(outputStream: OutputStream, pretty: Boolean = false) = apply {
        if (pretty) mapper.writerWithDefaultPrettyPrinter().writeValue(outputStream, data)
        else mapper.writeValue(outputStream, data)
    }

    /**
     * Clones the [JsonDocument] and returns the clone object.
     *
     * This will not include the [listeners], the [origin] or the [originKey].
     */
    fun clone() = apply { JsonDocument(data.toMutableMap()) }

    /**
     * If the [JsonDocument] has an origin, this will flush the changes to be included in the origin.
     *
     * If the [JsonDocument] has no origin, this will do nothing.
     *
     * This will also [flush] the [origin] and call the listeners of the [origin].
     *
     * @return The [JsonDocument] itself.
     */
    fun flush(): JsonDocument = apply {
        origin?.data?.put(originKey!!, data as JsonNode)
        origin?.notifyListeners()
        origin?.flush()
    }

    /**
     * Checks if the [JsonDocument] contains a value with the specified [key].
     *
     * @param key The key to check for.
     * @return Whether the [JsonDocument] contains a value with the specified [key].
     */
    operator fun contains(key: String) = key in data

    /**
     * Gets a value by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [JsonNode].
     *
     * If the value with the specified [key] does not exist, this will return null.
     *
     * @param key The key to get the value from.
     * @return The value with the specified [key] as a [JsonNode] or
     * null if no value with the specified [key] exists.
     */
    operator fun get(key: String) = data[key]

    /**
     * Gets a value by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [JsonNode].
     *
     * If the value with the specified [key] does not exist, this will return the [default] value.
     *
     * @param key The key to get the value from.
     * @param default The default value to return if no value with the specified [key] exists.
     * @return The value with the specified [key] as a [JsonNode] or
     * the [default] value if no value with the specified [key] exists.
     */
    fun get(key: String, default: JsonNode) = get(key) ?: default

    /**
     * Gets a [ValueNode] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [ValueNode].
     *
     * In other libraries this is called a primitive value.
     *
     * If the value with the specified [key] does not exist or
     * is not a [ValueNode], this will return null.
     *
     * @param key The key to get the value from.
     * @return The value with the specified [key] as a [ValueNode] or
     * null if no value with the specified [key] exists.
     */
    fun getPrimitive(key: String) = get(key) as? ValueNode

    /**
     * Gets a [ValueNode] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [ValueNode].
     *
     * In other libraries this is called a primitive value.
     *
     * If the value with the specified [key] does not exist or
     * is not a [ValueNode], this will return the [default] value.
     *
     * @param key The key to get the value from.
     * @param default The default value to return if no value with the specified [key] exists or the value is not a [ValueNode].
     * @return The value with the specified [key] as a [ValueNode] or
     * the [default] value if no value with the specified [key] exists or the value is not a [ValueNode].
     */
    fun getPrimitive(key: String, default: ValueNode) = getPrimitive(key) ?: default

    /**
     * Gets a [Byte] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [Byte].
     *
     * If the value with the specified [key] does not exist or
     * is not a [Byte] ([Number]), this will return null.
     *
     * @param key The key to get the value from.
     * @return The value with the specified [key] as a [Byte] or
     * null if the value with the specified [key] does not exist or is not a [Byte] ([Number]).
     */
    fun getByte(key: String) = getPrimitive(key)?.numberValue()?.toByte()

    /**
     * Gets a [Byte] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [Byte].
     *
     * If the value with the specified [key] does not exist or
     * is not a [Byte] ([Number]), this will return the [default] value.
     *
     * @param key The key to get the value from.
     * @param default The default value to return if no value with the specified [key] exists.
     * @return The value with the specified [key] as a [Byte] or
     * the [default] value if the value with the specified [key] does not exist or is not a [Byte] ([Number]).
     */
    fun getByte(key: String, default: Byte) = getByte(key) ?: default

    /**
     * Gets a [Short] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [Short].
     *
     * If the value with the specified [key] does not exist or
     * is not a [Short] ([Number]), this will return null.
     *
     * @param key The key to get the value from.
     * @return The value with the specified [key] as a [Short] or
     * null if the value with the specified [key] does not exist or is not a [Short] ([Number]).
     */
    fun getShort(key: String) = getPrimitive(key)?.numberValue()?.toShort()

    /**
     * Gets a [Short] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [Short].
     *
     * If the value with the specified [key] does not exist or
     * is not a [Short] ([Number]), this will return the [default] value.
     *
     * @param key The key to get the value from.
     * @param default The default value to return if no value with the specified [key] exists.
     * @return The value with the specified [key] as a [Short] or
     * the [default] value if the value with the specified [key] does not exist or is not a [Short] ([Number]).
     */
    fun getShort(key: String, default: Short) = getShort(key) ?: default

    /**
     * Gets an [Int] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as an [Int].
     *
     * If the value with the specified [key] does not exist or
     * is not a [Int] ([Number]), this will return null.
     *
     * @param key The key to get the value from.
     * @return The value with the specified [key] as an [Int] or
     * null if the value with the specified [key] does not exist or is not a [Int] ([Number]).
     */
    fun getInt(key: String) = getPrimitive(key)?.numberValue()?.toInt()

    /**
     * Gets an [Int] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as an [Int].
     *
     * If the value with the specified [key] does not exist or
     * is not a [Int] ([Number]), this will return the [default] value.
     *
     * @param key The key to get the value from.
     * @param default The default value to return if no value with the specified [key] exists.
     * @return The value with the specified [key] as an [Int] or
     * the [default] value if the value with the specified [key] does not exist or is not a [Int] ([Number]).
     */
    fun getInt(key: String, default: Int) = getInt(key) ?: default

    /**
     * Gets a [Long] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [Long].
     *
     * If the value with the specified [key] does not exist or
     * is not a [Long] ([Number]), this will return null.
     *
     * @param key The key to get the value from.
     * @return The value with the specified [key] as a [Long] or
     * null if the value with the specified [key] does not exist or is not a [Long] ([Number]).
     */
    fun getLong(key: String) = getPrimitive(key)?.numberValue()?.toLong()

    /**
     * Gets a [Long] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [Long].
     *
     * If the value with the specified [key] does not exist or
     * is not a [Long] ([Number]), this will return the [default] value.
     *
     * @param key The key to get the value from.
     * @param default The default value to return if no value with the specified [key] exists.
     * @return The value with the specified [key] as a [Long] or
     * the [default] value if the value with the specified [key] does not exist or is not a [Long] ([Number]).
     */
    fun getLong(key: String, default: Long) = getLong(key) ?: default

    /**
     * Gets a [Float] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [Float].
     *
     * If the value with the specified [key] does not exist or
     * is not a [Float] ([Number]), this will return null.
     *
     * @param key The key to get the value from.
     * @return The value with the specified [key] as a [Float] or
     * null if the value with the specified [key] does not exist or is not a [Float] ([Number]).
     */
    fun getFloat(key: String) = getPrimitive(key)?.numberValue()?.toFloat()

    /**
     * Gets a [Float] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [Float].
     *
     * If the value with the specified [key] does not exist or
     * is not a [Float] ([Number]), this will return the [default] value.
     *
     * @param key The key to get the value from.
     * @param default The default value to return if no value with the specified [key] exists.
     * @return The value with the specified [key] as a [Float] or
     * the [default] value if the value with the specified [key] does not exist or is not a [Float] ([Number]).
     */
    fun getFloat(key: String, default: Float) = getFloat(key) ?: default

    /**
     * Gets a [Double] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [Double].
     *
     * If the value with the specified [key] does not exist or
     * is not a [Double] ([Number]), this will return null.
     *
     * @param key The key to get the value from.
     * @return The value with the specified [key] as a [Double]
     * or null if the value with the specified [key] does not exist or is not a [Double] ([Number]).
     */
    fun getDouble(key: String) = getPrimitive(key)?.numberValue()?.toDouble()

    /**
     * Gets a [Double] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [Double].
     *
     * If the value with the specified [key] does not exist or
     * is not a [Double] ([Number]), this will return the [default] value.
     *
     * @param key The key to get the value from.
     * @param default The default value to return if no value with the specified [key] exists.
     * @return The value with the specified [key] as a [Double] or
     * the [default] value if the value with the specified [key] does not exist or is not a [Double] ([Number]).
     */
    fun getDouble(key: String, default: Double) = getDouble(key) ?: default

    /**
     * Gets a [Boolean] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [Boolean].
     *
     * If the value with the specified [key] does not exist or
     * is not a [Boolean]  this will return null.
     *
     * @param key The key to get the value from.
     * @return The value with the specified [key] as a [Boolean] or
     * null if the value with the specified [key] does not exist or is not a [Boolean].
     */
    fun getBoolean(key: String) = getPrimitive(key)?.booleanValue()

    /**
     * Gets a [Boolean] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [Boolean].
     *
     * If the value with the specified [key] does not exist or
     * is not a [Boolean]  this will return the [default] value.
     *
     * @param key The key to get the value from.
     * @param default The default value to return if no value with the specified [key] exists.
     * @return The value with the specified [key] as a [Boolean] or
     * the [default] value if the value with the specified [key] does not exist or is not a [Boolean].
     */
    fun getBoolean(key: String, default: Boolean) = getBoolean(key) ?: default

    /**
     * Gets a [Char] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [Char].
     *
     * If the value with the specified [key] does not exist or
     * is not a [Char] ([String]) with a length of 1, this will return null.
     *
     * @param key The key to get the value from.
     * @return The value with the specified [key] as a [Char] or
     * null if the value with the specified [key] does not exist or is not a [Char] ([String]) with a length of 1.
     */
    fun getChar(key: String) = getPrimitive(key)?.asText()?.singleOrNull()

    /**
     * Gets a [Char] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [Char].
     *
     * If the value with the specified [key] does not exist or
     * is not a [Char] ([String]) with a length of 1, this will return the [default] value.
     *
     * @param key The key to get the value from.
     * @param default The default value to return if no value with the specified [key] exists.
     * @return The value with the specified [key] as a [Char] or
     * the [default] value if the value with the specified [key] does not exist or is not a [Char] ([String]) with a length of 1.
     */
    fun getChar(key: String, default: Char) = getChar(key) ?: default

    /**
     * Gets a [String] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [String].
     *
     * If the value with the specified [key] does not exist or
     * is not a [String], this will return null.
     *
     * @param key The key to get the value from.
     * @return The value with the specified [key] as a [String] or
     * null if the value with the specified [key] does not exist or is not a [String].
     */
    fun getString(key: String) = getPrimitive(key)?.asText()

    /**
     * Gets a [String] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [String].
     *
     * If the value with the specified [key] does not exist or
     * is not a [String], this will return the [default] value.
     *
     * @param key The key to get the value from.
     * @param default The default value to return if no value with the specified [key] exists.
     * @return The value with the specified [key] as a [String] or
     * the [default] value if the value with the specified [key] does not exist or is not a [String].
     */
    fun getString(key: String, default: String) = getString(key) ?: default

    /**
     * Gets a [ArrayNode] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as an [ArrayNode].
     *
     * If the value with the specified [key] does not exist or
     * is not an [ArrayNode], this will return null.
     *
     * @param key The key to get the value from.
     * @return The value with the specified [key] as an [ArrayNode] or
     * null if the value with the specified [key] does not exist or is not an [ArrayNode].
     */
    fun getJsonArray(key: String) = get(key) as? ArrayNode

    /**
     * Gets a [ArrayNode] value by its [key] from the [JsonDocument].
     *
     * The value is always returned as an [ArrayNode].
     *
     * If the value with the specified [key] does not exist or
     * is not an [ArrayNode], this will return the [default] value.
     *
     * @param key The key to get the value from.
     * @param default The default value to return if no value with the specified [key] exists.
     * @return The value with the specified [key] as an [ArrayNode] or
     * the [default] value if the value with the specified [key] does not exist or is not an [ArrayNode].
     */
    fun getJsonArray(key: String, default: ArrayNode) = getJsonArray(key) ?: default

    /**
     * Gets an [Array] of [JsonNode]s by its [key] from the [JsonDocument].
     *
     * The value is always returned as an [Array] of [JsonNode]s.
     *
     * If the value with the specified [key] does not exist or
     * is not an [ArrayNode], this will return null.
     *
     * @param key The key to get the value from.
     * @return The value with the specified [key] as an [Array] of [JsonNode]s or
     * null if the value with the specified [key] does not exist or is not an [ArrayNode].
     */
    fun getNodeArray(key: String) = getJsonArray(key)?.elements()?.asSequence()?.toList()

    /**
     * Gets an [Array] of [JsonNode]s by its [key] from the [JsonDocument].
     *
     * The value is always returned as an [Array] of [JsonNode]s.
     *
     * If the value with the specified [key] does not exist or
     * is not an [ArrayNode], this will return the [default] value.
     *
     * @param key The key to get the value from.
     * @param default The default value to return if no value with the specified [key] exists.
     * @return The value with the specified [key] as an [Array] of [JsonNode]s or
     * the [default] value if the value with the specified [key] does not exist or is not an [ArrayNode].
     */
    fun getNodeArray(key: String, default: Array<JsonNode>) = getNodeArray(key) ?: default

    /**
     * Gets an [List] of [JsonNode]s by its [key] from the [JsonDocument].
     *
     * The value is always returned as an [List] of [JsonNode]s.
     *
     * If the value with the specified [key] does not exist or
     * is not an [ArrayNode], this will return null.
     *
     * @param key The key to get the value from.
     * @return The value with the specified [key] as an [List] of [JsonNode]s or
     * null if the value with the specified [key] does not exist or is not an [ArrayNode].
     */
    fun getNodeList(key: String) = getNodeArray(key)?.toList()

    /**
     * Gets an [List] of [JsonNode]s by its [key] from the [JsonDocument].
     *
     * The value is always returned as an [List] of [JsonNode]s.
     *
     * If the value with the specified [key] does not exist or
     * is not an [ArrayNode], this will return the [default] value.
     *
     * @param key The key to get the value from.
     * @param default The default value to return if no value with the specified [key] exists.
     * @return The value with the specified [key] as an [List] of [JsonNode]s or
     * the [default] value if the value with the specified [key] does not exist or is not an [ArrayNode].
     */
    fun getNodeList(key: String, default: List<JsonNode>) = getNodeList(key) ?: default

    /**
     * Gets an [Array] of [T]s by its [key] from the [JsonDocument].
     *
     * The value is always returned as an [Array] of [T]s.
     *
     * The mapping is done by the [mapper] of the [JsonDocument] and uses [T] to determine the type.
     *
     * If the value with the specified [key] does not exist or
     * is not an [ArrayNode], this will return null.
     *
     * If any of the values in the [ArrayNode] cannot be mapped to [T], the value will be null.
     *
     * @param T The type of the [Array] to return.
     * @param key The key to get the value from.
     * @return The value with the specified [key] as an [Array] of [T]s or null if no value with the specified [key] exists.
     */
    inline fun <reified T> getArray(key: String) = getList<T>(key)?.toTypedArray()

    /**
     * Gets an [Array] of [T]s by its [key] from the [JsonDocument].
     *
     * The value is always returned as an [Array] of [T]s.
     *
     * The mapping is done by the [mapper] of the [JsonDocument] and uses [T] to determine the type.
     *
     * If the value with the specified [key] does not exist or
     * is not an [ArrayNode], this will return the [default] value.
     *
     * If any of the values in the [ArrayNode] cannot be mapped to [T], the value will be null.
     *
     * @param T The type of the [Array] to return.
     * @param key The key to get the value from.
     * @param default The default value to return if no value with the specified [key] exists.
     * @return The value with the specified [key] as an [Array] of [T]s or
     */
    inline fun <reified T> getArray(key: String, default: Array<T>) = getArray<T>(key) ?: default

    /**
     * Gets an [List] of [T]s by its [key] from the [JsonDocument].
     *
     * The value is always returned as an [List] of [T]s.
     *
     * The mapping is done by the [mapper] of the [JsonDocument] and uses [T] to determine the type.
     *
     * If the value with the specified [key] does not exist or
     * is not an [ArrayNode], this will return null.
     *
     * If any of the values in the [ArrayNode] cannot be mapped to [T], the value will be null.
     *
     * @param T The type of the [List] to return.
     * @param key The key to get the value from.
     * @return The value with the specified [key] as an [List] of [T]s or
     * null if no value with the specified [key] exists.
     */
    inline fun <reified T> getList(key: String) = getNodeList(key)?.mapCatching { mapper.convertValue<T>(it) }

    /**
     * Gets an [List] of [T]s by its [key] from the [JsonDocument].
     *
     * The value is always returned as an [List] of [T]s.
     *
     * The mapping is done by the [mapper] of the [JsonDocument] and uses [T] to determine the type.
     *
     * If the value with the specified [key] does not exist or
     * is not an [ArrayNode], this will return the [default] value.
     *
     * If any of the values in the [ArrayNode] cannot be mapped to [T], the value will be null.
     *
     * @param T The type of the [List] to return.
     * @param key The key to get the value from.
     * @param default The default value to return if no value with the specified [key] exists.
     * @return The value with the specified [key] as an [List] of [T]s or
     * the [default] value if no value with the specified [key] exists.
     */
    inline fun <reified T> getList(key: String, default: List<T>) = getList<T>(key) ?: default

    /**
     * Gets an [ObjectNode] by its [key] from the [JsonDocument].
     *
     * The value is always returned as an [ObjectNode].
     *
     * If the value with the specified [key] does not exist or
     * is not an [ObjectNode], this will return null.
     *
     * @param key The key to get the value from.
     * @return The value with the specified [key] as an [ObjectNode] or
     * null if no value with the specified [key] exists.
     */
    fun getJsonObject(key: String) = get(key) as? ObjectNode

    /**
     * Gets an [ObjectNode] by its [key] from the [JsonDocument].
     *
     * The value is always returned as an [ObjectNode].
     *
     * If the value with the specified [key] does not exist or
     * is not an [ObjectNode], this will return the [default] value.
     *
     * @param key The key to get the value from.
     * @param default The default value to return if no value with the specified [key] exists.
     * @return The value with the specified [key] as an [ObjectNode] or
     * the [default] value if no value with the specified [key] exists.
     */
    fun getJsonObject(key: String, default: ObjectNode) = getJsonObject(key) ?: default

    /**
     * Get a [Map] of [String]s to [JsonNode]s by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [Map] of [String]s to [JsonNode]s.
     *
     * If the value with the specified [key] does not exist or
     *
     * @param key The key to get the value from.
     * @return The value with the specified [key] as a [Map] of [String]s to [JsonNode]s or
     * null if no value with the specified [key] exists.
     */
    fun getNodeMap(key: String) = getJsonObject(key)?.fields()?.asSequence()?.associate { it.key to it.value }

    /**
     * Get a [Map] of [String]s to [JsonNode]s by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [Map] of [String]s to [JsonNode]s.
     *
     * If the value with the specified [key] does not exist or
     *
     * @param key The key to get the value from.
     * @param default The default value to return if no value with the specified [key] exists.
     * @return The value with the specified [key] as a [Map] of [String]s to [JsonNode]s or
     * the [default] value if no value with the specified [key] exists.
     */
    fun getNodeMap(key: String, default: Map<String, JsonNode>) = getNodeMap(key) ?: default

    /**
     * Gets a [Map] of [String]s to [T]s by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [Map] of [String]s to [T]s.
     *
     * The mapping is done by the [mapper] of the [JsonDocument] and uses [T] to determine the type.
     *
     * If the value with the specified [key] does not exist or
     * is not an [ObjectNode], this will return null.
     *
     * If any of the values in the [ObjectNode] cannot be mapped to [T], the value will be null.
     *
     * @param T The type of the [Map] to return.
     * @param key The key to get the value from.
     * @return The value with the specified [key] as a [Map] of [String]s to [T]s or
     * null if no value with the specified [key] exists.
     */
    inline fun <reified T> getMap(key: String) = getNodeMap(key)?.mapValuesCatching { mapper.convertValue<T>(it.value) }

    /**
     * Gets a [Map] of [String]s to [T]s by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [Map] of [String]s to [T]s.
     *
     * The mapping is done by the [mapper] of the [JsonDocument] and uses [T] to determine the type.
     *
     * If the value with the specified [key] does not exist or
     * is not an [ObjectNode], this will return the [default] value.
     *
     * If any of the values in the [ObjectNode] cannot be mapped to [T], the value will be null.
     *
     * @param T The type of the [Map] to return.
     * @param key The key to get the value from.
     * @param default The default value to return if no value with the specified [key] exists.
     * @return The value with the specified [key] as a [Map] of [String]s to [T]s or
     * the [default] value if no value with the specified [key] exists.
     */
    inline fun <reified T> getMap(key: String, default: Map<String, T>) = getMap<T>(key) ?: default

    /**
     * Gets a [JsonDocument] by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [JsonDocument].
     *
     * If the value with the specified [key] does not exist or
     * is not a [ObjectNode], this will return null.
     *
     * The origin of the returned [JsonDocument] will be the [JsonDocument] this method was called on.
     *
     * @param key The key to get the value from.
     * @return The value with the specified [key] as a [JsonDocument] or
     * null if no value with the specified [key] exists.
     */
    fun getDocument(key: String) = getJsonObject(key)
        ?.let { fromNode(it) }
        ?.also {
            it.origin = this
            it.originKey = key
        }

    /**
     * Gets a [JsonDocument] by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [JsonDocument].
     *
     * If the value with the specified [key] does not exist or
     *
     * The origin of the returned [JsonDocument] will be the [JsonDocument] this method was called on.
     *
     * @param key The key to get the value from.
     * @param default The default value to return if no value with the specified [key] exists.
     * @return The value with the specified [key] as a [JsonDocument] or
     */
    fun getDocument(key: String, default: JsonDocument) = getDocument(key) ?: default.also {
        it.origin = this
        it.originKey = key
    }

    /**
     * Gets a [T] by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [T].
     *
     * The mapping is done by the [mapper] of the [JsonDocument] and uses [kClass] to determine the type.
     *
     * If the value with the specified [key] does not exist or
     * can not be mapped to [T], this will return null.
     *
     * @param T The type of the value to return.
     * @param key The key to get the value from.
     * @param kClass The [KClass] of the type to return.
     * @return The value with the specified [key] as a [T] or
     * null if no value with the specified [key] exists.
     */
    fun <T : Any> getTyped(key: String, kClass: KClass<T>) = get(key)
        ?.letCatching { mapper.convertValue(it, kClass.java) }
        ?.getOrNull()

    /**
     * Gets a [T] by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [T].
     *
     * The mapping is done by the [mapper] of the [JsonDocument] and uses [kClass] to determine the type.
     *
     * If the value with the specified [key] does not exist or
     * can not be mapped to [T], this will return the [default] value.
     *
     * @param T The type of the value to return.
     * @param key The key to get the value from.
     * @param kClass The [KClass] of the type to return.
     * @param default The default value to return if no value with the specified [key] exists.
     * @return The value with the specified [key] as a [T] or
     * the [default] value if no value with the specified [key] exists.
     */
    fun <T : Any> getTyped(key: String, kClass: KClass<T>, default: T) = getTyped(key, kClass) ?: default


    /**
     * Gets a [T] by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [T].
     *
     * The mapping is done by the [mapper] of the [JsonDocument] and uses [T] to determine the type.
     *
     * If the value with the specified [key] does not exist or
     * can not be mapped to [T], this will return null.
     *
     * @param T The type of the value to return.
     * @param key The key to get the value from.
     * @return The value with the specified [key] as a [T] or
     * null if no value with the specified [key] exists.
     */
    inline fun <reified T : Any> getTyped(key: String) = getTyped(key, T::class)

    /**
     * Gets a [T] by its [key] from the [JsonDocument].
     *
     * The value is always returned as a [T].
     *
     * The mapping is done by the [mapper] of the [JsonDocument] and uses [T] to determine the type.
     *
     * If the value with the specified [key] does not exist or
     * can not be mapped to [T], this will return the [default] value.
     *
     * @param T The type of the value to return.
     * @param key The key to get the value from.
     * @param default The default value to return if no value with the specified [key] exists.
     * @return The value with the specified [key] as a [T] or
     * the [default] value if no value with the specified [key] exists.
     */
    inline fun <reified T : Any> getTyped(key: String, default: T) = getTyped<T>(key) ?: default

    /**
     * Sets a value with the specified [key] to the [JsonDocument].
     *
     * The value is always a [JsonNode].
     *
     * If the [JsonDocument] already contains a value with the specified [key], it will be replaced.
     *
     * @param key The key to set the value to.
     * @param value The value to set.
     */
    operator fun set(key: String, value: JsonNode?) = apply {
        data[key] = value ?: NullNode.instance
        notifyListeners()
    }

    /**
     * Sets a value with the specified [key] to the [JsonDocument].
     *
     * The value can be of any type and will be mapped with the Jackson [ObjectMapper].
     *
     * If the [JsonDocument] already contains a value with the specified [key], it will be replaced.
     *
     * @param key The key to set the value to.
     * @param value The value to set.
     */
    operator fun set(key: String, value: Any?) = apply {
        data[key] = mapper.valueToTree(value)
        notifyListeners()
    }

    /**
     * Clears the [JsonDocument].
     *
     * This will remove all values from the [JsonDocument].
     *
     * @return The [JsonDocument] itself.
     */
    fun clear() = apply {
        data.clear()
        notifyListeners()
    }

    /**
     * Removes a value with the specified [key] from the [JsonDocument].
     *
     * If the [JsonDocument] does not contain a value with the specified [key], this will do nothing.
     *
     * @param key The key to remove the value from.
     * @return The [JsonDocument] itself.
     */
    fun remove(key: String) = apply {
        data.remove(key)
        notifyListeners()
    }

    /**
     * Converts the [JsonDocument] to a string.
     *
     * The [JsonDocument] will be converted to a json string.
     *
     * @return The [JsonDocument] as a json string.
     */
    override fun toString() = mapper.writeValueAsString(data)

    /**
     * Converts the [JsonDocument] to a pretty string.
     *
     * The [JsonDocument] will be converted to a json string with indentation.
     *
     * @return The [JsonDocument] as a pretty json string.
     */
    fun toPrettyString() = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data)

    companion object {

        /**
         * The [ObjectMapper] used in the [JsonDocument]s.
         */
        val mapper by lazy {
            ObjectMapper().registerModules(
                KotlinModule.Builder()
                    .withReflectionCacheSize(512)
                    .configure(KotlinFeature.NullToEmptyCollection, false)
                    .configure(KotlinFeature.NullToEmptyMap, false)
                    .configure(KotlinFeature.NullIsSameAsDefault, false)
                    .configure(KotlinFeature.SingletonSupport, false)
                    .configure(KotlinFeature.StrictNullChecks, false)
                    .build(),
                JavaTimeModule(),
                JsonDocumentModule,
            ).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        }

        /**
         * Creates a new empty [JsonDocument].
         *
         * @return The created [JsonDocument].
         */
        fun empty() = JsonDocument(mutableMapOf())

        /**
         * Creates a new [JsonDocument] from a [Map].
         *
         * @param map The [Map] to create the [JsonDocument] from.
         * @return The created [JsonDocument].
         */
        fun fromNodeMap(map: Map<String, JsonNode?>) = JsonDocument(map
            .filter { it.value != null }
            .mapValues { it.value!! }
            .toMutableMap())

        /**
         * Cre ates a new [JsonDocument] from a [Map] that contains any object as values.
         *
         * The values will be mapped with the Jackson [ObjectMapper].
         *
         * @param map The [Map] to create the [JsonDocument] from.
         * @return The created [JsonDocument].
         */
        fun fromMap(map: Map<String, Any?>) = fromNodeMap(map.mapValuesCatching { mapper.valueToTree(it.value) })

        /**
         * Creates a new [JsonDocument] from a [JsonNode].
         *
         * Of the [JsonNode] is not a [ObjectNode], this will return an empty [JsonDocument].
         *
         * @param node The [JsonNode] to create the [JsonDocument] from.
         * @return The created [JsonDocument].
         * @see fromMap
         * @see empty
         */
        fun fromNode(node: JsonNode?) =
            if (node is ObjectNode) fromNodeMap(node.fields().asSequence().associate { it.key to it.value })
            else empty()

        /**
         * Creates a new [JsonDocument] from any object.
         *
         * The object will be mapped with the Jackson [ObjectMapper].
         *
         * @param obj The object to create the [JsonDocument] from.
         * @return The created [JsonDocument].
         */
        fun fromObject(obj: Any) = fromNode(mapper.valueToTree(obj))

        /**
         * Creates a new [JsonDocument] from a json string.
         *
         * @param json The json string to create the [JsonDocument] from.
         * @return The created [JsonDocument].
         * @see fromNode
         */
        fun fromJson(json: String) = fromNode(mapper.readTree(json))

        /**
         * Creates a new [JsonDocument] from an input stream.
         *
         * @param stream The input stream to create the [JsonDocument] from.
         * @return The created [JsonDocument].
         * @see fromJson
         */
        fun fromStream(stream: InputStream) = fromJson(stream.bufferedReader().use { it.readText() })

        /**
         * Creates a new [JsonDocument] from a block.
         *
         * @param block The block to create the [JsonDocument] from.
         * @return The created [JsonDocument].
         * @see empty
         */
        fun fromBlock(block: JsonDocument.() -> Unit) = empty().apply(block)

        /**
         * Creates a new [JsonDocument] from a block.
         *
         * @param block The block to create the [JsonDocument] from.
         * @return The created [JsonDocument].
         * @see fromBlock
         */
        operator fun invoke(block: JsonDocument.() -> Unit) = fromBlock(block)

        /**
         * Creates a new empty [JsonDocument] and sets the specified [key] to the value.
         *
         * The value can be of any type and will be mapped with the Jackson [ObjectMapper].
         *
         * @param key The key to set the value to.
         * @param value The value to set.
         * @return The created [JsonDocument].
         */
        fun set(key: String, value: Any?) = empty().set(key, value)

    }

}