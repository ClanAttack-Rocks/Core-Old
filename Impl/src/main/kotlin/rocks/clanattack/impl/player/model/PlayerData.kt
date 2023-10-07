package rocks.clanattack.impl.player.model

import org.jetbrains.exposed.dao.id.IntIdTable
import rocks.clanattack.database.CreateTable

@CreateTable(0)
object PlayerData : IntIdTable("player_data") {

    val player = uuid("uuid")
    val key = varchar("data_key", 255)
    val value = text("value")

}