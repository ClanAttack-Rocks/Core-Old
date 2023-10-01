package rocks.clanattack.util.extention

import dev.kord.common.entity.Snowflake

/**
 * Converts this [Long] to a [Snowflake].
 */
@Suppress("unused")
val Long.snowflake
    get() = Snowflake(this)