package rocks.clanattack.util.promise.exception

import rocks.clanattack.util.promise.Promise

/**
 * Thrown when a [Promise] times out.
 */
class PromiseTimeoutException(message: String) : Exception(message)