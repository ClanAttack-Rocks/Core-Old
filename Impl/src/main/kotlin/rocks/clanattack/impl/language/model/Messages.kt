package rocks.clanattack.impl.language.model

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import rocks.clanattack.database.CreateTable

@CreateTable(1)
object Messages : IntIdTable("messages") {

    val language = reference(
        "language",
        Languages,
        onUpdate = ReferenceOption.CASCADE,
        onDelete = ReferenceOption.CASCADE
    )
    val key = varchar("key", 255)
    val value = text("value")

}