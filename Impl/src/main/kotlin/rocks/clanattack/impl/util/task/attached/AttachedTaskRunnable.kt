package rocks.clanattack.impl.util.task.attached

import kotlinx.coroutines.runBlocking
import rocks.clanattack.util.task.Task

class AttachedTaskRunnable(private val instance: AttachedTaskImpl, val task: suspend Task.() -> Unit) : Runnable {
    
    override fun run() {
        instance.timesExecuted++
        runBlocking {
            instance.task()
        }

        if (instance.config.times != null && instance.timesExecuted >= instance.config.times!!) instance.cancel()
    }
}