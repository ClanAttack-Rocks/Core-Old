package rocks.clanattack.util.extention

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEvent
import rocks.clanattack.entry.find
import rocks.clanattack.player.PlayerService

/**
 * Gets the [ClanAttack-Player][rocks.clanattack.player.Player] from the [PlayerEvent].
 */
val PlayerEvent.rocksPlayer
    get() = find<PlayerService>()[player]

/**
 * Gets the [ClanAttack-Player][rocks.clanattack.player.Player] from the [Player].
 */
val Player.rocksPlayer
    get() = find<PlayerService>()[uniqueId]