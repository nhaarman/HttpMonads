package com.nhaarman.httpmonads

import com.nhaarman.httpmonads.arrow.EitherCallAdapter
import com.nhaarman.httpmonads.funktionale.DisjunctionCallAdapter
import com.nhaarman.httpmonads.internal.getParameterUpperBound
import com.nhaarman.httpmonads.internal.rawTypeFor
import okhttp3.Request
import retrofit2.*
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

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
                    //Call::class.java to ResultAdapter.Companion::create,
                    HttpTry::class.java to HttpTryCallAdapter.Companion::create,
                    typeFor("arrow.core.Either") to EitherCallAdapter.Companion::create,
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

abstract class CallDelegate<TIn, TOut>(
    protected val proxy: Call<TIn>
) : Call<TOut> {

    override fun execute(): Response<TOut> = throw NotImplementedError()
    override final fun enqueue(callback: Callback<TOut>) = enqueueImpl(callback)
    override final fun clone(): Call<TOut> = cloneImpl()

    override fun cancel() = proxy.cancel()
    override fun request(): Request = proxy.request()
    override fun isExecuted() = proxy.isExecuted
    override fun isCanceled() = proxy.isCanceled

    abstract fun enqueueImpl(callback: Callback<TOut>)
    abstract fun cloneImpl(): Call<TOut>
}

class ResultAdapter(
    private val type: Type
) : CallAdapter<Type, Call<HttpTry<Type>>> {

    override fun responseType() = type
    override fun adapt(call: Call<Type>): Call<HttpTry<Type>> = ResultCall(type, call)

    companion object {

        fun create(responseType: Type) = ResultAdapter(responseType)
    }

    class ResultCall<T>(
        private val responseType: Type,
        proxy: Call<T>
    ) : CallDelegate<T, HttpTry<T>>(proxy) {

        override fun enqueueImpl(callback: Callback<HttpTry<T>>) = proxy.enqueue(object : Callback<T> {

            override fun onResponse(call: Call<T>, response: Response<T>) {
                val result = when {
                    responseType == Unit::class.java && response.isSuccessful -> successfulUnitResult()
                    response.isSuccessfulWithBody() -> HttpTry.Success(response.body()!!)
                    else -> HttpTry.Failure(response.toHttpError())
                }

                callback.onResponse(this@ResultCall, Response.success(result))
            }

            @Suppress("UNCHECKED_CAST")
            private fun successfulUnitResult(): HttpTry<T> {
                return HttpTry.Success(Unit) as HttpTry<T>
            }

            private fun <R> Response<R>.isSuccessfulWithBody(): Boolean {
                return code() in 200..299 && code() != 204 && code() != 205
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                val result = if (t is IOException) {
                    HttpTry.Failure(HttpError.NetworkError(t))
                } else {
                    throw t
                }

                callback.onResponse(this@ResultCall, Response.success(result))
            }
        })

        override fun cloneImpl() = ResultCall(responseType, proxy.clone())
    }

}