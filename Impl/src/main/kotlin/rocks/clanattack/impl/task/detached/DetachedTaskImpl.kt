package rocks.clanattack.impl.task.detached

import kotlinx.coroutines.Job
import rocks.clanattack.task.Task
import rocks.clanattack.task.TaskConfig

@Volatile
private var currentId = -1

class DetachedTaskImpl(override val config: TaskConfig) : Task {

    var job: Job? = null

    override val id = currentId--

    override var cancelled = false
    override var timesExecuted = 0u

    override fun cancel() {
        job?.cancel()
        cancelled = true
    }

}