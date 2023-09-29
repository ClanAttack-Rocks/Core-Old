package rocks.clanattack.util.promise.exception

import rocks.clanattack.util.promise.PromiseService

/**
 * An exception thrown when all promises in a [PromiseService.any] are rejected.
 */
class AllPromiseRejectedException(val reasons: List<Throwable>, message: String) : Exception(message) {

    constructor(reasons: List<Throwable>) : this(reasons, "All promises were rejected.")

}