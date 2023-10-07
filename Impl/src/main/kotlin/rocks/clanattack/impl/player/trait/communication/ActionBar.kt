package rocks.clanattack.impl.player.trait.communication

import kotlinx.datetime.LocalDateTime
import net.kyori.adventure.text.ComponentLike
import org.bukkit.Bukkit
import rocks.clanattack.entry.find
import rocks.clanattack.entry.point.EntryPoint
import rocks.clanattack.impl.task.TaskService
import rocks.clanattack.player.trait.communication.CommunicationPriority
import rocks.clanattack.util.extention.now
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

data class ActionBarInformation(
    val message: ComponentLike,
    val endTime: LocalDateTime,
    val priority: CommunicationPriority
)

object ActionBar {

    private var running = false

    private val actionBars = mutableMapOf<UUID, ActionBarInformation>()

    operator fun contains(uuid: UUID) = actionBars.containsKey(uuid)

    operator fun get(uuid: UUID) = actionBars[uuid]

    operator fun set(uuid: UUID, actionBarInformation: ActionBarInformation) {
        actionBars[uuid] = actionBarInformation
        start()
    }

    private fun start() {
        if (running) return

        running = true
        find<TaskService>().execute(synchronous = true, period = 1.seconds) {
            val remove = mutableListOf<UUID>()

            actionBars.forEach {
                if (it.value.endTime <= LocalDateTime.now()) {
                    remove.add(it.key)
                    return@forEach
                }

                val player = Bukkit.getPlayer(it.key)
                if (player == null) {
                    remove.add(it.key)
                    return@forEach
                }

                player.sendActionBar(it.value.message)
            }

            remove.forEach { actionBars.remove(it) }
            if (actionBars.isEmpty()) {
                running = false
                this.cancel()
            }
        }
    }

}