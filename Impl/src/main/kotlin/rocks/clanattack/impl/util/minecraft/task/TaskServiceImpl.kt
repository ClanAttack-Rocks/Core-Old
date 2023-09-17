package rocks.clanattack.impl.util.minecraft.task

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import rocks.clanattack.entry.find
import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.util.minecraft.task.Task
import rocks.clanattack.util.minecraft.task.TaskService
import kotlin.time.Duration

@Register(definition = TaskService::class)
class TaskServiceImpl : ServiceImplementation(), TaskService {

    override fun disable() {
        Bukkit.getScheduler()
            .pendingTasks
            .filter { it.owner == find() }
            .forEach { it.cancel() }
    }

    private fun createTask(task: Task.() -> Unit, bukkitCreator: (TaskRunnable) -> BukkitTask): Task {
        val taskInstance = TaskImpl()
        val bukkitTask = bukkitCreator(TaskRunnable(taskInstance, task))
        taskInstance.task = bukkitTask
        return taskInstance
    }

    override fun execute(asynchronous: Boolean, task: Task.() -> Unit) = createTask(task) {
        if (asynchronous) Bukkit.getScheduler().runTaskAsynchronously(find(), it)
        else Bukkit.getScheduler().runTask(find(), it)
    }

    override fun later(delay: Long, asynchronous: Boolean, task: Task.() -> Unit) = createTask(task) {
        if (asynchronous) Bukkit.getScheduler().runTaskLaterAsynchronously(find(), it, delay)
        else Bukkit.getScheduler().runTaskLater(find(), it, delay)
    }

    override fun later(delay: Duration, asynchronous: Boolean, task: Task.() -> Unit) =
        later(delay.inWholeSeconds * 20, asynchronous, task)

    override fun timer(delay: Long, period: Long, asynchronous: Boolean, task: Task.() -> Unit) = createTask(task) {
        if (asynchronous) Bukkit.getScheduler().runTaskTimerAsynchronously(find(), it, delay, period)
        else Bukkit.getScheduler().runTaskTimer(find(), it, delay, period)
    }

    override fun timer(delay: Duration, period: Duration, asynchronous: Boolean, task: Task.() -> Unit) =
        timer(delay.inWholeSeconds * 20, period.inWholeSeconds * 20, asynchronous, task)

    override fun timer(period: Long, asynchronous: Boolean, task: Task.() -> Unit) = createTask(task) {
        if (asynchronous) Bukkit.getScheduler().runTaskTimerAsynchronously(find(), it, 0, period)
        else Bukkit.getScheduler().runTaskTimer(find(), it, 0, period)
    }

    override fun timer(period: Duration, asynchronous: Boolean, task: Task.() -> Unit) =
        timer(period.inWholeSeconds * 20, asynchronous, task)
}