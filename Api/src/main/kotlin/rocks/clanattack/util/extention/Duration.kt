package rocks.clanattack.util.extention

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

val Int.ticks: Duration
    get() = (this * 20).seconds

val Long.ticks: Duration
    get() = (this * 20).seconds

val Double.ticks: Duration
    get() = (this * 20).seconds

val Duration.inWholeTicks: Long
    get() = this.inWholeSeconds * 20