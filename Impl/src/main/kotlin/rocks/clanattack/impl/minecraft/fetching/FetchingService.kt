package rocks.clanattack.impl.minecraft.fetching

import rocks.clanattack.entry.find
import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.task.TaskService
import java.util.*
import rocks.clanattack.minecraft.fetching.FetchingService as Interface

@Register(definition = Interface::class)
class FetchingService : ServiceImplementation(), Interface {

    private val uuidUrl = "https://api.mojang.com/users/profiles/minecraft/%s"
    private val nameUrl = "https://api.mojang.com/user/profile/%s"

    private val uuidCache = mutableMapOf<String, UUID>()
    private val nameCache = mutableMapOf<UUID, String>()
    override fun getUuid(name: String) = find<TaskService>().promise {
        if (name in uuidCache) return@promise uuidCache[name]

        TODO("Call the uuidUrl with the name and return the uuid (id in the returned json object), than cache it and the name")
    }

    override fun getName(uuid: UUID) = find<TaskService>().promise {
        if (uuid in nameCache) return@promise nameCache[uuid]

        TODO("Call the nameUrl with the uuid and return the name (name in the returned json object), than cache it and the uuid")
    }

    override fun existsUuid(uuid: UUID) = getName(uuid).map { it != null }

    override fun existsName(name: String) = getUuid(name).map { it != null }

}