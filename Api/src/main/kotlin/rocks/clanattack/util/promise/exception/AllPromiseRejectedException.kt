package rocks.clanattack.util.promise.exception

import rocks.clanattack.util.promise.PromiseService

/**
 * An exception thrown when all promises in a [PromiseService.any] are rejected.
 */
class AllPromiseRejectedException(
    @Suppress("unused") val reasons: List<Throwable>,
    message: String
) : Exception(message)