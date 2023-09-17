package rocks.clanattack.impl.util.minecraft.task

import rocks.clanattack.util.minecraft.task.Task

class TaskRunnable(private val instance: TaskImpl, val task: Task.() -> Unit) : Runnable {

    override fun run() {
        instance.task()
    }
}