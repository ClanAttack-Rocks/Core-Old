package rocks.clanattack.impl.minecraft.fetching

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import rocks.clanattack.entry.find
import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.impl.util.ktor.Ktor
import rocks.clanattack.task.promise
import rocks.clanattack.util.json.get
import rocks.clanattack.util.json.json
import rocks.clanattack.util.optional.Optional
import rocks.clanattack.util.optional.asOptional
import java.util.*
import rocks.clanattack.minecraft.fetching.FetchingService as Interface

@Register(definition = Interface::class, depends = [Ktor::class])
class FetchingService : ServiceImplementation(), Interface {

    private val uuidCache = mutableMapOf<String, UUID>()
    private val nameCache = mutableMapOf<UUID, String>()

    override fun getUuid(name: String) = promise {
        val lowerName = name.lowercase()
        if (lowerName in uuidCache) return@promise uuidCache[name].asOptional()

        val response = find<HttpClient>().get("https://api.mojang.com/users/profiles/minecraft/$name")
        if (!response.status.isSuccess()) return@promise Optional.empty()

        try {
            val json = json(response.bodyAsText())

            val uuid = json.get<UUID>("id") ?: return@promise Optional.empty()
            val realName = json.get<String>("name") ?: return@promise Optional.empty()

            uuidCache[lowerName] = uuid
            nameCache[uuid] = realName

            uuid
        } catch (e: IllegalArgumentException) {
            null
        }.asOptional()
    }

    override fun getName(uuid: UUID) = promise {
        if (uuid in nameCache) return@promise nameCache[uuid].asOptional()

        val response = find<HttpClient>().get("https://sessionserver.mojang.com/session/minecraft/profile/$uuid")
        if (!response.status.isSuccess()) return@promise Optional.empty()

        try {
            val json = json(response.bodyAsText())

            val name = json.get<String>("name") ?: return@promise Optional.empty()

            uuidCache[name.lowercase()] = uuid
            nameCache[uuid] = name

            name.asOptional()
        } catch (e: IllegalArgumentException) {
            Optional.empty()
        }
    }

    override fun existsUuid(uuid: UUID) = getName(uuid).map { it.isPresent }

    override fun existsName(name: String) = getUuid(name).map { it.isPresent }

}