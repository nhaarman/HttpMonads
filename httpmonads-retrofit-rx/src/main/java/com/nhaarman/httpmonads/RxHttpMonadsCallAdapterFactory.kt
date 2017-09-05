package com.nhaarman.httpmonads

import com.nhaarman.httpmonads.internal.*
import io.reactivex.*
import io.reactivex.schedulers.*
import retrofit2.*
import java.lang.reflect.*

class RxHttpMonadsCallAdapterFactory private constructor(
      private val delegate: HttpMonadsCallAdapterFactory,
      private val defaultScheduler: Scheduler
) : CallAdapter.Factory() {

    override fun get(returnType: Type, annotations: Array<out Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
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

        if (rawTypeFor(responseType).name == "org.funktionale.either.Disjunction") {
            responseType = (responseType as ParameterizedType).getParameterUpperBound(1)
            return RxSingleDisjunctionCallAdapter.create(
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