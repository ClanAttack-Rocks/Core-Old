package rocks.clanattack.impl.entry.point

import rocks.clanattack.entry.Registry
import rocks.clanattack.entry.find
import rocks.clanattack.entry.plugin.Loader
import rocks.clanattack.entry.point.EntryPoint
import rocks.clanattack.entry.point.ExitPoint
import rocks.clanattack.entry.registry
import rocks.clanattack.entry.service.Register
import rocks.clanattack.impl.util.reflection.*
import rocks.clanattack.util.extention.invocationCause
import rocks.clanattack.util.log.Logger
import java.lang.reflect.Method
import kotlin.reflect.KFunction

object PointHandler {

    private var initialRegister = false

    init {
        find<Loader>().onRegister { classLoader, basePackage ->
            if (initialRegister) callEntryPoints(classLoader, basePackage)
        }
    }

    fun callEntryPoints() {
        find<Logger>().info("Calling entry points...")
        val amount = callEntryPoints(EntryPoint::class.annotatedMethods)
        find<Logger>().info("Called $amount entry points.")

        initialRegister = true
    }

    private fun callEntryPoints(classLoader: ClassLoader, basePackage: String) {
        find<Logger>().info("Calling entry points from $basePackage...")
        val amount = callEntryPoints(EntryPoint::class.getAnnotatedMethods(classLoader, basePackage))
        find<Logger>().info("Called $amount entry points from $basePackage.")
    }

    private fun callEntryPoints(annotated: List<KFunction<*>>) = annotated.filter {
        if (it.returnType != Void::class) {
            find<Logger>().error("Entrypoint ${it.qualifiedName} doesn't return Unit/Void.")
            return@filter false
        }

        try {
            it.unitOmitCall(registry)
            true
        } catch (e: Exception) {
            find<Logger>().error("Couldn't call entry point ${it.qualifiedName}", e.invocationCause)
            false
        }
    }.count()

    fun callExitPoints() {
        find<Logger>().info("Calling exit points...")

        val amount =ExitPoint::class
            .annotatedMethods
            .filter {
                if (it.returnType != Void::class) {
                    find<Logger>().error("Exit point ${it.qualifiedName} doesn't return Unit/Void.")
                    return@filter false
                }

                try {
                    it.unitOmitCall(registry)
                    true
                } catch (e: Exception) {
                    find<Logger>().error("Couldn't call exit point ${it.qualifiedName}", e.invocationCause)
                    false
                }
            }.count()

        find<Logger>().info("Called $amount exit points.")
    }

}
