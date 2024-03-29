package rocks.clanattack.impl.entry.point

import rocks.clanattack.java.AnnotationScanner
import rocks.clanattack.java.MethodHelper
import rocks.clanattack.entry.find
import rocks.clanattack.entry.plugin.Loader
import rocks.clanattack.entry.point.EntryPoint
import rocks.clanattack.entry.point.ExitPoint
import rocks.clanattack.entry.registry
import rocks.clanattack.util.extention.invocationCause
import rocks.clanattack.util.log.Logger
import java.lang.reflect.Method

object PointHandler {

    private var initialRegister = false

    init {
        find<Loader>().onRegister { classLoader, basePackage ->
            if (initialRegister) callEntryPoints(classLoader, basePackage)
        }
    }

    fun callEntryPoints() {
        find<Logger>().info("Calling entry points...")
        val amount = callEntryPoints(AnnotationScanner.getAnnotatedMethods(EntryPoint::class.java))
        find<Logger>().info("Called $amount entry points.")

        initialRegister = true
    }

    private fun callEntryPoints(classLoader: ClassLoader, basePackage: String) {
        find<Logger>().info("Calling entry points from $basePackage...")
        val amount =
            callEntryPoints(AnnotationScanner.getAnnotatedMethods(EntryPoint::class.java, classLoader, basePackage))
        find<Logger>().info("Called $amount entry points from $basePackage.")
    }

    private fun callEntryPoints(annotated: List<Method>) = annotated.count {
        try {
            MethodHelper.call(it, registry)
            true
        } catch (e: Exception) {
            find<Logger>().error("Couldn't call entry point ${MethodHelper.getFullName(it)}", e.invocationCause)
            false
        }
    }

    fun callExitPoints() {
        find<Logger>().info("Calling exit points...")

        val amount = AnnotationScanner.getAnnotatedMethods(ExitPoint::class.java).count {
            try {
                MethodHelper.call(it, registry)
                true
            } catch (e: Exception) {
                find<Logger>().error("Couldn't call exit point ${MethodHelper.getFullName(it)}", e.invocationCause)
                false
            }
        }

        find<Logger>().info("Called $amount exit points.")
    }

}
