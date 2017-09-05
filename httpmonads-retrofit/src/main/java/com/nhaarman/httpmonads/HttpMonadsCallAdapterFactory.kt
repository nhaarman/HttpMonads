package com.nhaarman.httpmonads

import com.nhaarman.httpmonads.internal.*
import retrofit2.*
import java.lang.reflect.*

class HttpMonadsCallAdapterFactory private constructor(
      private val supportedReturnTypes: Map<Type?, (responseType: Type) -> CallAdapter<*, *>>
) : CallAdapter.Factory() {

    override fun get(returnType: Type, annotations: Array<out Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
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
                        typeFor("org.funktionale.either.Disjunction") to DisjunctionCallAdapter.Companion::create
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
