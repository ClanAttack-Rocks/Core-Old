package rocks.clanattack.util.promise

/**
 * The state of a [Promise].
 */
enum class PromiseState {

    /**
     * The [Promise] is still pending.
     */
    PENDING,

    /**
     * The [Promise] has been fulfilled with a value.
     */
    FULFILLED,

    /**
     * The [Promise] has been rejected with a reason.
     */
    REJECTED;

}