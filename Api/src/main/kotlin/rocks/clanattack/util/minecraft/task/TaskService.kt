package rocks.clanattack.util.minecraft.task

import rocks.clanattack.entry.service.Service
import kotlin.time.Duration

/**
 * The [TaskService] is responsible for executing [Task]s.
 *
 * For this it uses papers scheduler system.
 */
interface TaskService : Service {

    /**
     * Executes the given [task].
     *
     * If a task is executed [asynchronous] it will be executed in a different thread,
     * otherwise it will be executed in the main thread.
     */
    fun execute(asynchronous: Boolean = false, task: Task.() -> Unit): Task

    /**
     * Executes the given [task] after the given [delay] in ticks.
     *
     * If a task is executed [asynchronous] it will be executed in a different thread,
     * otherwise it will be executed in the main thread.
     */
    fun later(delay: Long, asynchronous: Boolean = false, task: Task.() -> Unit): Task

    /**
     * Executes the given [task] after the given [delay].
     *
     * If a task is executed [asynchronous] it will be executed in a different thread,
     * otherwise it will be executed in the main thread.
     */
    fun later(delay: Duration, asynchronous: Boolean = false, task: Task.() -> Unit): Task

    /**
     * Executes the given [task] after the given [delay] in ticks and repeats it every [period] in ticks.
     *
     * If a task is executed [asynchronous] it will be executed in a different thread,
     * otherwise it will be executed in the main thread.
     */
    fun timer(delay: Long, period: Long, asynchronous: Boolean = false, task: Task.() -> Unit): Task

    /**
     * Executes the given [task] after the given [delay] and repeats it every [period].
     *
     * If a task is executed [asynchronous] it will be executed in a different thread,
     * otherwise it will be executed in the main thread.
     */
    fun timer(delay: Duration, period: Duration, asynchronous: Boolean = false, task: Task.() -> Unit): Task

    /**
     * Executes the given [task] every [period] in ticks, the first time now.
     *
     * If a task is executed [asynchronous] it will be executed in a different thread,
     * otherwise it will be executed in the main thread.
     */
    fun timer(period: Long, asynchronous: Boolean = false, task: Task.() -> Unit): Task

    /**
     * Executes the given [task] every [period], the first time now.
     *
     * If a task is executed [asynchronous] it will be executed in a different thread,
     * otherwise it will be executed in the main thread.
     */
    fun timer(period: Duration, asynchronous: Boolean = false, task: Task.() -> Unit): Task

}