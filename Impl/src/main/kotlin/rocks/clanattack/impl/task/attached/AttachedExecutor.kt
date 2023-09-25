package rocks.clanattack.impl.task.attached

import org.bukkit.Bukkit
import rocks.clanattack.entry.find
import rocks.clanattack.extention.inWholeTicks
import rocks.clanattack.task.Task
import rocks.clanattack.task.TaskConfig

object AttachedExecutor {

    fun stop() {
        Bukkit.getScheduler()
            .pendingTasks
            .filter { it.owner == find() }
            .forEach { it.cancel() }
    }

    fun runTask(config: TaskConfig, task: suspend Task.() -> Unit): Task {
        val taskInstance = AttachedTaskImpl(config)
        val taskRunnable = AttachedTaskRunnable(taskInstance, task)

        val bukkitTask = when {
            config.synchronous && config.period != null -> Bukkit.getScheduler().runTaskTimer(
                    find(),
                    taskRunnable,
                    config.delay.inWholeTicks,
                    config.period!!.inWholeTicks
                )
            config.synchronous && config.delay.inWholeSeconds != 0L -> Bukkit.getScheduler().runTaskLater(
                    find(),
                    taskRunnable,
                    config.delay.inWholeTicks
                )
            config.synchronous -> Bukkit.getScheduler().runTask(find(), taskRunnable)
            config.period != null -> Bukkit.getScheduler().runTaskTimerAsynchronously(
                    find(),
                    taskRunnable,
                    config.delay.inWholeTicks,
                    config.period!!.inWholeTicks
                )
            config.delay.inWholeSeconds != 0L -> Bukkit.getScheduler().runTaskLaterAsynchronously(
                    find(),
                    taskRunnable,
                    config.delay.inWholeTicks
                )
            else -> Bukkit.getScheduler().runTaskAsynchronously(find(), taskRunnable)
        }

        taskInstance.task = bukkitTask
        return taskInstance
    }

}