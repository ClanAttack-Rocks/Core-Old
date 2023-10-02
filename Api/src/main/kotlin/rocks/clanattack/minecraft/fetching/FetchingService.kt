package rocks.clanattack.minecraft.fetching

import rocks.clanattack.entry.service.Service
import rocks.clanattack.util.optional.Optional
import rocks.clanattack.util.promise.Promise
import java.util.UUID
import javax.swing.text.html.Option

/**
 * The [FetchingService] is used to map uuids or names from mojang.
 */
@Suppress("unused")
interface FetchingService : Service {

    /**
     * Gets the uuid of a player by its [name].
     */
    fun getUuid(name: String): Promise<Optional<UUID>>

    /**
     * Gets the name of a player by its [uuid].
     */
    fun getName(uuid: UUID): Promise<Optional<String>>

    /**
     * Checks if a player with the given [uuid] exists.
     */
    fun existsUuid(uuid: UUID): Promise<Boolean>

    /**
     * Checks if a player with the given [name] exists.
     */
    fun existsName(name: String): Promise<Boolean>

}