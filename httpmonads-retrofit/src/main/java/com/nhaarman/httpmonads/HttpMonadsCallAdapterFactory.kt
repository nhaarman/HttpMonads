package com.nhaarman.httpmonads

import com.nhaarman.httpmonads.arrow.EitherCallAdapter
import com.nhaarman.httpmonads.internal.getParameterUpperBound
import com.nhaarman.httpmonads.internal.rawTypeFor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class HttpMonadsCallAdapterFactory private constructor(
    private val supportedReturnTypes: Map<Type?, (responseType: Type) -> CallAdapter<*, *>>
) : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (rawTypeFor(returnType) !in supportedReturnTypes) {
            return null
        }

        if (returnType !is ParameterizedType) {
            error("Return type must be parameterized")
        }

        val responseType = returnType.getParameterUpperBound(0)
        return supportedReturnTypes[rawTypeFor(returnType)]?.invoke(responseType)
    }

    companion object {

        fun create(): HttpMonadsCallAdapterFactory {
            return HttpMonadsCallAdapterFactory(
                mapOf(
                    HttpTry::class.java to HttpTryCallAdapter.Companion::create,
                    typeFor("arrow.core.Either") to EitherCallAdapter.Companion::create
                )
            )
        }

        private fun typeFor(name: String): Type? {
            return try {
                Class.forName(name)
            } catch (ignored: ClassNotFoundException) {
                null
            }
        }
    }
}
