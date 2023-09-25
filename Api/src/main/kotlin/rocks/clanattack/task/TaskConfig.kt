package rocks.clanattack.task

import kotlin.time.Duration

/**
 * The configuration of a [Task].
 *
 * This includes how and when the [Task] should be executed.
 */
interface TaskConfig {

    /**
     * Weather the task will be executed detached from the
     * minecraft scheduler system (using kotlin coroutines).
     */
    val detached: Boolean

    /**
     * Weather the task will be executed synchronously.
     *
     * If executed [detached] the task must always be executed asynchronously.
     */
    val synchronous: Boolean

    /**
     * The [Duration] between the scheduling of the [Task]
     * and the execution of the [Task].
     */
    val delay: Duration

    /**
     * The [Duration] between the execution of the [Task]
     * and the next execution of the [Task].
     *
     * If `null` the [Task] will only be executed once.
     */
    val period: Duration?

    /**
     * The amount of times the [Task] will be executed.
     *
     * If `null` the [Task] will be executed infinitely.
     *
     * If [period] is `null`, this value must also be `null`.
     *
     * [times] can never be `0`.
     */
    val times: UInt?

    /**
     * The [Builder] used to create a [TaskConfig].
     */
    interface Builder : TaskConfig {

        override var detached: Boolean
        override var synchronous: Boolean
        override var delay: Duration
        override var period: Duration?
        override var times: UInt?

        /**
         * Creates a [TaskConfig] from the current state of the [Builder].
         *
         * If not set the following values will be used:
         * - [detached] = `false`
         * - [synchronous] = `false`
         * - [delay] = `0.seconds`
         * - [period] = `null`
         * - [times] = `null`
         *
         * @throws IllegalArgumentException if [detached] and [synchronous] are both `true`
         * @throws IllegalArgumentException if [period] is `null` and [times] is not `null`
         * @throws IllegalArgumentException if [times] is `0`
         */
        fun build(): TaskConfig

    }

}