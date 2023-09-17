package rocks.clanattack.entry.service

import kotlin.reflect.KClass

/**
 * All [Service] implementations must be annotated with this annotation.
 *
 * See [ServiceImplementation] for more information.
 *
 * @property definition The frontend of the service.
 * @property depends The providers this provider depends on.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Register(
    val definition: KClass<out Service>,
    vararg val depends: KClass<out Service> = [],
)
