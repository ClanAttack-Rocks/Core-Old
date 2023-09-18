package rocks.clanattack.impl.util.task

import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.impl.util.task.attached.AttachedExecutor
import rocks.clanattack.impl.util.task.detached.DetachedExecutor
import rocks.clanattack.util.task.Task
import rocks.clanattack.util.task.TaskConfig
import rocks.clanattack.util.task.TaskService

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