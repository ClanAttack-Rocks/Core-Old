package rocks.clanattack.impl.player.trait

import dev.kord.core.entity.Member
import dev.vankka.mcdiscordreserializer.discord.DiscordSerializer
import kotlinx.coroutines.runBlocking
import rocks.clanattack.entry.find
import rocks.clanattack.impl.discord.DiscordService
import rocks.clanattack.language.Replacement
import rocks.clanattack.player.Player
import rocks.clanattack.player.trait.get
import rocks.clanattack.task.promise
import rocks.clanattack.util.extention.snowflake
import rocks.clanattack.player.trait.DiscordTrait as Interface

class DiscordTrait(private val player: Player) : Interface {

    override var member: Member?
        get() = player.data.get<ULong>("core.discord.member")
            ?.let { runBlocking { find<DiscordService>().guild.getMember(it.snowflake) } }
        set(value) {
            if (value != null) player.data["core.discord.member"] = value.id.value
            else player.data.remove("core.discord.member")
        }

    override fun sendMessage(key: String, replacement: Replacement.() -> Unit) = promise {
        if (member == null) throw IllegalStateException("Player is not connected to discord!")

        val message = player.communication.language.getMessage(key, replacement)
        val discord = DiscordSerializer.INSTANCE.serialize(message.asComponent())

        member!!.getDmChannel().createMessage(discord)
    }
}