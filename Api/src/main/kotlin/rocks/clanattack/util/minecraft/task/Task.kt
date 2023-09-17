package rocks.clanattack.util.minecraft.task

/**
 * A [Task] executed by the [TaskService].
 */
interface Task {

    /**
     * The id of the [Task].
     */
    val id: Int

    /**
     * Weather the [Task] is executed synchronously or asynchronously.
     */
    val synchronous: Boolean

    /**
     * Weather the [Task] is cancelled.
     */
    val cancelled: Boolean

    /**
     * Cancels the [Task].
     */
    fun cancel()

}