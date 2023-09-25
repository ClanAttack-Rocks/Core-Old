package rocks.clanattack.extention

import java.lang.reflect.InvocationTargetException

val Exception.invocationCause: Throwable
    get() {
        var cause: Throwable = this
        while (cause is InvocationTargetException && cause.cause != null) cause = cause.cause!!
        return cause
    }