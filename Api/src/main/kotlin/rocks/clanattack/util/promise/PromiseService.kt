package rocks.clanattack.util.promise

import rocks.clanattack.entry.service.Service
import rocks.clanattack.util.promise.exception.AllPromiseRejectedException
import kotlin.reflect.KClass

/**
 * The [PromiseService] is used to create [Promise]s.
 */
interface PromiseService : Service {

    /**
     * Creates a new [PromiseProvider] for the given [type][T].
     *
     * @see PromiseProvider
     */
    fun <T> create(): PromiseProvider<T>

    /**
     * Takes multiple [promises] and returns a new [Promise] that is fulfilled with a list of the results of the
     * [promises] when all of the [promises] are fulfilled.
     *
     * If any of the [promises] is rejected, the [Promise] returned by this method is rejected with the reason of the
     * first rejected [Promise].
     */
    fun <T> all(vararg promises: Promise<T>): Promise<List<T>>

    /**
     * Takes multiple [promises] and returns a new [Promise] that is fulfilled with a list of the results of the
     * [promises] when all of the [promises] are settled.
     */
    fun <T> allSettled(vararg promises: Promise<T>): Promise<List<PromiseResult<T>>>

    /**
     * Takes multiple [promises] and returns a new [Promise] that is fulfilled with the value of the first fulfilled
     * [Promise].
     *
     * If all of the [promises] are rejected, the [Promise] returned by this method is rejected with a
     * [AllPromiseRejectedException] containing the reasons of all rejected [promises].
     */
    fun <T> any(vararg promises: Promise<T>): Promise<T>

    /**
     * Takes multiple [promises] and returns a new [Promise] that is fulfilled or rejected with the value or reason of
     * the first [Promise] that settles.
     */
    fun <T> race(vararg promises: Promise<T>): Promise<T>

    /**
     * Returns a [Promise] that is rejected with the given [reason].
     */
    fun <T> reject(reason: Throwable): Promise<T>

    /**
     * Returns a [Promise] that is fulfilled with the given [value].
     */
    fun <T> fulfill(value: T): Promise<T>

}