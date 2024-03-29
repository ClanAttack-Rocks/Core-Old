package rocks.clanattack.impl.task.detached

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import rocks.clanattack.task.Task
import rocks.clanattack.task.TaskConfig
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

object DetachedExecutor : CoroutineScope {

    private val threadPool = Executors.newCachedThreadPool()

    override val coroutineContext = object  : CoroutineDispatcher() {

        override fun dispatch(context: CoroutineContext, block: Runnable) {
            if (threadPool.isShutdown || threadPool.isTerminated)
                throw IllegalStateException("Cannot dispatch task to shutdown thread pool.")

            threadPool.execute(block)
        }
    }

    fun stop() {
        threadPool.shutdown()
    }

    fun runTask(config: TaskConfig, task: suspend Task.() -> Unit): Task {
        val instance = DetachedTask(config)
        val runnable = DetachedTaskRunnable(instance, task)
        instance.job = launch {
            runnable.start()
        }
        return instance
    }

}