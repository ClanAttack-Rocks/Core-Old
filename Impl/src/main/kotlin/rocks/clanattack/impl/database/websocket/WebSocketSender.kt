package rocks.clanattack.impl.database.websocket

import rocks.clanattack.database.ChangeType
import rocks.clanattack.entry.find
import rocks.clanattack.task.TaskService
import rocks.clanattack.util.extention.unit
import rocks.clanattack.util.json.JsonDocument
import rocks.clanattack.util.json.get
import rocks.clanattack.util.json.json
import rocks.clanattack.util.promise.Promise
import rocks.clanattack.util.promise.PromiseService
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.time.Duration.Companion.seconds

class WebSocketSender(private val websocket: Websocket) {

    @Volatile
    private var id = 1

    private val liveListeners = mutableListOf<LiveListener<*>>()

    init {
        websocket.addListener {
            if ("id" in it) return@addListener false
            val resultDocument = it.get<JsonDocument>("result") ?: return@addListener false
            val liveId = resultDocument.get<UUID>("id") ?: return@addListener false

            val listeners = liveListeners.filter { listener -> listener.id == liveId }
            if (listeners.isEmpty()) return@addListener false

            val action = resultDocument.get<ChangeType>("action") ?: return@addListener false

            listeners.forEach { listener ->
                val thing =
                    if (action == ChangeType.DELETE) resultDocument.get<JsonDocument>("result")?.get<String>("id")
                        ?: return@forEach
                    else resultDocument.get<String>("result") ?: return@forEach

                try {
                    val result = if (action == ChangeType.DELETE) null else resultDocument["result", listener.type]
                    listener(action, thing, result)
                } catch (e: Exception) {
                    listener(action, thing, null)
                }
            }

            false
        }
    }

    fun <T : Any> send(method: String, type: KClass<T>, vararg params: Any): Promise<T> {
        val promise = find<PromiseService>().create<T>()
        val currentId = id++

        find<TaskService>().execute(detached = true) {
            websocket.addListener {
                if (it.get<Int>("id") == currentId) {
                    try {
                        if (type == Unit::class) {
                            @Suppress("UNCHECKED_CAST")
                            promise.fulfill(Unit as T)
                        } else {
                            val result = it["result", type]!!
                            promise.fulfill(result)
                        }
                    } catch (e: Exception) {
                        promise.reject(e)
                    }

                    true
                } else false
            }

            try {
                websocket.send(json {
                    this["id"] = currentId
                    this["method"] = method
                    this["params"] = params
                })
            } catch (e: Exception) {
                promise.reject(e)
            }
        }

        return promise.timeout(10.seconds)
    }

    inline fun <reified T : Any> send(method: String, vararg params: Any) = send(method, T::class, *params)

    fun <T : Any> addLiveListener(uuid: UUID, type: KClass<T>, callback: (ChangeType, String, T?) -> Unit) =
        unit { liveListeners.add(LiveListener(uuid, type, callback)) }

    inline fun <reified T : Any> addLiveListener(uuid: UUID, noinline callback: (ChangeType, String, T?) -> Unit) =
        addLiveListener(uuid, T::class, callback)

    fun removeLiveListener(uuid: UUID) = unit { liveListeners.removeIf { it.id == uuid } }

    private inner class LiveListener<T : Any>(
        val id: UUID,
        val type: KClass<T>,
        val callback: (ChangeType, String, T?) -> Unit
    ) {

        operator fun invoke(action: ChangeType, thing: String, result: Any?) {
            try {
                val castedResult = type.cast(result)
                callback(action, thing, castedResult)
            } catch (e: Exception) {
                callback(action, thing, null)
            }
        }

    }

}