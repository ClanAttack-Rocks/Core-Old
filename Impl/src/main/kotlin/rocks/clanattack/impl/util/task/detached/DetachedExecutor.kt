package rocks.clanattack.impl.util.task.detached

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import rocks.clanattack.util.task.Task
import rocks.clanattack.util.task.TaskConfig
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

object DetachedExecutor : CoroutineScope {

    private val threadPool = Executors.newCachedThreadPool()

    override val coroutineContext = object  : CoroutineDispatcher() {

        override fun dispatch(context: CoroutineContext, block: Runnable) = threadPool.execute(block)
    }

    fun stop() {
        threadPool.shutdown()
    }

    fun runTask(config: TaskConfig, task: suspend Task.() -> Unit): Task {
        val instance = DetachedTaskImpl(config)
        val runnable = DetachedTaskRunnable(instance, task)
        instance.job = launch {
            runnable.start()
        }
        return instance
    }

}