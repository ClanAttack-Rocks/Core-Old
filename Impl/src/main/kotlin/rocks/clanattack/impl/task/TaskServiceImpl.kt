package rocks.clanattack.impl.task

import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.impl.task.attached.AttachedExecutor
import rocks.clanattack.impl.task.detached.DetachedExecutor
import rocks.clanattack.task.Task
import rocks.clanattack.task.TaskConfig
import rocks.clanattack.task.TaskService

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

}