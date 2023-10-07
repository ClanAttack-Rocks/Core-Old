package rocks.clanattack.impl.util.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import rocks.clanattack.impl.util.json.module.adventure.AdventureModule
import rocks.clanattack.impl.util.json.module.datetime.DateTimeModule
import rocks.clanattack.impl.util.json.module.document.JsonDocumentModule
import rocks.clanattack.impl.util.json.module.duration.DurationModule
import rocks.clanattack.impl.util.json.module.uuid.UUIDModule
import rocks.clanattack.util.extention.alsoIf
import rocks.clanattack.util.extention.unit
import java.io.File
import java.io.OutputStream
import kotlin.reflect.KClass
import rocks.clanattack.util.json.JsonDocument as Interface

class JsonDocument(
    val data: MutableMap<String, JsonNode> = mutableMapOf(),
    override var origin: JsonDocument? = null,
    private var originKey: String? = null,
) : Interface {

    private val listeners = mutableListOf<(Interface) -> Unit>()

    override val keys: List<String>
        get() = data.keys.toList()

    override val size: Int
        get() = data.size

    override fun onChange(listener: (Interface) -> Unit) = unit { listeners.add(listener) }

    override fun removeListener(listener: (Interface) -> Unit) = unit { listeners.remove(listener) }

    private fun notifyChange() = unit { listeners.forEach { it(this) } }

    override fun print(stream: OutputStream, pretty: Boolean) {
        if (pretty) mapper.writerWithDefaultPrettyPrinter().writeValue(stream, data)
        else mapper.writeValue(stream, data)
    }

    override fun write(file: File, pretty: Boolean) {
        if (pretty) mapper.writerWithDefaultPrettyPrinter().writeValue(file, data)
        else mapper.writeValue(file, data)
    }

    override fun clone() = JsonDocument(data.toMutableMap())

    override fun flush() {
        origin?.set(originKey!!, data)
        origin?.flush()
        notifyChange()
    }

    override fun contains(key: String) = key in data

    override fun <T : Any> get(key: String, type: KClass<T>) = data[key]
        ?.let { mapper.convertValue(it, type.java) }
        ?.alsoIf({ it is JsonDocument }) {
            (it as JsonDocument).origin = this
            it.originKey = key
        }

    override fun <T : Any> get(key: String, default: T, type: KClass<T>) = get(key, type) ?: default

    override fun <T : Any> getList(key: String, type: KClass<T>) =
        data[key]
            ?.let {
                if (it is ArrayNode) it
                else throw IllegalArgumentException("The value of the key $key is not a list")
            }
            ?.elements()
            ?.asSequence()
            ?.map { mapper.convertValue(it, type.java) }
            ?.toList()

    override fun <T : Any> getList(key: String, default: List<T>, type: KClass<T>) = getList(key, type) ?: default

    override fun <T : Any> getMap(key: String, type: KClass<T>) =
        data[key]
            ?.let {
                if (it is ObjectNode) it
                else throw IllegalArgumentException("The value of the key $key is not a map")
            }
            ?.fields()
            ?.asSequence()
            ?.map { it.key to mapper.convertValue(it.value, type.java) }
            ?.toMap()

    override fun <T : Any> getMap(key: String, default: Map<String, T>, type: KClass<T>) = getMap(key, type) ?: default

    override fun set(key: String, value: Any?) {
        data[key] = mapper.valueToTree(value)
        notifyChange()
    }

    override fun clear() {
        data.clear()
        notifyChange()
    }

    override fun remove(key: String) {
        data.remove(key)
        notifyChange()
    }

    override fun toString(pretty: Boolean): String =
        if (pretty) mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data)
        else mapper.writeValueAsString(data)

    override fun toString() = toString(false)

    companion object {

        val mapper: ObjectMapper by lazy {
            ObjectMapper().registerModules(
                KotlinModule.Builder()
                    .withReflectionCacheSize(512)
                    .configure(KotlinFeature.NullToEmptyCollection, false)
                    .configure(KotlinFeature.NullToEmptyMap, false)
                    .configure(KotlinFeature.NullIsSameAsDefault, false)
                    .configure(KotlinFeature.SingletonSupport, false)
                    .configure(KotlinFeature.StrictNullChecks, false)
                    .build(),
                AdventureModule,
                DateTimeModule,
                JsonDocumentModule,
                DurationModule,
                UUIDModule,
            ).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        }

    }


}