package com.nhaarman.httpmonads

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.nhaarman.httpmonads.HttpError.NetworkError
import io.reactivex.Scheduler
import io.reactivex.Single
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type

internal class RxSingleHttpTryCallAdapter<R>(
      private val responseType: Type,
      private val rxDelegate: CallAdapter<R, Single<*>>
) : CallAdapter<R, Single<*>> {

    override fun adapt(call: Call<R>): Single<*> {
        return rxDelegate.adapt(call)
              .map { HttpTry.success(it) }
              .onErrorResumeNext { t ->
                  when (t) {
                      is HttpException -> Single.just(HttpTry.failure(t.response().toHttpError()))
                      is IOException -> Single.just(HttpTry.failure(NetworkError(t)))
                      else -> Single.error(t)
                  }
              }
    }

    override fun responseType(): Type {
        return responseType
    }

    companion object {

        fun create(
              returnType: Type,
              responseType: Type,
              annotations: Array<out Annotation>,
              retrofit: Retrofit,
              scheduler: Scheduler
        ): RxSingleHttpTryCallAdapter<Any> {
            val rxDelegate = rxDelegate(scheduler, returnType, annotations, retrofit)

            return RxSingleHttpTryCallAdapter(
                  responseType,
                  rxDelegate
            )
        }

        @Suppress("UNCHECKED_CAST")
        private fun rxDelegate(scheduler: Scheduler, returnType: Type, annotations: Array<out Annotation>, retrofit: Retrofit): CallAdapter<Any, Single<*>> {
            return RxJava2CallAdapterFactory.createWithScheduler(scheduler).get(returnType, annotations, retrofit) as CallAdapter<Any, Single<*>>
        }
    }
}