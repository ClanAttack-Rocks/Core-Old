package rocks.clanattack.impl.util.reflection

import rocks.clanattack.entry.Registry
import rocks.clanattack.entry.registry
import kotlin.reflect.KClass
import kotlin.reflect.jvm.isAccessible

fun <T : Any> KClass<out T>.create() : T {
    if (this.objectInstance != null) return this.objectInstance!!

    return this.constructors
        .find {
            it.parameters.isEmpty()
                    || (it.parameters.size == 1 && it.parameters[0].type.classifier == Registry::class)
        }
        ?.let {
            it.isAccessible = true

            if (it.parameters.isEmpty()) it.call()
            else it.call(registry)
        }
        ?: throw IllegalArgumentException(
            "The class ${this.simpleName} does not have a eligible constructor for automatic creation."
        )
}