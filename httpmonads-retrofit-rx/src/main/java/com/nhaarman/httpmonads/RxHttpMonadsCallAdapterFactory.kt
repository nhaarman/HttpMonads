package com.nhaarman.httpmonads

import com.nhaarman.httpmonads.arrow.RxSingleEitherCallAdapter
import com.nhaarman.httpmonads.internal.getParameterUpperBound
import com.nhaarman.httpmonads.internal.rawTypeFor
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class RxHttpMonadsCallAdapterFactory private constructor(
    private val delegate: HttpMonadsCallAdapterFactory,
    private val defaultScheduler: Scheduler
) : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (rawTypeFor(returnType) != Single::class.java) {
            return delegate.get(returnType, annotations, retrofit)
        }

        if (returnType !is ParameterizedType) {
            error("Return type must be parameterized")
        }

        var responseType = returnType.getParameterUpperBound(0)
        if (rawTypeFor(responseType) == HttpTry::class.java) {
            responseType = (responseType as ParameterizedType).getParameterUpperBound(0)
            return RxSingleHttpTryCallAdapter.create(
                returnType,
                responseType,
                annotations,
                retrofit,
                defaultScheduler
            )
        }

        if (rawTypeFor(responseType).name == "arrow.core.Either") {
            responseType = (responseType as ParameterizedType).getParameterUpperBound(1)
            return RxSingleEitherCallAdapter.create(
                returnType,
                responseType,
                annotations,
                retrofit,
                defaultScheduler
            )
        }

        return RxSingleCallAdapter.create(
            returnType,
            responseType,
            annotations,
            retrofit,
            defaultScheduler
        )
    }

    companion object {

        fun create(defaultScheduler: Scheduler = Schedulers.io()) = RxHttpMonadsCallAdapterFactory(
            HttpMonadsCallAdapterFactory.create(),
            defaultScheduler
        )
    }
}