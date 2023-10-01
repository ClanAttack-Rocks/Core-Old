package rocks.clanattack.minecraft.fetching

import rocks.clanattack.entry.service.Service
import rocks.clanattack.util.promise.Promise
import java.util.UUID

/**
 * The [FetchingService] is used to map uuids or names from mojang.
 */
@Suppress("unused")
interface FetchingService : Service {

    /**
     * Gets the uuid of a player by its [name].
     */
    fun getUuid(name: String): Promise<UUID?>

    /**
     * Gets the name of a player by its [uuid].
     */
    fun getName(uuid: UUID): Promise<String?>

    /**
     * Checks if a player with the given [uuid] exists.
     */
    fun existsUuid(uuid: UUID): Promise<Boolean>

    /**
     * Checks if a player with the given [name] exists.
     */
    fun existsName(name: String): Promise<Boolean>

}