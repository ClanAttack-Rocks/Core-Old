package rocks.clanattack.impl.entry.point

import rocks.clanattack.entry.Registry
import rocks.clanattack.entry.find
import rocks.clanattack.entry.plugin.Loader
import rocks.clanattack.entry.point.EntryPoint
import rocks.clanattack.entry.point.ExitPoint
import rocks.clanattack.entry.registry
import rocks.clanattack.entry.service.Register
import rocks.clanattack.impl.util.annotation.AnnotationScanner
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
        val amount = callEntryPoints(AnnotationScanner.findMethods(EntryPoint::class))
        find<Logger>().info("Called $amount entry points.")

        initialRegister = true
    }

    private fun callEntryPoints(classLoader: ClassLoader, basePackage: String) {
        find<Logger>().info("Calling entry points from $basePackage...")
        val amount = callEntryPoints(AnnotationScanner.findMethods(Register::class, classLoader, basePackage))
        find<Logger>().info("Called $amount entry points from $basePackage.")
    }

    private fun callEntryPoints(annotated: List<Method>) = annotated.filter {
        if (it.returnType.kotlin != Void::class) {
            find<Logger>().error("The method ${getName(it)} does return ${it.returnType.kotlin.simpleName}, should be Unit/Void.")
            return@filter false
        }

        val declaringClass = it.declaringClass
        val instance = try {
            registry.getOrCreate(declaringClass.kotlin)
        } catch (_: Exception) {
            find<Logger>().error(
                "Could not create instance of ${declaringClass.name} " +
                        "(required for entry point ${getName(it)})"
            )
            return@filter false
        }

        if (it.parameters.isEmpty()) {
            try {
                it.invoke(instance)
            } catch (e: Exception) {
                find<Logger>().error("Could not call entry point ${getName(it)}", e.invocationCause)
                return@filter false
            }
        } else if (it.parameters.size == 1 && it.parameters[0].type.kotlin == Registry::class) {
            try {
                it.invoke(instance, registry)
            } catch (e: Exception) {
                find<Logger>().error("Could not call entry point ${getName(it)}", e.invocationCause)
                return@filter false
            }
        } else {
            find<Logger>().error(
                "The method ${getName(it)} must either have no parameters or " +
                        "a single Registry parameter."
            )
            return@filter false
        }

        true
    }.count()

    fun callExitPoints() {
        find<Logger>().info("Calling exit points...")

        val amount = AnnotationScanner.findMethods(ExitPoint::class)
            .filter {
                if (it.returnType.kotlin != Void::class) {
                    find<Logger>().error("The method ${getName(it)} does return ${it.returnType.kotlin.simpleName}, should be Unit/Void.")
                    return@filter false
                }

                val declaringClass = it.declaringClass
                val instance = try {
                    registry.getOrCreate(declaringClass.kotlin)
                } catch (_: Exception) {
                    find<Logger>().error(
                        "Could not create instance of ${declaringClass.name} " +
                                "(required for exit point ${getName(it)})"
                    )
                    return@filter false
                }

                if (it.parameters.isEmpty()) {
                    try {
                        it.invoke(instance)
                    } catch (e: Exception) {
                        find<Logger>().error("Could not call exit point ${getName(it)}", e.invocationCause)
                        return@filter false
                    }
                } else if (it.parameters.size == 1 && it.parameters[0].type.kotlin == Registry::class) {
                    try {
                        it.invoke(instance, registry)
                    } catch (e: Exception) {
                        find<Logger>().error("Could not call exit point ${getName(it)}", e.invocationCause)
                        return@filter false
                    }
                } else {
                    find<Logger>().error(
                        "The method ${getName(it)} must either have no parameters or " +
                                "a single Registry parameter."
                    )
                    return@filter false
                }

                true
            }.count()

        find<Logger>().info("Called $amount exit points.")
    }

    private fun getName(method: Method) = "${method.declaringClass.name}#${method.name}"
}
