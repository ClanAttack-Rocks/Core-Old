package rocks.clanattack.impl.entry

import rocks.clanattack.entry.Registry
import kotlin.reflect.KClass
import kotlin.reflect.cast

lateinit var registryImpl: RegistryImpl
    private set

class RegistryImpl : Registry {

    init {
        registryImpl = this

        set(Registry::class, this)
    }

    private val _instances = mutableMapOf<KClass<*>, Any>()

    override val instances: Map<KClass<*>, Any>
        get() = _instances.toMap()

    override fun <T : Any> get(klass: KClass<T>): T? {
        val instance = _instances[klass] ?: return null

        if (!klass.isInstance(instance)) throw IllegalStateException(
            "The instance of ${klass.simpleName} " +
                    "is of type ${instance::class.simpleName}, " +
                    "witch is not a subtype of ${klass.simpleName}."
        )

        return klass.cast(instance)
    }

    override fun <T : Any> create(klass: KClass<T>, register: Boolean): T {
        if (klass in _instances) throw IllegalArgumentException("The class ${klass.simpleName} is already registered.")

        if (klass.objectInstance != null) {
            _instances[klass] = klass.objectInstance!!
            return klass.objectInstance!!
        }

        val instance =
            klass.constructors
                .find { it.parameters.size == 1 && it.parameters[0].type.classifier == Registry::class }
                ?.call(this)
                ?: klass.constructors
                    .find { it.parameters.isEmpty() }
                    ?.call()
                ?: throw IllegalArgumentException(
                    "The class ${klass.simpleName} does not have " +
                            "a no-args constructor or a constructor with a single Registry parameter."
                )

        if (register) _instances[klass] = instance

        return instance
    }

    override fun <T : Any, P : T> set(klass: KClass<T>, instance: P) {
        if (klass in _instances) throw IllegalArgumentException("The class ${klass.simpleName} is already registered.")
        _instances[klass] = instance
    }

    fun setUnsafe(klass: KClass<*>, instance: Any, check: Boolean = true) {
        if (check && klass in _instances) throw IllegalArgumentException("The class ${klass.simpleName} is already registered.")
        if (check && !klass.isInstance(instance)) throw IllegalArgumentException(
            "The instance of ${klass.simpleName} " +
                    "is of type ${instance::class.simpleName}, " +
                    "witch is not a subtype of ${klass.simpleName}."
        )

        _instances[klass] = instance
    }

}