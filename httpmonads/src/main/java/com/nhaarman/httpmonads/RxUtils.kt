package com.nhaarman.httpmonads

import io.reactivex.Observable
import io.reactivex.Single

fun <T, R> Observable<out HttpTry<T>>.mapHttpTry(mapper: (T) -> R): Observable<HttpTry<R>> {
    return map { it.map(mapper) }
}

fun <T, R> Observable<out HttpTry<T>>.switchMapHttpTry(mapper: (T) -> Observable<HttpTry<R>>): Observable<HttpTry<R>> {
    return switchMap {
        when (it) {
            is HttpTry.Success -> mapper(it.value)
            is HttpTry.Failure -> Observable.just(HttpTry.failure(it.httpError))
        }
    }
}

fun <T, R> Single<out HttpTry<T>>.mapHttpTry(mapper: (T) -> R): Single<HttpTry<R>> {
    return map { it.map(mapper) }
}

fun <T, R> Single<out HttpTry<T>>.flatMapHttpTry(mapper: (T) -> Single<out HttpTry<R>>): Single<HttpTry<R>> {
    return flatMap {
        when (it) {
            is HttpTry.Success -> mapper(it.value)
            is HttpTry.Failure -> Single.just(HttpTry.Failure(it.httpError))
        }
    }
}

fun <T, R> Observable<out HttpTry<T>>.flatMapHttpTry(mapper: (T) -> Observable<out HttpTry<R>>): Observable<HttpTry<R>> {
    return flatMap {
        when (it) {
            is HttpTry.Success -> mapper(it.value)
            is HttpTry.Failure -> Observable.just(HttpTry.Failure(it.httpError))
        }
    }
}

fun <T1, T2> Pair<HttpTry<T1>, HttpTry<T2>>.flatten(): HttpTry<Pair<T1, T2>> {
    return this.first.flatMap { x -> this.second.map { y -> x to y } }
}