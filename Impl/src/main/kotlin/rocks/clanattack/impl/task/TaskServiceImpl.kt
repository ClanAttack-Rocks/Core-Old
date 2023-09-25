package rocks.clanattack.impl.task

import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.impl.task.attached.AttachedExecutor
import rocks.clanattack.impl.task.detached.DetachedExecutor
import rocks.clanattack.task.Task
import rocks.clanattack.task.TaskConfig
import rocks.clanattack.task.TaskService
import java.util.concurrent.CompletableFuture

@Register(definition = TaskService::class)
class TaskServiceImpl : ServiceImplementation(), TaskService {

    override fun disable() {
        AttachedExecutor.stop()
        DetachedExecutor.stop()
    }

    override fun execute(config: TaskConfig.Builder.() -> Unit, task: suspend Task.() -> Unit) =
        TaskConfigImpl.Builder()
            .apply(config)
            .build()
            .let {
                if (it.detached) DetachedExecutor.runTask(it, task)
                else AttachedExecutor.runTask(it, task)
            }

    override fun <T> asCompletableFuture(block: suspend Task.() -> T): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        execute(detached = true) {
            future.complete(block())
        }
        return future
    }

}