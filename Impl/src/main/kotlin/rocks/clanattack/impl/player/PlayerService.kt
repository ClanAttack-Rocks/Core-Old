package rocks.clanattack.impl.player

import com.google.common.cache.CacheBuilder
import org.bukkit.OfflinePlayer
import rocks.clanattack.database.DatabaseService
import rocks.clanattack.discord.DiscordService
import rocks.clanattack.entry.find
import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.language.LanguageService
import rocks.clanattack.minecraft.fetching.FetchingService
import java.util.*
import java.util.concurrent.TimeUnit
import rocks.clanattack.player.PlayerService as Interface

@Register(
    definition = Interface::class,
    depends = [DatabaseService::class, LanguageService::class, DiscordService::class, FetchingService::class]
)
class PlayerService : ServiceImplementation(), Interface {


    private val cache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build<UUID, Player>()
    override fun get(uuid: UUID): Player {
        val cachedPlayer = cache.getIfPresent(uuid)
        if (cachedPlayer != null) return cachedPlayer

        val player = Player(uuid)
        cache.put(uuid, player)

        return player
    }

    override fun get(name: String) = find<FetchingService>().getUuid(name)
        .get()
        .map { get(it) }
        .value

    override fun get(offlinePlayer: OfflinePlayer) = get(offlinePlayer.uniqueId)


}