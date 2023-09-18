package rocks.clanattack.impl.util.minecraft.listener

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.java.JavaPlugin
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder
import rocks.clanattack.entry.Registry
import rocks.clanattack.entry.find
import rocks.clanattack.entry.plugin.Loader
import rocks.clanattack.impl.util.annotation.AnnotationScanner
import rocks.clanattack.util.log.Logger
import rocks.clanattack.util.minecraft.listener.Listen
import rocks.clanattack.util.task.TaskService
import kotlin.reflect.KClass
import kotlin.reflect.full.functions
import kotlin.reflect.full.isSuperclassOf

object ListenerHandler {

    private val listeners = mutableMapOf<KClass<out Event>, MutableList<ListenerData>>()
    var loaded = false
        private set

    fun block() {
        Bukkit.getPluginManager().registerEvents(PlayerLoginListener(), find<JavaPlugin>())
    }

    fun load() {
        find<TaskService>().execute {
            find<Logger>().info("Loading listeners...")
            AnnotationScanner.findMethods(Listen::class).forEach {
                val declaringClass = it.declaringClass
                val instance = try {
                    find<Registry>().getOrCreate(declaringClass.kotlin)
                } catch (_: Exception) {
                    find<Logger>().error(
                        "Could not create instance of ${declaringClass.name} " +
                                "(required for listener ${it.declaringClass.simpleName}#${it.name})"
                    )
                    return@forEach
                }

                val listenerData = ListenerData.create(it, instance)
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

            Reflections(ConfigurationBuilder().addClassLoaders(find<Loader>().classLoaders.keys))
                .getSubTypesOf(Event::class.java)
                .asSequence()
                .map { it.kotlin }
                .filter { !it.isAbstract }
                .filter {
                    it.functions.any { function ->
                        function.parameters.isEmpty() && function.name == "getHandlers"
                    }
                }
                .filter { shouldRegister(it) }
                .distinct()
                .forEach {
                    Bukkit.getPluginManager().registerEvent(
                        it.java,
                        listener,
                        EventPriority.NORMAL,
                        execute,
                        find<JavaPlugin>()
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

        val toBeCalled = this.listeners
            .filter { (klass, _) -> klass.isSuperclassOf(eventClass) }
            .map { (_, list) -> list.filter { eventClass.shouldCall(it) } }
            .flatten()

        for (listenerData in toBeCalled) {
            if (!event.shouldCall(listenerData)) continue
            listenerData.method.invoke(listenerData.declaringInstance, event)
        }
    }

}