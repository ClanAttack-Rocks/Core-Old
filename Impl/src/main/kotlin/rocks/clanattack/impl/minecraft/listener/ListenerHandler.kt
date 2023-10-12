package rocks.clanattack.impl.minecraft.listener

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import rocks.clanattack.entry.find
import rocks.clanattack.entry.point.EntryPoint
import rocks.clanattack.entry.registry
import rocks.clanattack.java.AnnotationScanner
import rocks.clanattack.java.MethodHelper
import rocks.clanattack.java.SubTypeScanner
import rocks.clanattack.minecraft.listener.Listen
import rocks.clanattack.task.detached
import rocks.clanattack.util.extention.invocationCause
import rocks.clanattack.util.log.Logger
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

object ListenerHandler {

    private val listeners = mutableMapOf<KClass<out Event>, MutableList<ListenerData>>()
    var loaded = false
        private set

    @EntryPoint
    fun entry() {
        Bukkit.getPluginManager().registerEvents(PlayerLoginListener(), find())

        detached {
            find<Logger>().info("Loading listeners...")
            AnnotationScanner.getAnnotatedMethods(Listen::class.java).forEach {
                val declaringClass = it.declaringClass
                try {
                    registry.getOrCreate(declaringClass.kotlin)
                } catch (e: Exception) {
                    find<Logger>().error(
                        "Could not create instance of ${declaringClass.name} " +
                                "(required for listener ${MethodHelper.getFullName(it)})", e
                    )
                    return@forEach
                }

                val listenerData = ListenerData.create(it)
                listeners.getOrPut(listenerData.event.kotlin) { mutableListOf() }.add(listenerData)
            }

            val sortedListeners =
                listeners.map { (event, data) -> event to data.sortedBy { it.priority }.toMutableList() }.toMap()
            listeners.clear()
            sortedListeners.forEach { (event, data) -> listeners[event] = data }

            find<Logger>().info("Loaded ${listeners.map { it.value.size }.sum()} listeners.")

            find<Logger>().info("Registering events...")
            val listener = object : Listener {}
            val execute = EventExecutor { _, event -> fireEvent(event) }
            var counter = 0

            SubTypeScanner.getSubTypesOf(Event::class.java)
                .asSequence()
                .map { it.asSubclass(Event::class.java) }
                .filter { !Modifier.isAbstract(it.modifiers) }
                .filter { it.methods.any { method -> method.name == "getHandlers" && method.parameterCount == 0 } }
                .filter { shouldRegister(it.kotlin) }
                .distinct()
                .toList()
                .forEach {
                    Bukkit.getPluginManager().registerEvent(
                        it,
                        listener,
                        EventPriority.NORMAL,
                        execute,
                        find()
                    )

                    counter++
                }

            find<Logger>().info("Registered $counter events.")
            loaded = true
        }
    }

    private fun shouldRegister(klass: KClass<out Event>) =
        listeners.any { (_, list) -> list.any { klass.shouldCall(it) } }

    private fun fireEvent(event: Event) {
        val eventClass = event::class

        val toBeCalled = listeners
            .filter { (klass, _) -> klass.java.isAssignableFrom(eventClass.java) }
            .map { (_, list) -> list.filter { eventClass.shouldCall(it) } }
            .flatten()

        for (listenerData in toBeCalled) {
            if (!event.shouldCall(listenerData)) continue
            try {
                MethodHelper.call(listenerData.method, event, registry)
            } catch (e: Exception) {
                find<Logger>().error(
                    "Could not execute listener ${MethodHelper.getFullName(listenerData.method)} " +
                            "for event ${event::class.qualifiedName}",
                    e.invocationCause
                )
            }
        }
    }

}