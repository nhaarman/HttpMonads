package com.nhaarman.httpmonads

import com.jakewharton.retrofit2.adapter.rxjava2.*
import io.reactivex.*
import retrofit2.*
import java.lang.reflect.*

internal class RxSingleCallAdapter<R>(
      private val responseType: Type,
      private val rxDelegate: CallAdapter<R, Single<*>>
) : CallAdapter<R, Single<*>> {

    override fun adapt(call: Call<R>): Single<*> {
        return rxDelegate.adapt(call)
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
        ): RxSingleCallAdapter<Any> {
            val rxDelegate = rxDelegate(scheduler, returnType, annotations, retrofit)

            return RxSingleCallAdapter(
                  responseType,
                  rxDelegate
            )
        }

        @Suppress("UNCHECKED_CAST")
        private fun rxDelegate(scheduler: Scheduler, returnType: Type, annotations: Array<out Annotation>, retrofit: Retrofit): CallAdapter<Any, Single<*>> {
            return RxJava2CallAdapterFactory
                  .createWithScheduler(scheduler)
                  .get(returnType, annotations, retrofit) as CallAdapter<Any, Single<*>>
        }
    }
}