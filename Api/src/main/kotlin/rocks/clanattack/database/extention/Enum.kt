package rocks.clanattack.database.extention

import org.jetbrains.exposed.sql.Table

/**
 * Creates an enumeration column.
 */
inline fun <reified T : Enum<T>> Table.enum(
    name: String,
    sql: String? = null,
) = customEnumeration(name, sql, { value ->
    if (value !is String) throw IllegalArgumentException("Value $value is not a string")
    enumValueOf<T>(value)
}, { value ->
    value.name
})