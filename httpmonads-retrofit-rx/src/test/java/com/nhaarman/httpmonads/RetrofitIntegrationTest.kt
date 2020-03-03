package com.nhaarman.httpmonads

import com.nhaarman.expect.expect
import io.reactivex.Single
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol.HTTP_1_0
import okhttp3.ResponseBody
import org.jetbrains.annotations.TestOnly
import org.junit.Test
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

class RetrofitIntegrationTest {

    private val service = Retrofit.Builder()
        .baseUrl("https://localhost")
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(RxHttpMonadsCallAdapterFactory.create())
        .client(OkHttpClient.Builder()
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Chain): okhttp3.Response {
                    return okhttp3.Response.Builder()
                        .request(chain.request())
                        .protocol(HTTP_1_0)
                        .code(200)
                        .message("OK")
                        .body(ResponseBody.create(MediaType.parse("text/plain"),"success!"))
                        .build()
                }
            })
            .build())
        .build()
        .create(Service::class.java)

    @Test
    fun `Call of String can be executed`( ){
        /* When */
        val result = service.callResult().execute().body()

        /* Then */
        expect(result).toBe("success!")
    }

    @Test
    fun `Single of String can be executed`( ){
        /* When */
        val result = service.singleResult().blockingGet()

        /* Then */
        expect(result).toBe("success!")
    }

    @Test
    fun `Single of HttpTry can be executed`( ){
        /* When */
        val result = service.singleHttpTryResult().blockingGet()

        /* Then */
        expect(result).toBe(HttpTry.success("success!"))
    }

    interface Service {

        @GET("/")
        fun callResult(): Call<String>

        @GET("/")
        fun singleResult(): Single<String>

        @GET("/")
        fun singleHttpTryResult() : Single<HttpTry<String>>
    }
}