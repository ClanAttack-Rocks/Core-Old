package rocks.clanattack.impl.entry.service

import rocks.clanattack.java.AnnotationScanner
import rocks.clanattack.java.ServiceHelper
import rocks.clanattack.entry.find
import rocks.clanattack.entry.plugin.Loader
import rocks.clanattack.entry.registry
import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.Service
import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.util.extention.invocationCause
import rocks.clanattack.util.log.Logger
import kotlin.reflect.KClass

data class ServiceInformation(
    val implementation: ServiceImplementation,
    val annotation: Register
)

object ServiceHandler {

    private val services = mutableMapOf<KClass<out Service>, ServiceInformation>()
    private var initialRegister = false

    init {
        find<Loader>().onRegister { classLoader, basePackage ->
            if (initialRegister) {
                registerServices(classLoader, basePackage)
                enableServices()
            }
        }
    }

    fun registerServices() {
        find<Logger>().info("Registering services...")
        val amount = registerServices(AnnotationScanner.getAnnotatedClasses(Register::class.java))
        find<Logger>().info("Registered $amount services.")

        initialRegister = true
    }

    private fun registerServices(classLoader: ClassLoader, basePackage: String) {
        find<Logger>().info("Registering services from $basePackage...")
        val amount =
            registerServices(AnnotationScanner.getAnnotatedClasses(Register::class.java, classLoader, basePackage))
        find<Logger>().info("Registered $amount services from $basePackage.")
    }

    private fun registerServices(annotated: List<Class<*>>) = annotated
        .filter {
            val annotation = it.getDeclaredAnnotation(Register::class.java) ?: return@filter false
            if (!ServiceImplementation::class.java.isAssignableFrom(it)) {
                find<Logger>().error("The service ${it.name} doesn't implement ServiceImplementation")
                return@filter false
            }

            val definition = annotation.definition
            if (!definition.java.isAssignableFrom(it)) {
                find<Logger>().error("The service ${it.name} doesn't implement its definition (${it.name})")
                return@filter false
            }

            val instance = try {
                registry.create(it.kotlin)
            } catch (e: IllegalArgumentException) {
                find<Logger>().error("The service ${it.name} couldn't be registered", e)
                return@filter false
            }

            find<Logger>().info("Registered service ${it.simpleName} (in ${it.packageName})")
            services[definition] = ServiceInformation(instance as ServiceImplementation, annotation)
            true
        }
        .count()

    fun enableServices() {
        find<Logger>().info("Enabling services...")

        val untouchedServices = services.filter { (_, info) -> !info.implementation.enabled }.toMutableMap()

        var fails = 0
        var enabled = 0
        val modified = mutableListOf<KClass<out Service>>()
        while (untouchedServices.isNotEmpty()) {
            if (fails >= 3) {
                find<Logger>().error("The services")
                untouchedServices.forEach { (definition, _) ->
                    find<Logger>().error("- ${definition.qualifiedName}")
                }
                find<Logger>().error("could not be enabled.")
                find<Logger>().error("Do they depend each other, or depend on not registered services?")
                find<Logger>().error("Did the services they depend on fail to enable?")

                find<Logger>().info("On the next try to enable any services, these services will be tried again.")
                break
            }

            untouchedServices
                .filter { (_, info) -> info.annotation.depends.all { services[it]?.implementation?.enabled == true } }
                .forEach { (definition, info) ->
                    try {
                        find<Logger>().info("Enabling ${definition.simpleName} (in ${definition.java.packageName})...")

                        info.implementation.enable()
                        ServiceHelper.setEnabled(info.implementation, true)
                        enabled++

                        find<Logger>().info("Enabled ${definition.simpleName}.")
                    } catch (e: Exception) {
                        find<Logger>().error(
                            "An error occurred while enabling ${definition.simpleName}.",
                            e.invocationCause
                        )
                    }

                    modified.add(definition)
                }

            if (modified.isEmpty()) {
                find<Logger>().warn("No services could be enabled (${untouchedServices.size} left, retrying ${3 - fails} more times).")
                fails++
            } else fails = 0

            modified.forEach { untouchedServices.remove(it) }
            modified.clear()
        }

        find<Logger>().info("Enabled $enabled services.")
    }

    fun disableServices() {
        find<Logger>().info("Disabling services...")

        val untouchedServices = services.filter { (_, info) -> info.implementation.enabled }.toMutableMap()

        var fails = 0
        var disabled = 0
        val modified = mutableListOf<KClass<out Service>>()
        while (untouchedServices.isNotEmpty()) {
            if (fails >= 3) {
                find<Logger>().error("The services")
                untouchedServices.forEach { (definition, _) ->
                    find<Logger>().error("- ${definition.qualifiedName}")
                }
                find<Logger>().error("could not be disabled.")
                find<Logger>().error("Do they depend each other, or depend on not registered services?")
                find<Logger>().error("Did the services they depend on fail to disable?")
                break
            }

            untouchedServices
                .filter { (_, info) ->
                    services.all {
                        it.value.annotation.depends.none { depend ->
                            depend == info.annotation.definition && it.value.implementation.enabled
                        }
                    }
                }
                .forEach { (definition, info) ->
                    try {
                        find<Logger>().info("Disabling ${definition.qualifiedName}...")

                        info.implementation.disable()
                        ServiceHelper.setEnabled(info.implementation, false)
                        disabled++

                        find<Logger>().info("Disabled ${definition.qualifiedName}.")
                    } catch (e: Exception) {
                        find<Logger>().error(
                            "An error occurred while disabling ${definition.qualifiedName}.",
                            e.invocationCause
                        )
                    }

                    modified.add(definition)
                }

            if (modified.isEmpty()) {
                find<Logger>().warn("No services could be disabled (${untouchedServices.size} left, retrying ${3 - fails} more times).")
                fails++
            } else fails = 0

            modified.forEach { untouchedServices.remove(it) }
            modified.clear()
        }

        find<Logger>().info("Disabled $disabled services.")
    }

}