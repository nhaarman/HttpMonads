package com.nhaarman.httpmonads

import com.google.common.reflect.TypeToken
import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.Protocol.HTTP_1_1
import okhttp3.Request
import org.junit.Test
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit.Builder
import java.io.IOException

class RxSingleCallAdapterTest {

    @Test
    fun `Single of String`() {
        /* Given */
        val adapter = RxSingleCallAdapter.create(type<Single<String>>(), String::class.java, emptyArray(), retrofit, Schedulers.io())
        val call: Call<Any> = successCall("Test", 200)

        /* When */
        val result = adapter.adapt(call).blockingGet()

        /* Then */
        expect(result).toBe("Test")
    }

    @Test
    fun `error Single of String`() {
        /* Given */
        val adapter = RxSingleCallAdapter.create(type<Single<String>>(), String::class.java, emptyArray(), retrofit, Schedulers.io())
        val call: Call<Any> = errorCall(500)

        /* Expect */
        expectErrorWithMessage("500") on {

            /* When */
            adapter.adapt(call).blockingGet()
        }
    }

    @Test
    fun `IOException Single of String`() {
        /* Given */
        val adapter = RxSingleCallAdapter.create(type<Single<String>>(), String::class.java, emptyArray(), retrofit, Schedulers.io())
        val e = IOException("Test")
        val call: Call<Any> = networkErrorCall(e)

        /* Expect */
        expectErrorWithMessage("Test") on {

            /* When */
            adapter.adapt(call).blockingGet()
        }
    }

    val retrofit = Builder().baseUrl("http://localhost").build()
    private inline fun <reified T> type() = object : TypeToken<T>() {}.type

    private fun <T> successCall(body: T?, code: Int): Call<T> {
        val response = Response.success(
              body,
              okhttp3.Response.Builder()
                    .request(Request.Builder()
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