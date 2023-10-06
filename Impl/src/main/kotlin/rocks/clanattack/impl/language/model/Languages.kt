package rocks.clanattack.impl.language.model

import org.jetbrains.exposed.dao.id.IdTable
import rocks.clanattack.database.CreateTable

@CreateTable
object Languages : IdTable<String>("languages") {

    override val id = varchar("iso_code", 2).entityId()
    val name = varchar("name", 255)
    val skull = text("skull")
    val enabled = bool("enabled")

}