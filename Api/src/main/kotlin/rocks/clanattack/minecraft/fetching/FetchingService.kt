package rocks.clanattack.minecraft.fetching

import rocks.clanattack.entry.service.Service
import java.util.UUID

/**
 * The [FetchingService] is used to map uuids or names from mojang.
 */
@Suppress("unused")
interface FetchingService : Service {

    /**
     * Gets the uuid of a player by its [name].
     */
    fun getUuid(name: String): UUID?

    /**
     * Gets the name of a player by its [uuid].
     */
    fun getName(uuid: UUID): String?

    /**
     * Checks if a player with the given [uuid] exists.
     */
    fun existsUuid(uuid: UUID): Boolean

    /**
     * Checks if a player with the given [name] exists.
     */
    fun existsName(name: String): Boolean

}