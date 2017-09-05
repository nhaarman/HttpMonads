package com.nhaarman.httpmonads

import com.nhaarman.expect.expect
import com.nhaarman.httpmonads.HttpError.NetworkError
import com.nhaarman.httpmonads.HttpError.ServerError5XX.InternalServerError500
import com.nhaarman.mockito_kotlin.*
import okhttp3.Protocol.HTTP_1_1
import okhttp3.Request.Builder
import org.funktionale.either.Disjunction
import org.junit.Test
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

class DisjunctionCallAdapterTest {

    @Test
    fun `adapting a 200 success response`() {
        /* Given */
        val adapter = DisjunctionCallAdapter.createFromClass(String::class.java)
        val call = successCall("Test", 200)

        /* When */
        val result = adapter.adapt(call)

        /* Then */
        expect(result).toBe(Disjunction.right("Test"))
    }

    @Test
    fun `adapting a 200 success response for Unit`() {
        /* Given */
        val adapter = DisjunctionCallAdapter.createFromClass(Unit::class.java)
        val call = successCall<Unit>(null, 200)

        /* When */
        val result = adapter.adapt(call)

        /* Then */
        expect(result).toBe(Disjunction.right(Unit))
    }

    @Test
    fun `adapting a 204 success response for Unit`() {
        /* Given */
        val adapter = DisjunctionCallAdapter.createFromClass(Unit::class.java)
        val call = successCall<Unit>(null, 204)

        /* When */
        val result = adapter.adapt(call)

        /* Then */
        expect(result).toBe(Disjunction.right(Unit))
    }

    @Test
    fun `adapting a 205 success response for Unit`() {
        /* Given */
        val adapter = DisjunctionCallAdapter.createFromClass(Unit::class.java)
        val call = successCall<Unit>(null, 205)

        /* When */
        val result = adapter.adapt(call)

        /* Then */
        expect(result).toBe(Disjunction.right(Unit))
    }

    @Test
    fun `adapting a 500 failed response`() {
        /* Given */
        val adapter = DisjunctionCallAdapter.createFromClass(String::class.java)
        val call = errorCall<String>(500)

        /* When */
        val result = adapter.adapt(call)

        /* Then */
        expect(result).toBe(Disjunction.left(InternalServerError500))
    }

    @Test
    fun `adapting an IOException`() {
        /* Given */
        val adapter = DisjunctionCallAdapter.createFromClass(String::class.java)
        val exception = IOException("Test")
        val call = networkErrorCall<String>(exception)

        /* When */
        val result = adapter.adapt(call)

        /* Then */
        expect(result).toBe(Disjunction.left(NetworkError(exception)))
    }

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
        }
    }

    private fun <T> errorCall(code: Int): Call<T> {
        val response = Response.error<T>(code, mock())

        return mock {
            on { execute() } doReturn response
        }
    }

    private fun <T> networkErrorCall(e: IOException): Call<T> {
        return mock {
            on { execute() } doThrow e
        }
    }
}