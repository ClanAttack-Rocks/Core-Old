package rocks.clanattack.impl.util.promise

import rocks.clanattack.entry.service.Register
import rocks.clanattack.entry.service.ServiceImplementation
import rocks.clanattack.util.promise.PromiseProvider
import rocks.clanattack.util.promise.PromiseResult
import rocks.clanattack.util.promise.exception.AllPromiseRejectedException
import rocks.clanattack.util.promise.PromiseService as Interface
import rocks.clanattack.util.promise.Promise as PromiseInterface

@Register(definition = Interface::class)
class PromiseService : ServiceImplementation(), Interface {
    override fun <T> create(): PromiseProvider<T> = Promise()

    override fun <T> all(vararg promises: PromiseInterface<T>): PromiseInterface<List<T>> {
        val promise = Promise<List<T>>()
        var count = promises.size
        val results = mutableListOf<T>()

        promises.forEach {
            it.then { value ->
                results.add(value)
                count--

                if (count == 0 && !promise.isSettled) promise.fulfill(results)
            }.catch { throwable ->
                if (!promise.isSettled) promise.reject(throwable)
            }
        }

        return promise
    }

    override fun <T> allSettled(vararg promises: PromiseInterface<T>): rocks.clanattack.util.promise.Promise<List<PromiseResult<T>>> {
        val promise = Promise<List<PromiseResult<T>>>()
        var count = promises.size
        val results = mutableListOf<PromiseResult<T>>()

        promises.forEach {
            it.finally { result ->
                count--
                results.add(result)

                if (count == 0 && !promise.isSettled) promise.fulfill(results)
            }
        }

        return promise
    }

    override fun <T> any(vararg promises: PromiseInterface<T>): PromiseInterface<T> {
        val promise = Promise<T>()
        var count = promises.size
        val reasons = mutableListOf<Throwable>()

        promises.forEach {
            it.then { value ->
                if (!promise.isSettled) {
                    promise.fulfill(value)
                    count = -1
                }
            }.catch { throwable ->
                if (count == -1) return@catch

                count--
                reasons.add(throwable)

                if (count == 0 && !promise.isSettled) promise.reject(
                    AllPromiseRejectedException(
                        reasons,
                        "All promises rejected."
                    )
                )
            }
        }
        
        return promise
    }

    override fun <T> race(vararg promises: PromiseInterface<T>): PromiseInterface<T> {
        val promise = Promise<T>()

        promises.forEach {
            it.then { value ->
                if (!promise.isSettled) promise.fulfill(value)
            }.catch { throwable ->
                if (!promise.isSettled) promise.reject(throwable)
            }
        }

        return promise
    }

    override fun <T> fulfill(value: T): PromiseInterface<T>  = Promise(
        state = rocks.clanattack.util.promise.PromiseState.FULFILLED,
        value = value
    )

    override fun <T> reject(reason: Throwable): PromiseInterface<T> = Promise(
        state = rocks.clanattack.util.promise.PromiseState.REJECTED,
        reason = reason
    )

}