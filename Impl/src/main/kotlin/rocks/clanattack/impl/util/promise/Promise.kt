package rocks.clanattack.impl.util.promise

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import rocks.clanattack.entry.find
import rocks.clanattack.impl.task.TaskService
import rocks.clanattack.util.promise.PromiseProvider
import rocks.clanattack.util.promise.PromiseResult
import rocks.clanattack.util.promise.PromiseService
import rocks.clanattack.util.promise.PromiseState
import rocks.clanattack.util.promise.exception.PromiseTimeoutException
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration
import rocks.clanattack.util.promise.Promise as Interface

class Promise<T : Any>(
    @Volatile override var state: PromiseState = PromiseState.PENDING,
    private var value: T? = null,
    private var reason: Throwable? = null
) : PromiseProvider<T> {

    private val lock = Any()

    private val callback = Collections.synchronizedList(mutableListOf<(PromiseResult<T>) -> Unit>())

    override fun then(onFulfill: (T) -> Unit): Interface<T> = when (state) {
        PromiseState.PENDING -> {
            val promise = Promise<T>()

            callback.add {
                when (it.state) {
                    PromiseState.FULFILLED -> {
                        try {
                            onFulfill(it.value!!)
                            promise.fulfill(it.value!!)
                        } catch (e: Exception) {
                            promise.reject(e)
                        }
                    }

                    PromiseState.REJECTED -> {
                        try {
                            promise.reject(it.reason!!)
                        } catch (e: Exception) {
                            promise.reject(e)
                        }
                    }

                    else -> throw IllegalStateException("Promise is still pending.")
                }
            }

            promise
        }

        PromiseState.FULFILLED -> {
            try {
                onFulfill(value!!)
                find<PromiseService>().fulfill(value!!)
            } catch (e: Exception) {
                find<PromiseService>().reject(e)
            }
        }

        PromiseState.REJECTED -> find<PromiseService>().reject(reason!!)
    }

    override fun then(onFulfill: (T) -> Unit, onReject: (Throwable) -> Unit): Interface<T> = when (state) {
        PromiseState.PENDING -> {
            val promise = Promise<T>()

            callback.add {
                when (it.state) {
                    PromiseState.FULFILLED -> {
                        try {
                            onFulfill(it.value!!)
                            promise.fulfill(it.value!!)
                        } catch (e: Exception) {
                            promise.reject(e)
                        }
                    }

                    PromiseState.REJECTED -> {
                        try {
                            onReject(it.reason!!)
                            promise.fulfill(it.value!!)
                        } catch (e: Exception) {
                            promise.reject(e)
                        }
                    }

                    else -> throw IllegalStateException("Promise is still pending.")
                }
            }

            promise
        }

        PromiseState.FULFILLED -> {
            try {
                onFulfill(value!!)
                find<PromiseService>().fulfill(value!!)
            } catch (e: Exception) {
                find<PromiseService>().reject(e)
            }
        }

        PromiseState.REJECTED -> {
            try {
                onReject(reason!!)
                find<PromiseService>().fulfill(value!!)
            } catch (e: Exception) {
                find<PromiseService>().reject(e)
            }
        }
    }

    override fun <U : Any> map(onFulfill: (T) -> U): Interface<U> = when (state) {
        PromiseState.PENDING -> {
            val promise = Promise<U>()

            callback.add {
                when (it.state) {
                    PromiseState.FULFILLED -> {
                        try {
                            val result = onFulfill(it.value!!)
                            promise.fulfill(result)
                        } catch (e: Exception) {
                            promise.reject(e)
                        }
                    }

                    PromiseState.REJECTED -> {
                        try {
                            promise.reject(it.reason!!)
                        } catch (e: Exception) {
                            promise.reject(e)
                        }
                    }

                    else -> throw IllegalStateException("Promise is still pending.")
                }
            }

            promise
        }

        PromiseState.FULFILLED -> {
            try {
                val result = onFulfill(value!!)
                find<PromiseService>().fulfill(result)
            } catch (e: Exception) {
                find<PromiseService>().reject(e)
            }
        }

        PromiseState.REJECTED -> find<PromiseService>().reject(reason!!)
    }

    override fun timeout(duration: Duration): Interface<T> = when (state) {
        PromiseState.PENDING -> {
            val promise = Promise<T>()

            val task = find<TaskService>().execute(detached = true, delay = duration) {
                if (!isSettled) promise.reject(PromiseTimeoutException("Promise timed out after $duration"))
            }

            callback.add { result ->
                if (promise.isSettled) return@add

                when (result.state) {
                    PromiseState.FULFILLED -> {
                        task.cancel()
                        promise.fulfill(result.value!!)
                    }

                    PromiseState.REJECTED -> {
                        task.cancel()
                        promise.reject(result.reason!!)
                    }

                    else -> throw IllegalStateException("Promise is still pending.")
                }
            }


            promise
        }

        PromiseState.FULFILLED -> find<PromiseService>().fulfill(value!!)
        PromiseState.REJECTED -> find<PromiseService>().reject(reason!!)
    }

    override fun orElse(value: T): Interface<T> = when (state) {
        PromiseState.PENDING -> {
            val promise = Promise<T>()

            callback.add {
                when (it.state) {
                    PromiseState.FULFILLED -> {
                        try {
                            promise.fulfill(it.value!!)
                        } catch (e: Exception) {
                            promise.reject(e)
                        }
                    }

                    PromiseState.REJECTED -> {
                        try {
                            promise.fulfill(value)
                        } catch (e: Exception) {
                            promise.reject(e)
                        }
                    }

                    else -> throw IllegalStateException("Promise is still pending.")
                }
            }

            promise
        }

        PromiseState.FULFILLED -> find<PromiseService>().fulfill(this.value!!)
        PromiseState.REJECTED -> find<PromiseService>().fulfill(value)
    }

    override fun catch(onReject: (Throwable) -> Unit): Interface<T> = when (state) {
        PromiseState.PENDING -> {
            val promise = Promise<T>()

            callback.add {
                when (it.state) {
                    PromiseState.FULFILLED -> promise.fulfill(it.value!!)
                    PromiseState.REJECTED -> {
                        try {
                            onReject(it.reason!!)
                            promise.fulfill(it.value!!)
                        } catch (e: Exception) {
                            promise.reject(e)
                        }
                    }

                    else -> throw IllegalStateException("Promise is still pending.")
                }
            }

            promise
        }

        PromiseState.FULFILLED -> find<PromiseService>().fulfill(value!!)
        PromiseState.REJECTED -> {
            try {
                onReject(reason!!)
                find<PromiseService>().fulfill(value!!)
            } catch (e: Exception) {
                find<PromiseService>().reject(e)
            }
        }
    }

    override fun catchMap(onReject: (Throwable) -> Throwable): Interface<T> = when (state) {
        PromiseState.PENDING -> {
            val promise = Promise<T>()

            callback.add {
                when (it.state) {
                    PromiseState.FULFILLED -> promise.fulfill(it.value!!)
                    PromiseState.REJECTED -> {
                        try {
                            val result = onReject(it.reason!!)
                            promise.reject(result)
                        } catch (e: Exception) {
                            promise.reject(e)
                        }
                    }

                    else -> throw IllegalStateException("Promise is still pending.")
                }
            }

            promise
        }

        PromiseState.FULFILLED -> find<PromiseService>().fulfill(value!!)
        PromiseState.REJECTED -> {
            try {
                val result = onReject(reason!!)
                find<PromiseService>().reject(result)
            } catch (e: Exception) {
                find<PromiseService>().reject(e)
            }
        }
    }

    override fun finally(onFinally: (PromiseResult<T>) -> Unit): Interface<T> = when (state) {
        PromiseState.PENDING -> {
            val promise = Promise<T>()

            callback.add {
                try {
                    onFinally(it)
                    promise.fulfill(it.value!!)
                } catch (e: Exception) {
                    promise.reject(e)
                }
            }

            promise
        }

        PromiseState.FULFILLED -> {
            try {
                onFinally(PromiseResult(PromiseState.FULFILLED, value, null))
                find<PromiseService>().fulfill(value!!)
            } catch (e: Exception) {
                find<PromiseService>().reject(e)
            }
        }

        PromiseState.REJECTED -> {
            try {
                onFinally(PromiseResult(PromiseState.REJECTED, null, reason))
                find<PromiseService>().fulfill(value!!)
            } catch (e: Exception) {
                find<PromiseService>().reject(e)
            }
        }
    }

    override suspend fun await(): T = when (state) {
        PromiseState.PENDING -> suspendCancellableCoroutine { continuation ->
            then(
                onFulfill = { continuation.resume(it) },
                onReject = { continuation.resumeWithException(it) }
            )
        }

        PromiseState.FULFILLED -> value!!
        PromiseState.REJECTED -> throw reason!!
    }

    override fun get(): T = when (state) {
        PromiseState.PENDING -> {
            runBlocking { await() }
        }
        PromiseState.FULFILLED -> value!!
        PromiseState.REJECTED -> throw reason!!
    }

    @Synchronized
    override fun fulfill(value: T) {
        if (isSettled) throw IllegalStateException("Promise is already settled.")

        state = PromiseState.FULFILLED
        this.value = value

        val promiseResult = PromiseResult(state, value, null)
        synchronized(lock) {
            callback.forEach { it(promiseResult) }
            callback.clear()
        }
    }

    @Synchronized
    override fun reject(reason: Throwable) {
        if (isSettled) throw IllegalStateException("Promise is already settled.")

        state = PromiseState.REJECTED
        this.reason = reason

        val promiseResult = PromiseResult<T>(state, null, reason)
        synchronized(lock) {
            callback.forEach { it(promiseResult) }
            callback.clear()
        }
    }

}