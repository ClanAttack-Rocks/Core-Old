package rocks.clanattack.util.minecraft.listener

import org.bukkit.event.Event
import kotlin.reflect.KClass

import rocks.clanattack.entry.Registry

/**
 * Listens to a specific bukkit [Event].
 *
 * For a method to be called when the specific [Event] is fired, it has to be annotated with this annotation.
 *
 * The method must be in a class eligible for an automatic [Registry.create] call **and** return nothing.
 *
 * The method can either take
 * - no parameters,
 * - one parameter of the type of the event,
 * - one parameter of the type [Registry] or
 * - two parameters, the first one being of the type of the event and the second one being a [Registry].
 *
 * The method can be private, but must be accessible.
 *
 * Listeners with a higher priority will be called first.
 *
 * [HIGHEST][ListenerPriority.HIGHEST] ->
 * [HIGH][ListenerPriority.HIGH] ->
 * [NORMAL][ListenerPriority.NORMAL] ->
 * [LOW][ListenerPriority.LOW] ->
 * [LOWEST][ListenerPriority.LOWEST]
 *
 * If [executeCanceled] is true, the listener will also be called when the event is canceled.
 *
 * If [includeSubevents] is true, the listener will also be called when a subevent of the specified event is fired.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Listen(
    val event: KClass<out Event>,
    val priority: ListenerPriority = ListenerPriority.NORMAL,
    val executeCanceled: Boolean = false,
    val includeSubevents: Boolean = false,
)