package rocks.clanattack.task

import rocks.clanattack.entry.find
import rocks.clanattack.entry.service.Service
import rocks.clanattack.util.extention.unit
import rocks.clanattack.util.promise.Promise
import java.util.concurrent.CompletableFuture
import kotlin.time.Duration

/**
 * The [TaskService] is responsible for executing [Task]s.
 *
 * For this it uses papers scheduler system.
 */
@Suppress("unused")
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

    /**
     * Executes the given [task] with
     * - [detached],
     * - [synchronous],
     * - [delay],
     * - [period] and
     * - [times].
     */
    fun execute(
        detached: Boolean? = null,
        synchronous: Boolean? = null,
        delay: Duration? = null,
        period: Duration? = null,
        times: UInt? = null,
        task: suspend Task.() -> Unit
    ): Task = execute({
        detached?.let { this.detached = it }
        synchronous?.let { this.synchronous = it }
        delay?.let { this.delay = it }
        period?.let { this.period = it }
        times?.let { this.times = it }
    }, task)

    /**
     * Executes the given [task]
     * - detached,
     * - asynchronous,
     * - now and
     * - exactly once
     *
     * and returns the result as a [Promise].
     */
    fun <T : Any> promise(task: suspend Task.() -> T): Promise<T>

    /**
     * Executes the given [block] detached (now) and returns the result as a [CompletableFuture].
     *
     * **DEPRECIATED** Use [promise] instead.
     */
    @Deprecated("Use promise instead", ReplaceWith("promise(block)"))
    fun <T> asCompletableFuture(block: suspend Task.() -> T): CompletableFuture<T>

}

/**
 * Executes the given [block] detached once now.
 */
fun detached(block: suspend Task.() -> Unit) = unit { find<TaskService>().execute(detached = true, task = block) }

/**
 * Executes the given [block] attached, not synchronous, once now.
 */
fun async(block: suspend Task.() -> Unit) = unit { find<TaskService>().execute(task = block) }

/**
 * Executes the given [block] attached, synchronous, once now.
 */
fun sync(block: suspend Task.() -> Unit) = unit { find<TaskService>().execute(synchronous = true, task = block) }

/**
 * Executes the given [block] detached, once now and returns the result as a [Promise].
 */
fun <T : Any> promise(block: suspend Task.() -> T) = find<TaskService>().promise(block)