package rocks.clanattack.extention

/**
 * Checks if the predicate evaluates to true, if so, the block will be executed
 *
 * @param T the type of the object
 * @param predicate the predicate to evaluate
 * @param block the block to execute
 * @return the object itself
 */
fun <T> T.alsoIf(predicate: (T) -> Boolean, block: (T) -> Unit): T {
    if (predicate(this)) {
        block(this)
    }

    return this
}

/**
 * Checks if the condition is true, if so, the block will be executed
 *
 * @param T the type of the object
 * @param condition the condition to evaluate
 * @param block the block to execute
 * @return the object itself
 */
fun <T> T.alsoIf(condition: Boolean, block: (T) -> Unit): T = alsoIf({ condition }, block)

/**
 * Checks if the predicate evaluates to true, if so, the block will be executed
 *
 * @param T the type of the object
 * @param predicate the predicate to evaluate
 * @param block the block to execute, which must return a value of type [T]
 * @return the result of the [block] or `this` if the [predicate] is false
 */
fun <T> T.letIf(predicate: (T) -> Boolean, block: (T) -> T) = if (predicate(this)) block(this) else this

/**
 * Checks if the condition is true, if so, the block will be executed
 *
 * @param T the type of the object
 * @param condition the condition to evaluate
 * @param block the block to execute, which must return a value of type [T]
 * @return the result of the [block] or `this` if the [condition] is false
 */
fun <T> T.letIf(condition: Boolean, block: (T) -> T) = letIf({ condition }, block)

/**
 * Executes the block if this is null
 *
 * @param block the block to execute
 * @return the object itself
 */
inline fun <T> T?.ifNull(block: () -> Unit): T? {
    if (this == null) block()
    return this
}

/**
 * Executes the block and returns always [Unit]
 */
fun <T : Any?> unit(block: () -> T) { block() }

/**
 * Executes the block with the receiver as receiver and returns the block's result
 *
 * @param T the type of the receiver
 * @param R the return type of the block
 * @param block the block to execute
 * @return the result of the block
 */
inline fun <T : Any, R : Any?> T.with(block: T.() -> R): R = block()
