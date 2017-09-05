package com.nhaarman.httpmonads

import com.nhaarman.httpmonads.HttpTry.Failure
import com.nhaarman.httpmonads.HttpTry.Success
import org.funktionale.either.Disjunction
import org.funktionale.either.Either
import org.funktionale.tries.Try

fun <R> HttpTry<R>.toEither(): Either<HttpError, R> = when (this) {
    is Failure -> Either.left(httpError)
    is Success -> Either.right(value)
}

fun <R> HttpTry<R>.toDisjunction(): Disjunction<HttpError, R> = when (this) {
    is Failure -> Disjunction.left(httpError)
    is Success -> Disjunction.right(value)
}

fun <R> HttpTry<R>.toTry(): Try<out R> = when (this) {
    is Failure -> Try.Failure(httpError.toThrowable())
    is Success -> Try.Success(value)
}