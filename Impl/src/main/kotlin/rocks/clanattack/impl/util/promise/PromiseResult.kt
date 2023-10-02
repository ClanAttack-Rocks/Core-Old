package rocks.clanattack.impl.util.promise

import rocks.clanattack.util.promise.PromiseState
import rocks.clanattack.util.promise.PromiseResult as Interface

data class PromiseResult<T>(
    override val state: PromiseState,
    override val value: T?,
    override val reason: Throwable?,
) : Interface<T>