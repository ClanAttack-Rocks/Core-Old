package rocks.clanattack.extention

import kotlin.reflect.KClass

/**
 * Calls the [block] with `this` as its receiver and returns its encapsulated result if invocation was successful,
 * catching all [exceptionTypes] exceptions that may happen in the [block] execution and encapsulating them as a failure.
 *
 * @param exceptionTypes the types of exceptions to catch
 * @param block the block to execute
 * @return the result of the [block] execution
 */
inline fun <T, R> T.runCatching(vararg exceptionTypes: KClass<out Throwable>, block: T.() -> R) = try {
    Result.success(block())
} catch (e: Exception) {
    if (exceptionTypes.any { it.isInstance(e) }) Result.failure(e)
    else throw e
}

/**
 * Calls the [block] with `this` value as its argument and returns its encapsulated result if invocation was successful,
 * catching any [Exception] that may happen in the [block] execution and encapsulating it as a failure.
 *
 * @param block the block to execute
 * @return the result of the [block] execution
 */
inline fun <T, R> T.letCatching(block: (T) -> R) = try {
    Result.success(block(this))
} catch (e: Exception) {
    Result.failure(e)
}

/**
 * Calls the [block] with `this` value as its argument and returns its encapsulated result if invocation was successful,
 * catching all [exceptionTypes] exceptions that may happen in the [block] execution and encapsulating them as a failure.
 *
 * @param exceptionTypes the types of exceptions to catch
 * @param block the block to execute
 * @return the result of the [block] execution
 */
inline fun <T, R> T.letCatching(vararg exceptionTypes: KClass<out Throwable>, block: (T) -> R) = try {
    Result.success(block(this))
} catch (e: Exception) {
    if (exceptionTypes.any { it.isInstance(e) }) Result.failure(e)
    else throw e
}

/**
 * Calls the [block] in a try catch block and returns its result or `null` if an exception was thrown.
 *
 * @param block the block to execute
 * @return the result of the [block] execution or `null` if an exception was thrown
 */
inline fun <T> succeedOrNull(block: () -> T) = try {
    block()
} catch (e: Exception) {
    null
}