package rocks.clanattack.util.task

import rocks.clanattack.entry.service.Service

/**
 * The [TaskService] is responsible for executing [Task]s.
 *
 * For this it uses papers scheduler system.
 */
interface TaskService : Service {

    /**
     * Executes the given [task] with the given [config] created from the block.
     *
     * The [task] may block a thread, depending on the [TaskConfig.detached] and [TaskConfig.synchronous] values.
     * Be careful when using long blocking operation in a non-detached, synchronous [Task].
     */
    fun execute(config: TaskConfig.Builder.() -> Unit, task: suspend Task.() -> Unit): Task


    /**
     * Executes the given [task] with a default config (attached, asynchronous, no delay, no period, infinite times).
     */
    fun execute(task: suspend Task.() -> Unit): Task = execute({}, task)

}