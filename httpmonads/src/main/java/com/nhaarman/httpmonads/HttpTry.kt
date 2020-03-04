package com.nhaarman.httpmonads

sealed class HttpTry<out R> {

    data class Failure(val httpError: HttpError) : HttpTry<Nothing>()
    data class Success<out R>(val value: R) : HttpTry<R>()

    inline fun <X> map(f: (R) -> X): HttpTry<X> = when (this) {
        is Failure -> this
        is Success -> Success(f(value))
    }

    inline fun <X> flatMap(f: (R) -> HttpTry<X>): HttpTry<X> = when (this) {
        is Success -> f(value)
        is Failure -> this
    }

    inline fun <X> fold(fl: (HttpError) -> X, fr: (R) -> X): X = when (this) {
        is Failure -> fl(httpError)
        is Success -> fr(value)
    }

    companion object {

        fun failure(httpError: HttpError): HttpTry<Nothing> = Failure(httpError)
        fun <R> success(value: R): HttpTry<R> = Success(value)
    }
}

inline fun <R> HttpTry<R>.getOrElse(default: () -> R): R = when (this) {
    is HttpTry.Success -> value
    is HttpTry.Failure -> default()
}