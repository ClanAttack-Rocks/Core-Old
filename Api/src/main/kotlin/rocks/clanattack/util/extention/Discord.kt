package rocks.clanattack.util.extention

import dev.kord.common.entity.Snowflake

/**
 * Converts this [Long] to a [Snowflake].
 */
val Long.snowflake
    get() = Snowflake(this)