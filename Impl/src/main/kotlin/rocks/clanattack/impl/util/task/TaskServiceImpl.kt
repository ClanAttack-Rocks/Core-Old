package rocks.clanattack.impl.util.task

import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.impl.util.task.attached.AttachedExecutor
import rocks.clanattack.util.task.Task
import rocks.clanattack.util.task.TaskConfig
import rocks.clanattack.util.task.TaskService

@Register(definition = TaskService::class)
class TaskServiceImpl : ServiceImplementation(), TaskService {

    override fun disable() {
        AttachedExecutor.stop()

        // TODO: Stop all kotlin coroutines
    }

    override fun execute(config: TaskConfig.Builder.() -> Unit, task: suspend Task.() -> Unit) =
        TaskConfigImpl.Builder()
            .apply(config)
            .build()
            .let {
                if (it.detached) error("Detached tasks are not implemented yet")
                else AttachedExecutor.runTask(it, task)
            }

}