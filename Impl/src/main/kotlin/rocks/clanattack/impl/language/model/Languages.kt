package rocks.clanattack.impl.language.model

import org.jetbrains.exposed.dao.id.IdTable
import rocks.clanattack.database.CreateTable

@CreateTable(0)
object Languages : IdTable<String>("languages") {

    override val id = varchar("iso_code", 2).entityId()
    val name = varchar("name", 255)
    val skull = text("skull")
    val enabled = bool("enabled")

    override val primaryKey = PrimaryKey(id)

}