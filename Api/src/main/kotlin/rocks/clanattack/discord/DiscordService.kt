package rocks.clanattack.discord

import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import rocks.clanattack.entry.service.Service

/**
 * The [DiscordService] is used to interact with the Discord API.
 */
interface DiscordService : Service {

    /**
     * The [Kord] instance, used to interact with the Discord API.
     */
    val kord: Kord

    /**
     * The main [Guild] of the bot.
     *
     * @throws IllegalStateException If the bot is not on the main guild or the main guild is not set.
     */
    val guild: Guild

}