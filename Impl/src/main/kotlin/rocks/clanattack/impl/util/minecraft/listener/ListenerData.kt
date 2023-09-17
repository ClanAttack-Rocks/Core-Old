package rocks.clanattack.impl.util.minecraft.listener

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import rocks.clanattack.util.minecraft.listener.*
import java.lang.reflect.Method
import kotlin.reflect.KClass

data class ListenerData(
    val event: Class<out Event>,
    val handlerClass: Class<out Event>,
    val priority: ListenerPriority,
    val executeCanceled: Boolean,
    val includeSubevents: Boolean,
    val method: Method,
    val declaringInstance: Any
) {

    companion object {

        fun create(method: Method, declaringInstance: Any) = ListenerData(
            method.getDeclaredAnnotation(Listen::class.java).event.java,
            method.getDeclaredAnnotation(Listen::class.java).event.java
                .getMethod("getHandlerList").declaringClass.asSubclass(Event::class.java),
            if (method.isAnnotationPresent(Priority::class.java))
                method.getDeclaredAnnotation(Priority::class.java).priority else ListenerPriority.NORMAL,
            method.isAnnotationPresent(ExecuteCanceled::class.java),
            method.isAnnotationPresent(IncludeSubevents::class.java),
            method,
            declaringInstance
        )

    }


}

fun KClass<out Event>.shouldCall(listenerData: ListenerData): Boolean {
    val handlerClass = this.java.getDeclaredMethod("getHandlerList").declaringClass

    if (!listenerData.event.isAssignableFrom(this.java)) return false
    if (listenerData.handlerClass == handlerClass) return true

    return listenerData.includeSubevents
}

fun Event.shouldCall(listenerData: ListenerData): Boolean {
    if (!this::class.shouldCall(listenerData)) return false
    if (this is Cancellable && this.isCancelled && !listenerData.executeCanceled) return false

    return true
}