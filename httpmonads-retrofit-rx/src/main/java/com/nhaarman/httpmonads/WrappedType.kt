package com.nhaarman.httpmonads

import retrofit2.*
import java.lang.reflect.*

internal class WrappedType(private val returnType: Type) : ParameterizedType {

    override fun getRawType(): Type {
        return (returnType as ParameterizedType).rawType
    }

    override fun getOwnerType(): Type {
        return (returnType as ParameterizedType).ownerType
    }

    override fun getActualTypeArguments(): Array<Type> {
        return arrayOf(object : ParameterizedType {
            override fun getRawType(): Type {
                return Response::class.java
            }

            override fun getOwnerType(): Type? {
                return null
            }

            override fun getActualTypeArguments(): Array<Type> {
                return ((returnType as ParameterizedType).actualTypeArguments[0] as ParameterizedType).actualTypeArguments
            }
        })

    }
}