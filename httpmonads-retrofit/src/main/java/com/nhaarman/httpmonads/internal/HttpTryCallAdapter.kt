package com.nhaarman.httpmonads.internal

import com.nhaarman.httpmonads.HttpError.NetworkError
import com.nhaarman.httpmonads.HttpTry
import com.nhaarman.httpmonads.HttpTry.Failure
import com.nhaarman.httpmonads.HttpTry.Success
import com.nhaarman.httpmonads.toHttpError
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type

/**
 * A [CallAdapter] that adapts [Call]s to [HttpTry]s.
 */
internal class HttpTryCallAdapter<R> private constructor(
    private val responseType: Type
) : CallAdapter<R, HttpTry<R>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<R>): HttpTry<R> {
        return try {
            val response = call.execute()
            when {
                responseType == Unit::class.java && response.isSuccessful -> successfulUnitResult()
                response.isSuccessfulWithBody() -> HttpTry.Success(response.body()!!)
                else -> Failure(response.toHttpError())
            }
        } catch (e: IOException) {
            Failure(
                NetworkError(
                    e
                )
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun successfulUnitResult(): HttpTry<R> {
        return Success(Unit) as HttpTry<R>
    }

    private fun <R> Response<R>.isSuccessfulWithBody() =
        code() >= 200 && code() < 300 && code() != 204 && code() != 205

    companion object {

        fun create(responseType: Type) =
            HttpTryCallAdapter<Any>(responseType)

        fun <R> createFromClass(responseType: Class<R>) =
            HttpTryCallAdapter<R>(responseType)
    }
}