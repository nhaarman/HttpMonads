package com.nhaarman.httpmonads.internal

import java.lang.reflect.*


fun ParameterizedType.getParameterUpperBound(index: Int): Type {
    if (index < 0 || index >= actualTypeArguments.size) {
        throw IllegalArgumentException(
              "Index $index not in range [0,${actualTypeArguments.size}) for $this")
    }
    val paramType = actualTypeArguments[index]
    if (paramType is WildcardType) {
        return paramType.upperBounds[0]
    }
    return paramType
}

fun rawTypeFor(type: Type): Class<*> {
    return when (type) {
        is Class<*> -> {
            // Type is a normal class.
            type
        }
        is ParameterizedType -> {
            type.rawType as? Class<*> ?: throw IllegalArgumentException()
        }
        is GenericArrayType -> {
            val componentType = type.genericComponentType
            java.lang.reflect.Array.newInstance(rawTypeFor(componentType), 0).javaClass
        }
        is TypeVariable<*> -> {
            // We could use the variable's bounds, but that won't work if there are multiple. Having a raw
            // this that's more general than necessary is okay.
            Any::class.java
        }
        is WildcardType -> {
            rawTypeFor(type.upperBounds[0])
        }
        else -> error("Expected a Class, ParameterizedType, or GenericArrayType, but <$type> is of type ${type.javaClass.name}")
    }
}
