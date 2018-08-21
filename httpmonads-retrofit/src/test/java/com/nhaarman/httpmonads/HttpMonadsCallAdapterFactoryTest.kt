package com.nhaarman.httpmonads

import arrow.core.Either
import com.google.common.reflect.TypeToken
import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import org.junit.Test
import retrofit2.Retrofit.Builder

class HttpMonadsCallAdapterFactoryTest {

    val factory = HttpMonadsCallAdapterFactory.create()

    @Test
    fun `a non supported type returns null`() {
        /* When */
        val result = factory.get(String::class.java, emptyArray(), retrofit)

        /* Then */
        expect(result).toBeNull()
    }

    @Test
    fun `a non-parameterized type throws`() {
        /* Expect */
        expectErrorWithMessage("Return type must be parameterized") on {

            /* When */
            factory.get(HttpTry::class.java, emptyArray(), retrofit)
        }
    }

    @Test
    fun `adapter for HttpTry`() {
        /* When */
        val result = factory.get(type<HttpTry<String>>(), emptyArray(), retrofit)

        /* Then */
        expect(result).toBeInstanceOf<HttpTryCallAdapter<*>>()
    }

    @Test
    fun `adapter for Either`() {
        /* When */
        val result = factory.get(type<Either<Any, Any>>(), emptyArray(), retrofit)

        /* Then */
        expect(result).toBeInstanceOf<EitherCallAdapter<*>>()
    }

    val retrofit = Builder().baseUrl("http://localhost").build()

    private inline fun <reified T> type() = object : TypeToken<T>() {}.type
}