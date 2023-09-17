package rocks.clanattack.entry.service

import rocks.clanattack.entry.Registry

/**
 * The definition of a [Service].
 *
 * A [Service] is in general a collection of methods or properties that can be used by other plugins.
 *
 * The definition only provides the methods but no implementation for them and
 * is the only part visible to other plugins.
 *
 * Each definition must have a implementation that is a subclass of [ServiceImplementation]
 * and is annotated with [Register].
 */
interface Service

/**
 * The implementation of a [Service].
 *
 * Each implementation must extend the [ServiceImplementation] class,
 * in addition to the definition of the service.
 *
 * The implementation is the logic that is executed when a user of the service calls a method.
 *
 * The backend must be
 * - a subclass of the definition of the service,
 *
 * **and**
 * - a subclass of [ServiceImplementation],
 *
 * **and**
 * - be annotated with [Register],
 *
 * **and**
 * - an object or
 * - a class with a public constructor with no parameters or a single [Registry] parameter.
 *
 * The backend can be private, but must be accessible.
 */
open class ServiceImplementation {

    /**
     * The current state of the service.
     */
    val enabled: Boolean = false

    /**
     * Called when the service is enabled.
     */
    open fun enable() {}

    /**
     * Called when the service is disabled.
     */
    open fun disable() {}

}
