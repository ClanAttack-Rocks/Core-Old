package rocks.clanattack.impl.discord

import dev.kord.common.entity.ActivityType
import dev.kord.common.entity.DiscordBotActivity
import dev.kord.common.entity.PresenceStatus
import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.on
import dev.kord.gateway.ALL
import dev.kord.gateway.DiscordPresence
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import rocks.clanattack.entry.find
import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.setting.SettingService
import rocks.clanattack.setting.get
import rocks.clanattack.task.TaskService
import rocks.clanattack.task.detached
import rocks.clanattack.util.extention.snowflake
import rocks.clanattack.util.promise.PromiseService
import rocks.clanattack.discord.DiscordService as Interface

@Register(definition = Interface::class, depends = [SettingService::class, TaskService::class, PromiseService::class])
class DiscordService : ServiceImplementation(), Interface {

    private var _kord: Kord? = null
    private var _guild: Guild? = null

    override val kord: Kord
        get() = _kord ?: throw IllegalStateException("DiscordService not ready yet!")
    override val guild: Guild
        get() = _guild ?: throw IllegalStateException("DiscordService not ready yet!")

    override fun enable() {
        find<SettingService>().registerSetting("core.discord.token", "token")
        find<SettingService>().registerSetting("core.discord.guild", 0)

        val promise = find<PromiseService>().create<Unit>()
        detached {
            try {
                _kord = Kord(
                    find<SettingService>().get<String>("core.discord.token")
                        ?: throw IllegalStateException("No token set!")
                )

                kord.on<ReadyEvent> {
                    _guild = kord.getGuild(
                        find<SettingService>().get<Long>("core.discord.guild")?.snowflake
                            ?: throw IllegalStateException("Bot is not on the main guild!")
                    )
                    promise.fulfill(Unit)
                }

                kord.login {
                    presence = DiscordPresence(
                        status = PresenceStatus.Online,
                        afk = false,
                        game = DiscordBotActivity(
                            name = "ClanAttack",
                            type = ActivityType.Watching
                        )
                    )

                    @OptIn(PrivilegedIntent::class)
                    intents += Intents.ALL
                }
            } catch (e: Exception) {
                promise.reject(e)
            }
        }

        promise.get()
    }

    override fun disable() = detached {
        _kord?.shutdown()
        _kord = null
        _guild = null
    }

}