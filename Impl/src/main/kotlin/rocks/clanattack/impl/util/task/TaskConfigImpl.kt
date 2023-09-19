package rocks.clanattack.impl.util.task

import rocks.clanattack.util.task.TaskConfig
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class TaskConfigImpl(
    override val detached: Boolean,
    override val synchronous: Boolean,
    override val delay: Duration,
    override val period: Duration?,
    override val times: UInt?
) : TaskConfig {

    class Builder : TaskConfig.Builder {
        override var detached: Boolean = false
        override var synchronous: Boolean = false
        override var delay: Duration = 0.seconds
        override var period: Duration? = null
        override var times: UInt? = null

        override fun build(): TaskConfig {
            if (detached && synchronous) {
                throw IllegalStateException("A detached task must always be executed asynchronously")
            }

            if (period == null && times != null) {
                throw IllegalStateException("A task with a null period must also have a null times value")
            }

            if (times == 0u) {
                throw IllegalStateException("A task can never be executed 0 times")
            }

            return TaskConfigImpl(detached, synchronous, delay, period, times)
        }

    }

}