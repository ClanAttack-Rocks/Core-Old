package rocks.clanattack.impl.minecraft.listener

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import rocks.clanattack.minecraft.listener.Listen
import rocks.clanattack.minecraft.listener.ListenerPriority
import java.lang.reflect.Method
import kotlin.reflect.KClass

data class ListenerData(
    val event: Class<out Event>,
    val priority: ListenerPriority,
    val executeCanceled: Boolean,
    val includeSubevents: Boolean,
    val method: Method,
    val declaringInstance: Any
) {

    companion object {

        fun create(method: Method, declaringInstance: Any) = method.getDeclaredAnnotation(Listen::class.java).let {
            ListenerData(
                it.event.java,
                it.priority,
                it.executeCanceled,
                it.includeSubevents,
                method,
                declaringInstance
            )
        }

    }


}

fun KClass<out Event>.shouldCall(listenerData: ListenerData) =
    listenerData.event == this.java
            || (listenerData.includeSubevents && listenerData.event.isAssignableFrom(this.java))

fun Event.shouldCall(listenerData: ListenerData) =
    this::class.shouldCall(listenerData)
            && (this !is Cancellable || !this.isCancelled || listenerData.executeCanceled)