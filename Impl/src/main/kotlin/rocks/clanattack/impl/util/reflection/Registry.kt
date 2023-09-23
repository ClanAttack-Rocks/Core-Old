package rocks.clanattack.impl.util.reflection

import rocks.clanattack.entry.Registry
import kotlin.reflect.jvm.javaField
import rocks.clanattack.entry.registry as tlRegistry

fun Registry.setAsTopLevel() {
    ::tlRegistry.javaField
        ?.also { it.isAccessible = true }
        ?.also { it.set(null, this) }
        ?.also { it.isAccessible = false }
}