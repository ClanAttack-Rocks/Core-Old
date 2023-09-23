package rocks.clanattack.impl.util.reflection

import rocks.clanattack.entry.registry
import kotlin.reflect.*
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.javaMethod

val KFunction<*>.declaringClass
    get() = this.javaMethod?.declaringClass?.kotlin ?: throw IllegalArgumentException("Function is not a method")

val KFunction<*>.qualifiedName
    get() = "${this.declaringClass.qualifiedName}#${this.name}"

fun <T : Any> KFunction<T>.omitCall(resultType: KClass<T>, vararg params: Any): T {
    if (this.parameters.size > params.size)
        throw IllegalArgumentException("Function ${this.qualifiedName} can have at most ${this.parameters.size} parameters")

    val parameters = mutableListOf<Any>()
    for (i in params.indices) {
        if (this.parameters.size <= i) break

        val param = this.parameters[i]
        if (param.type == params[i]::class.starProjectedType) {
            parameters.add(params[i])
        } else {
            throw IllegalArgumentException("Parameter ${param.name} of function ${this.qualifiedName} is not of type ${param.type}")
        }
    }

    val instance = try {
        registry.getOrCreate(this.declaringClass)
    } catch (e: Exception) {
        throw IllegalStateException("Could not create instance of ${declaringClass.qualifiedName}", e)
    }

    return try {
        val result = this.javaMethod?.invoke(instance, *parameters.toTypedArray())
        if (result == null && resultType != Void::class && resultType != Unit::class)
            throw IllegalStateException("Function ${this.qualifiedName} should return ${resultType.qualifiedName}")

        if (result != null && !resultType.isInstance(result))
            throw IllegalStateException("Function ${this.qualifiedName} should return ${resultType.qualifiedName}")

        resultType.cast(result)
    } catch (e: Exception) {
        throw IllegalStateException("Could not call function ${this.qualifiedName}", e)
    }
}

fun KFunction<*>.unitOmitCall(vararg params: Any) = this.call(Unit::class, *params)

inline fun <reified T : Any> KFunction<T>.omitCall(vararg params: Any) = this.call(T::class, *params)