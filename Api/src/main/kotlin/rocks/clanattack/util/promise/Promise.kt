package rocks.clanattack.util.promise

import rocks.clanattack.util.promise.exception.PromiseTimeoutException
import kotlin.time.Duration

/**
 * A [Promise] is a proxy for a value not necessarily known when the promise is created.
 *
 * It allows you to associate handlers with an asynchronous action's eventual success value or failure reason.
 *
 * This lets asynchronous methods return values like synchronous methods: instead of immediately returning the final value,
 * the asynchronous method returns a promise to supply the value at some point in the future.
 *
 * A [Promise] is in one of these states:
 * - pending: initial state, neither fulfilled nor rejected.
 * - fulfilled: meaning that the operation completed successfully.
 * - rejected: meaning that the operation failed.
 *
 * A pending promise can either be fulfilled with a value or rejected with a reason (error).
 *
 * When either of these options happens, the associated handlers queued up by a promise's then method are called.
 */
interface Promise<T> {

    /**
     * The current state of the [Promise].
     */
    val state: PromiseState

    /**
     * Whether the [Promise] is settled (fulfilled or rejected).
     */
    val isSettled: Boolean
        get() = state != PromiseState.PENDING

    /**
     * Attaches callbacks for the fulfillment of the [Promise].
     *
     * If the [Promise] is already settled, the corresponding callback will be executed immediately.
     *
     * The returned [Promise] is a new [Promise] that is either fulfilled or rejected
     * with the same value or reason as the original.
     *
     * If the [onFulfill] method throws an error, the returned [Promise] is rejected with the thrown error as the reason.
     *
     * A new [Promise] is always created and returned.
     */
    fun then(onFulfill: (T) -> Unit): Promise<T>

    /**
     * Attaches callbacks for the resolution of the [Promise].
     *
     * If the [Promise] is already settled, the corresponding callback will be executed immediately.
     *
     * The returned [Promise] is a new [Promise] that is either fulfilled or rejected
     * with the same value or reason as the original.
     *
     * If the [onFulfill] method throws an error, the returned [Promise] is rejected with the thrown error as the reason.
     *
     * A new [Promise] is always created and returned.
     */
    fun then(onFulfill: (T) -> Unit, onReject: (Throwable) -> Unit): Promise<T>

    /**
     * Attaches a callback for only the resolution of the [Promise].
     *
     * If the [Promise] is already settled, the corresponding callback will be executed immediately.
     *
     * The returned [Promise] is a new [Promise] that is either fulfilled with the value returned by the callback
     * or rejected with the reason why the callback threw an error.
     *
     * If the [onFulfill] method throws an error, the returned [Promise] is rejected with the thrown error as the reason.
     *
     * A new [Promise] is always created and returned.
     */
    fun <U> map(onFulfill: (T) -> U): Promise<U>

    /**
     * Returns a [Promise] that is either fulfilled or rejected with the same value or reason as the original,
     * or rejected after the provided [duration] with a [PromiseTimeoutException].
     *
     * If the [Promise] is already settled, the corresponding callback will be executed immediately.
     *
     * A new [Promise] is always created and returned.
     */
    fun timeout(duration: Duration): Promise<T>

    /**
     * Returns a [Promise] that is either fulfilled with the value of the original [Promise] or
     * fulfilled with the provided [value] if the original [Promise] is rejected.
     *
     * If the [Promise] is already settled, the corresponding callback will be executed immediately.
     *
     * If the [Promise] is rejected, the returned [Promise] is fulfilled with the provided [value].
     *
     * A new [Promise] is always created and returned.
     */
    fun orElse(value: T): Promise<T>

    /**
     * Attaches a callback for only the rejection of the [Promise].
     *
     * If the [Promise] is already settled, the corresponding callback will be executed immediately.
     *
     * The returned [Promise] is a new [Promise] that is either fulfilled with the value returned by the callback
     * or rejected with the reason why the callback threw an error.
     *
     * If the [onReject] method throws an error, the returned [Promise] is rejected with the thrown error as the reason.
     *
     * A new [Promise] is always created and returned.
     */
    fun catch(onReject: (Throwable) -> Unit): Promise<T>

    /**
     * Attaches a callback for only the rejection of the [Promise].
     *
     * If the [Promise] is already settled, the corresponding callback will be executed immediately.
     *
     * The returned [Promise] is a new [Promise] that is either fulfilled with the same value or rejected with the reason
     * provided by the callback.
     *
     * If the [onReject] method throws an error, the returned [Promise] is rejected with the thrown error as the reason.
     *
     * A new [Promise] is always created and returned.
     */
    fun catchMap(onReject: (Throwable) -> Throwable): Promise<T>

    /**
     * Attaches a callback that is invoked when the [Promise] is settled (fulfilled or rejected).
     *
     * The returned [Promise] is a new [Promise] that is either fulfilled or rejected
     * with the same value or reason as the original.
     *
     * If the [onFinally] method throws an error, the returned [Promise] is rejected with the thrown error as the reason.
     *
     * A new [Promise] is always created and returned.
     */
    fun finally(onFinally: (PromiseResult<T>) -> Unit): Promise<T>

    /**
     * Awaits the resolution of the [Promise].
     *
     * This function suspends the current coroutine until the [Promise] is settled.
     *
     * If the [Promise] is already settled, the value is returned immediately.
     * If the [Promise] is rejected, the [Throwable] is thrown.
     */
    suspend fun await(): T

    /**
     * Gets the value of the [Promise].
     *
     * This function blocks the current thread until the [Promise] is settled.
     *
     * If the [Promise] is already settled, the value is returned immediately.
     * If the [Promise] is rejected, the [Throwable] is thrown.
     */
    fun get(): T

}