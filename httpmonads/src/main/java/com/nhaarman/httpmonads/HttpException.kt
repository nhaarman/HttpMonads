package com.nhaarman.httpmonads

import java.io.*

internal class HttpException : Exception {

    constructor(e: IOException) : super(e)
    constructor(httpError: HttpError) : super(httpError.toString())
}