package com.nhaarman.httpmonads

import com.google.common.reflect.TypeToken
import com.nhaarman.expect.expect
import com.nhaarman.expect.expectErrorWithMessage
import com.nhaarman.httpmonads.arrow.RxSingleEitherCallAdapter
import com.nhaarman.httpmonads.funktionale.RxSingleDisjunctionCallAdapter
import io.reactivex.Single
import org.junit.Test
import retrofit2.Retrofit.Builder

class RxHttpMonadsCallAdapterFactoryTest {

    val factory = RxHttpMonadsCallAdapterFactory.create()

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
            factory.get(Single::class.java, emptyArray(), retrofit)
        }
    }

    @Test
    fun `adapter for Single`() {
        /* When */
        val result = factory.get(type<Single<String>>(), emptyArray(), retrofit)

        /* Then */
        expect(result).toBeInstanceOf<RxSingleCallAdapter<*>>()
    }

    @Test
    fun `adapter for Single of HttpTry`() {
        /* When */
        val result = factory.get(type<Single<HttpTry<String>>>(), emptyArray(), retrofit)

        /* Then */
        expect(result).toBeInstanceOf<RxSingleHttpTryCallAdapter<*>>()
    }

    @Test
    fun `adapter for Single of Disjunction`() {
        /* When */
        val result = factory.get(type<Single<org.funktionale.either.Disjunction<HttpError, String>>>(), emptyArray(), retrofit)

        /* Then */
        expect(result).toBeInstanceOf<RxSingleDisjunctionCallAdapter<*>>()
    }

    @Test
    fun `adapter for Single of Either`() {
        /* When */
        val result = factory.get(type<Single<arrow.core.Either<HttpError, String>>>(), emptyArray(), retrofit)

        /* Then */
        expect(result).toBeInstanceOf<RxSingleEitherCallAdapter<*>>()
    }

    val retrofit = Builder().baseUrl("http://localhost").build()
    private inline fun <reified T> type() = object : TypeToken<T>() {}.type
}