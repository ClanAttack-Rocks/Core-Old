package rocks.clanattack.impl.task.detached

import kotlinx.coroutines.delay
import rocks.clanattack.task.Task

class DetachedTaskRunnable(private val instance: DetachedTask, val task: suspend Task.() -> Unit) {

    suspend fun start() {
        delay(instance.config.delay)

        while (!instance.cancelled && instance.config.times == null || instance.timesExecuted < instance.config.times!!) {
            run()

            if (instance.config.period != null) delay(instance.config.period!!)
            else break
        }

        if (!instance.cancelled) instance.cancel()
    }

    private suspend fun run() {
        instance.timesExecuted++
        instance.task()
    }

}