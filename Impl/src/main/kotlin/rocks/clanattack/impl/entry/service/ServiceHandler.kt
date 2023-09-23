package rocks.clanattack.impl.entry.service

import rocks.clanattack.entry.find
import rocks.clanattack.entry.plugin.Loader
import rocks.clanattack.entry.registry
import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.Service
import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.impl.util.reflection.annotatedClasses
import rocks.clanattack.impl.util.reflection.getAnnotatedClasses
import rocks.clanattack.impl.util.reflection.setEnabled
import rocks.clanattack.util.extention.invocationCause
import rocks.clanattack.util.log.Logger
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

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
        val amount = registerServices(Register::class.annotatedClasses)
        find<Logger>().info("Registered $amount services.")

        initialRegister = true
    }

    private fun registerServices(classLoader: ClassLoader, basePackage: String) {
        find<Logger>().info("Registering services from $basePackage...")
        val amount = registerServices(Register::class.getAnnotatedClasses(classLoader, basePackage))
        find<Logger>().info("Registered $amount services from $basePackage.")
    }

    private fun registerServices(annotated: List<KClass<out Any>>) = annotated.filter {
        val annotation = it.findAnnotation<Register>() ?: return@filter false
        if (!it.isSubclassOf(ServiceImplementation::class)) {
            find<Logger>().error("The service ${it.qualifiedName} doesn't implement ServiceImplementation")
            return@filter false
        }

        val definition = annotation.definition
        if (!it.isSubclassOf(definition)) {
            find<Logger>().error(
                "The service ${it.qualifiedName} doesn't implement its definition (${definition.qualifiedName})"
            )
            return@filter false
        }

        val instance = try {
            registry.create(it)
        } catch (e: IllegalArgumentException) {
            find<Logger>().error("The service ${it.qualifiedName} couldn't be registered", e)
            return@filter false
        }

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

            untouchedServices.forEach { (definition, info) ->
                if (info.annotation.depends.all { services[it]?.implementation?.enabled == true }) {
                    try {
                        info.implementation.enable()
                        info.implementation.setEnabled(true)
                        enabled++
                    } catch (e: Exception) {
                        find<Logger>().error(
                            "An error occurred while enabling ${definition.qualifiedName}.",
                            e.invocationCause
                        )
                    }

                    modified.add(definition)
                }
            }

            if (modified.isEmpty()) fails++
            else fails = 0

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

            untouchedServices.forEach { (definition, info) ->
                if (info.annotation.depends.all { services[it]?.implementation?.enabled != true }) {
                    try {
                        info.implementation.disable()
                        info.implementation.setEnabled(false)
                        disabled++
                    } catch (e: Exception) {
                        find<Logger>().error(
                            "An error occurred while disabling ${definition.qualifiedName}.",
                            e.invocationCause
                        )
                    }

                    modified.add(definition)
                }
            }

            if (modified.isEmpty()) fails++
            else fails = 0

            modified.forEach { untouchedServices.remove(it) }
            modified.clear()
        }

        find<Logger>().info("Disabled $disabled services.")
    }

}