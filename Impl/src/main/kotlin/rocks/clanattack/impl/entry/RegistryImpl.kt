package rocks.clanattack.impl.entry

import org.bukkit.plugin.Plugin
import rocks.clanattack.entry.Registry
import rocks.clanattack.impl.util.reflection.create
import kotlin.reflect.KClass
import kotlin.reflect.cast

lateinit var registryImpl: RegistryImpl
    private set

class RegistryImpl(plugin: Plugin) : Registry {

    val _instances = mutableListOf<Any>()

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
            ?.let { klass.cast(it) }
    }

    override fun <T : Any> create(klass: KClass<T>): T {
        if (_instances.any { it::class == klass })
            throw IllegalArgumentException("The class ${klass.simpleName} is already registered.")

        val instance = klass.create()
        _instances.add(instance)

        return instance
    }

    override fun add(instance: Any) {
        _instances.add(instance)
    }

}