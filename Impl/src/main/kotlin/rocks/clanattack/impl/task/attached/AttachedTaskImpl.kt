package rocks.clanattack.impl.task.attached

import org.bukkit.scheduler.BukkitTask
import rocks.clanattack.task.Task
import rocks.clanattack.task.TaskConfig
import java.lang.IllegalStateException

class AttachedTaskImpl(override val config: TaskConfig) : Task {

    var task: BukkitTask? = null
    private val _task: BukkitTask
        get() = task ?: throw IllegalStateException("Runnable must be initialized to run")

    override val id: Int
        get() = _task.taskId

    override val cancelled: Boolean
        get() = _task.isCancelled

    override var timesExecuted = 0u

    override fun cancel() = _task.cancel()

}
