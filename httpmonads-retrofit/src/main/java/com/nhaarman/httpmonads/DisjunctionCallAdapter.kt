package com.nhaarman.httpmonads

import com.nhaarman.httpmonads.HttpError.NetworkError
import org.funktionale.either.Disjunction
import retrofit2.*
import java.io.IOException
import java.lang.reflect.Type

/**
 * A [CallAdapter] that adapts [Call]s to [Disjunction]s.
 */
internal class DisjunctionCallAdapter<R> private constructor(
      private val responseType: Type
) : CallAdapter<R, Disjunction<HttpError, R>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<R>): Disjunction<HttpError, R> {
        return try {
            val response = call.execute()
            when {
                responseType == Unit::class.java && response.isSuccessful -> successfulUnitResult()
                response.isSuccessfulWithBody() -> Disjunction.right(response.body()!!)
                else -> Disjunction.left(response.toHttpError())
            }
        } catch (e: IOException) {
            Disjunction.left(NetworkError(e))
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun successfulUnitResult(): Disjunction<HttpError, R> {
        return Disjunction.right(Unit) as Disjunction<HttpError, R>
    }

    private fun <R> Response<R>.isSuccessfulWithBody() =
          code() >= 200 && code() < 300 && code() != 204 && code() != 205

    companion object {

        fun create(responseType: Type) = DisjunctionCallAdapter<Any>(responseType)
        fun <R> createFromClass(responseType: Class<R>) = DisjunctionCallAdapter<R>(responseType)
    }
}