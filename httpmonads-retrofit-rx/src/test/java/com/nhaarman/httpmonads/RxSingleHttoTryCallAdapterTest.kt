package com.nhaarman.httpmonads

import com.google.common.reflect.TypeToken
import com.nhaarman.expect.expect
import com.nhaarman.httpmonads.HttpError.*
import com.nhaarman.httpmonads.HttpError.ServerError5XX.*
import com.nhaarman.httpmonads.HttpTry.*
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.Protocol.*
import okhttp3.Request.*
import org.junit.Test
import retrofit2.*
import java.io.IOException

class RxSingleHttoTryCallAdapterTest {

    @Test
    fun `Single of HttpTry of String`() {
        /* Given */
        val adapter = RxSingleHttpTryCallAdapter.create(type<Single<HttpTry<String>>>(), String::class.java, emptyArray(), retrofit, Schedulers.io())
        val call: Call<Any> = successCall("Test", 200)

        /* When */
        val result = adapter.adapt(call).blockingGet()

        /* Then */
        expect(result).toBe(Success("Test"))
    }

    @Test
    fun `error Single of HttpTry of String`() {
        /* Given */
        val adapter = RxSingleHttpTryCallAdapter.create(type<Single<HttpTry<String>>>(), String::class.java, emptyArray(), retrofit, Schedulers.io())
        val call: Call<Any> = errorCall(500)

        /* When */
        val result = adapter.adapt(call).blockingGet()

        /* Then */
        expect(result).toBe(Failure(InternalServerError500))
    }

    @Test
    fun `IOException Single of HttpTry of String`() {
        /* Given */
        val adapter = RxSingleHttpTryCallAdapter.create(type<Single<HttpTry<String>>>(), String::class.java, emptyArray(), retrofit, Schedulers.io())
        val e = IOException("Test")
        val call: Call<Any> = networkErrorCall(e)

        /* When */
        val result = adapter.adapt(call).blockingGet()

        /* Then */
        expect(result).toBe(Failure(NetworkError(e)))
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