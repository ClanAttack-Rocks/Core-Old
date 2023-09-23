package rocks.clanattack.impl.util.reflection

import rocks.clanattack.entry.service.ServiceImplementation
import kotlin.reflect.jvm.javaField

fun ServiceImplementation.setEnabled(enabled: Boolean) {
    ServiceImplementation::enabled.javaField
        ?.also { it.isAccessible = true }
        ?.also { it.set(this, true) }
        ?.also { it.isAccessible = false }
}