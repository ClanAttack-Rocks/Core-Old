package rocks.clanattack.impl.minecraft.command.model

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import rocks.clanattack.database.CreateTable

@CreateTable(0)
object Commands : IdTable<String>("commands") {

    override val id = varchar("command", 255).entityId()
    val permission = varchar("permission", 255)
    val aliases = text("aliases")

    override val primaryKey = PrimaryKey(id)

}