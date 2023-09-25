package rocks.clanattack.task

/**
 * A [Task] executed by the [TaskService].
 */
interface Task {

    /**
     * The id of the [Task].
     */
    val id: Int

    /**
     * Weather the [Task] is cancelled.
     */
    val cancelled: Boolean

    /**
     * The configuration of the [Task].
     */
    val config: TaskConfig

    /**
     * The amount of times the [Task] has been executed.
     */
    val timesExecuted: UInt

    /**
     * Cancels the [Task].
     */
    fun cancel()

}