package rocks.clanattack.player.trait

import dev.kord.core.entity.Member
import dev.kord.core.entity.Message
import rocks.clanattack.language.Replacement
import rocks.clanattack.player.trait.communication.CommunicationTrait
import rocks.clanattack.util.promise.Promise

/**
 * The [DiscordTrait] integrates with every aspect between a player and discord.
 */
interface DiscordTrait {

    /**
     * The [Member] associated with this player on the guild or `null` if the player is not connected to discord.
     */
    val member: Member?

    /**
     * Sends a message to the player over the private message channel.
     *
     * The message will be translated using the players [language][CommunicationTrait.language].
     *
     * @see Replacement
     * @throws IllegalStateException if the player is not connected to discord.
     */
    fun sendMessage(key: String, replacement: Replacement.() -> Unit = {}): Promise<Message>


}