package com.nhaarman.httpmonads.internal

import com.nhaarman.httpmonads.HttpError.NetworkError
import com.nhaarman.httpmonads.HttpTry
import com.nhaarman.httpmonads.HttpTry.Failure
import com.nhaarman.httpmonads.HttpTry.Success
import com.nhaarman.httpmonads.toHttpError
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type

internal class CallHttpTryCallAdapter private constructor(
    private val responseType: Type
) : CallAdapter<Type, Call<HttpTry<Type>>> {

    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<Type>): Call<HttpTry<Type>> {
        return HttpTryCall(responseType, call)
    }

    companion object {

        fun create(responseType: Type): CallHttpTryCallAdapter {
            return CallHttpTryCallAdapter(responseType)
        }
    }

    private class HttpTryCall<T>(
        private val responseType: Type,
        private val proxy: Call<T>
    ) : Call<HttpTry<T>> {

        override fun execute(): Response<HttpTry<T>> {
            return httpTryResponseFor(proxy.execute())
        }

        override fun enqueue(callback: Callback<HttpTry<T>>) {
            proxy.enqueue(object : Callback<T> {

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    callback.onResponse(this@HttpTryCall, httpTryResponseFor(response))
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    val result = if (t is IOException) {
                        Failure(NetworkError(t))
                    } else {
                        throw t
                    }

                    callback.onResponse(this@HttpTryCall, Response.success(result))
                }
            })
        }

        private fun httpTryResponseFor(response: Response<T>): Response<HttpTry<T>> {
            val result = when {
                responseType == Unit::class.java && response.isSuccessful -> successfulUnitResult()
                response.isSuccessfulWithBody() -> Success(response.body()!!)
                else -> Failure(response.toHttpError())
            }

            return Response.success(result)
        }

        @Suppress("UNCHECKED_CAST")
        private fun successfulUnitResult(): HttpTry<T> {
            return Success(Unit) as HttpTry<T>
        }

        private fun <R> Response<R>.isSuccessfulWithBody(): Boolean {
            return code() in 200..299 && code() != 204 && code() != 205
        }

        override fun isExecuted() = proxy.isExecuted
        override fun cancel() = proxy.cancel()
        override fun isCanceled() = proxy.isCanceled
        override fun clone() = HttpTryCall(responseType, proxy.clone())
        override fun request() = proxy.request()
    }
}
