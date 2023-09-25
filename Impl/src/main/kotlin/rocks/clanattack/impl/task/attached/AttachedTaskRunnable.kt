package rocks.clanattack.impl.task.attached

import kotlinx.coroutines.runBlocking
import rocks.clanattack.task.Task

class AttachedTaskRunnable(private val instance: AttachedTask, val task: suspend Task.() -> Unit) : Runnable {
    
    override fun run() {
        instance.timesExecuted++
        runBlocking {
            instance.task()
        }

        if (instance.config.times != null && instance.timesExecuted >= instance.config.times!!) instance.cancel()
    }
}