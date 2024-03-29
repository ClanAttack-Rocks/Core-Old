package rocks.clanattack.impl.entry

import org.bukkit.plugin.Plugin
import rocks.clanattack.java.ClassHelper
import rocks.clanattack.entry.Registry as Interface
import kotlin.reflect.KClass

lateinit var registryImpl: Registry
    private set

class Registry(plugin: Plugin) : Interface {

    private val _instances = mutableListOf<Any>()

    override val instances: List<Any>
        get() = _instances.toList()

    init {
        plugin.logger.info("Initializing registry...")

        registryImpl = this

        add(this)
        add(plugin)

        plugin.logger.info("Initialized registry.")
    }

    override fun <T : Any> get(klass: KClass<T>): T? {
        return _instances.find { klass.isInstance(it) }
            ?.let { klass.java.cast(it) }
    }

    override fun <T : Any> create(klass: KClass<T>): T {
        if (_instances.any { it::class == klass })
            throw IllegalArgumentException("The class ${klass.simpleName} is already registered.")

        val instance = ClassHelper.createInstance(klass.java)
        _instances.add(instance)

        return instance
    }

    override fun add(instance: Any) {
        _instances.add(instance)
    }

}