package com.nhaarman.httpmonads

import arrow.core.Either
import com.nhaarman.httpmonads.HttpError.NetworkError
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type

/**
 * A [CallAdapter] that adapts [Call]s to [Either]s.
 */
internal class EitherCallAdapter<R> private constructor(
      private val responseType: Type
) : CallAdapter<R, Either<HttpError, R>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<R>): Either<HttpError, R> {
        return try {
            val response = call.execute()
            when {
                responseType == Unit::class.java && response.isSuccessful -> successfulUnitResult()
                response.isSuccessfulWithBody() -> Either.right(response.body()!!)
                else -> Either.left(response.toHttpError())
            }
        } catch (e: IOException) {
            Either.left(NetworkError(e))
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun successfulUnitResult(): Either<HttpError, R> {
        return Either.right(Unit) as Either<HttpError, R>
    }

    private fun <R> Response<R>.isSuccessfulWithBody() =
          code() >= 200 && code() < 300 && code() != 204 && code() != 205

    companion object {

        fun create(responseType: Type) = EitherCallAdapter<Any>(responseType)
        fun <R> createFromClass(responseType: Class<R>) = EitherCallAdapter<R>(responseType)
    }
}