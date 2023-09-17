package rocks.clanattack.impl.util.minecraft.task

import org.bukkit.scheduler.BukkitTask
import rocks.clanattack.util.minecraft.task.Task
import java.lang.IllegalStateException

class TaskImpl : Task {

    var task: BukkitTask? = null

    private val _task: BukkitTask
        get() = task ?: throw IllegalStateException("Runnable must be initialized to run")

    override val id: Int
        get() = _task.taskId

    override val synchronous: Boolean
        get() = _task.isSync

    override val cancelled: Boolean
        get() = _task.isCancelled

    override fun cancel() = _task.cancel()

}