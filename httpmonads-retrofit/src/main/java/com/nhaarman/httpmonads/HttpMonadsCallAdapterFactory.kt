package com.nhaarman.httpmonads

import com.nhaarman.httpmonads.arrow.EitherCallAdapter
import com.nhaarman.httpmonads.internal.CallHttpTryCallAdapter
import com.nhaarman.httpmonads.internal.HttpTryCallAdapter
import com.nhaarman.httpmonads.internal.getParameterUpperBound
import com.nhaarman.httpmonads.internal.rawTypeFor
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class HttpMonadsCallAdapterFactory private constructor() : CallAdapter.Factory() {

    private val supportedTypes = listOf(
        Call::class.java,
        HttpTry::class.java,
        typeFor("arrow.core.Either")
    )

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        val rawReturnType = rawTypeFor(returnType)
        if (rawReturnType !in supportedTypes) {
            return null
        }

        if (returnType !is ParameterizedType) {
            error("Return type must be parameterized")
        }

        if (rawReturnType == Call::class.java) {
            val responseType = (returnType as ParameterizedType).getParameterUpperBound(0)
            if (rawTypeFor(responseType) == HttpTry::class.java) {
                val actualResponseType =
                    (responseType as ParameterizedType).getParameterUpperBound(0)
                return CallHttpTryCallAdapter.create(actualResponseType)
            }
        }


        if (rawReturnType == HttpTry::class.java) {
            val responseType = returnType.getParameterUpperBound(0)
            return HttpTryCallAdapter.create(responseType)
        }

        if (rawReturnType == typeFor("arrow.core.Either")) {
            val responseType = returnType.getParameterUpperBound(0)
            return EitherCallAdapter.create(responseType)
        }

        return null
    }

    companion object {

        fun create(): HttpMonadsCallAdapterFactory {
            return HttpMonadsCallAdapterFactory()
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
