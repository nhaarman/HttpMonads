package com.nhaarman.httpmonads.funktionale

import com.google.common.reflect.TypeToken
import com.nhaarman.expect.expect
import com.nhaarman.httpmonads.HttpError
import com.nhaarman.httpmonads.HttpError.NetworkError
import com.nhaarman.httpmonads.HttpError.ServerError5XX.InternalServerError500
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.Protocol.HTTP_1_1
import okhttp3.Request.Builder
import org.funktionale.either.Disjunction
import org.junit.Test
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

class RxSingleDisjunctionCallAdapterTest {

    @Test
    fun `Single of Disjunction of String`() {
        /* Given */
        val adapter = RxSingleDisjunctionCallAdapter.create(type<Single<Disjunction<HttpError, String>>>(), String::class.java, emptyArray(), retrofit, Schedulers.io())
        val call: Call<Any> = successCall("Test", 200)

        /* When */
        val result = adapter.adapt(call).blockingGet()

        /* Then */
        expect(result).toBe(Disjunction.right("Test"))
    }

    @Test
    fun `error Single of Disjunction of String`() {
        /* Given */
        val adapter = RxSingleDisjunctionCallAdapter.create(type<Single<Disjunction<HttpError, String>>>(), String::class.java, emptyArray(), retrofit, Schedulers.io())
        val call: Call<Any> = errorCall(500)

        /* When */
        val result = adapter.adapt(call).blockingGet()

        /* Then */
        expect(result).toBe(Disjunction.left(InternalServerError500))
    }

    @Test
    fun `IOException Single of Disjunction of String`() {
        /* Given */
        val adapter = RxSingleDisjunctionCallAdapter.create(type<Single<Disjunction<HttpError, String>>>(), String::class.java, emptyArray(), retrofit, Schedulers.io())
        val e = IOException("Test")
        val call: Call<Any> = networkErrorCall(e)

        /* When */
        val result = adapter.adapt(call).blockingGet()

        /* Then */
        expect(result).toBe(Disjunction.left(NetworkError(e)))
    }

    val retrofit = Retrofit.Builder().baseUrl("http://localhost").build()
    private inline fun <reified T> type() = object : TypeToken<T>() {}.type

    private fun <T> successCall(body: T?, code: Int): Call<T> {
        val response = Response.success(
              body,
              okhttp3.Response.Builder()
                    .request(Builder()
                          .url("http://localhost")
                          .build()
                    )
                    .protocol(HTTP_1_1)
                    .code(code)
                    .message("$body")
                    .build()
        )

        return mock {
            on { execute() } doReturn response
            on { clone() } doReturn it
        }
    }

    private fun <T> errorCall(code: Int): Call<T> {
        val response = Response.error<T>(code, mock())

        return mock {
            on { execute() } doReturn response
            on { clone() } doReturn it
        }
    }

    private fun <T> networkErrorCall(e: IOException): Call<T> {
        return mock {
            on { execute() } doThrow e
            on { clone() } doReturn it
        }
    }
}