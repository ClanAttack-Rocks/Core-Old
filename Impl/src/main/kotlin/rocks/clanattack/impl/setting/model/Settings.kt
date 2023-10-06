package rocks.clanattack.impl.setting.model

import org.jetbrains.exposed.dao.id.IntIdTable
import rocks.clanattack.database.CreateTable

@CreateTable(0)
object Settings : IntIdTable("settings") {

    val key = varchar("key", 255)
    val value = text("value")

}