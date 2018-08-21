package com.nhaarman.httpmonads

import arrow.core.Either
import arrow.core.Try
import com.nhaarman.httpmonads.HttpTry.Failure
import com.nhaarman.httpmonads.HttpTry.Success

fun <R> HttpTry<R>.toEither(): Either<HttpError, R> = when (this) {
    is Failure -> Either.left(httpError)
    is Success -> Either.right(value)
}

fun <R> HttpTry<R>.toTry(): Try<R> = when (this) {
    is Failure -> Try.Failure(httpError.toThrowable())
    is Success -> Try.Success(value)
}