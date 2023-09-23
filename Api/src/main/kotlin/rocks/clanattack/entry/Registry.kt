package rocks.clanattack.entry

import rocks.clanattack.entry.point.EntryPoint
import rocks.clanattack.entry.point.ExitPoint
import rocks.clanattack.entry.service.Service
import kotlin.reflect.KClass

/**
 * The [Registry]
 */
lateinit var registry: Registry
    private set

/**
 * The [Registry] is used to register instances of classes.
 */
@Suppress("unused")
interface Registry {

    /**
     * All class instances.
     *
     * This contains all classes every created by the system automatically.
     *
     * So amongst others
     * - all [Service]s
     * - all classes with [EntryPoint]s and
     * - all classes with [ExitPoint]s.
     */
    val instances: List<Any>

    /**
     * Gets the instance for the given [klass].
     *
     * If the specific class is not found, a more specific class (a class that implements the given class)
     * will be searched. The first eligible class will be returned.
     *
     * If no class is found that is a subtype of the given [klass], null will be returned.
     *
     * So getting `Any::class` will return the first instance of any class.
     */
    operator fun <T : Any> get(klass: KClass<T>): T?

    /**
     * Creates a new instance of the given [klass].
     *
     * For this the [klass] must be
     * - an object or
     * - a class wit a no-args constructor or
     * - a class with a constructor with a single [Registry] parameter.
     *
     * The constructor can be private, but must be accessible.
     *
     * The created class will also be registered.
     *
     * @throws IllegalArgumentException If a class with the exact same type is already registered.
     * @throws IllegalArgumentException When the class isn't eligible for automatic creation.
     */
    @Throws(IllegalArgumentException::class)
    fun <T : Any> create(klass: KClass<T>): T

//    /**
//     * Registers the given [instance] for the given [klass].
//     *
//     * @throws IllegalArgumentException When the class is already registered.
//     */
//    @Throws(IllegalArgumentException::class)
//    fun <T : Any, P : T> set(klass: KClass<T>, instance: P)

    /**
     * Adds the given [instance] to the registry.
     *
     * @throws IllegalArgumentException When the class is already registered.
     */
    @Throws(IllegalArgumentException::class)
    fun add(instance: Any)

//    /**
//     * Creates a new instance of the given [implementationClass] and registers it for the given [definitionClass].
//     *
//     * For this the [implementationClass] must be
//     * - an object or
//     * - a class wit a no-args constructor or
//     * - a class with a constructor with a single [Registry] parameter.
//     *
//     * The constructor can be private, but must be accessible.
//     *
//     * @throws IllegalArgumentException When the class can't be instantiated or is already registered.
//     */
//    @Throws(IllegalArgumentException::class)
//    fun <T : Any, P : T> create(definitionClass: KClass<T>, implementationClass: KClass<P>): T {
//        val instance = create(implementationClass, false)
//        set(definitionClass, instance)
//        return instance
//    }

    /**
     * Gets the instance for the given [klass] or creates a new one.
     *
     * For restriction on creating, see [create].
     *
     * @throws IllegalArgumentException When the class can't be instantiated.
     *
     * @see get
     * @see create
     */
    @Throws(IllegalStateException::class, IllegalArgumentException::class)
    fun <T : Any> getOrCreate(klass: KClass<T>) = get(klass) ?: create(klass)

//    /**
//     * Gets the instance for the given [definitionClass] or creates a new [implementationClass] and registers it.
//     *
//     * For restriction on creating, see [create].
//     *
//     * @throws IllegalStateException When the saved instance is not of the correct type
//     * @throws IllegalArgumentException When the class can't be instantiated.
//     *
//     * @see get
//     * @see create
//     */
//    @Throws(IllegalStateException::class, IllegalArgumentException::class)
//    fun <T : Any, P : T> getOrCreate(definitionClass: KClass<T>, implementationClass: KClass<P>) =
//        get(definitionClass) ?: create(definitionClass, implementationClass)

}

/**
 * Finds an instance by its [type][T] or null if no instance was found.
 *
 * @throws IllegalStateException The saved instance is not of the correct type.
 */
@Throws(IllegalStateException::class)
inline fun <reified T : Any> findOrNull() = registry[T::class]

/**
 * Finds an instance by its [type][T].
 *
 * @throws IllegalStateException The saved instance is not of the correct type.
 * @throws NullPointerException When no instance was found.
 */
@Throws(IllegalStateException::class, NullPointerException::class)
inline fun <reified T : Any> find() = findOrNull<T>()
    ?: throw NullPointerException("No instance of ${T::class.simpleName} was found.")
