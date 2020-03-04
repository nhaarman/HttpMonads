package com.nhaarman.httpmonads

import java.io.IOException
import java.io.InputStream

sealed class HttpError {

    sealed class NoBody2XX : HttpError() {
        object NoContent204 : NoBody2XX() {
            override fun toString() = "NoContent204"
        }

        object ResetContent205 : NoBody2XX() {
            override fun toString() = "ResetContent205"
        }
    }

    sealed class Redirection3XX : HttpError() {
        object MultipleChoices300 : Redirection3XX() {
            override fun toString() = "MultipleChoices300"
        }

        object MovedPermanently301 : Redirection3XX() {
            override fun toString() = "MovedPermanently301"
        }

        object Found302 : Redirection3XX() {
            override fun toString() = "Found302"
        }

        object SeeOther303 : Redirection3XX() {
            override fun toString() = "SeeOther303"
        }

        object NotModified304 : Redirection3XX() {
            override fun toString() = "NotModified304"
        }

        object UseProxy305 : Redirection3XX() {
            override fun toString() = "UseProxy305"
        }

        object TemporaryRedirect307 : Redirection3XX() {
            override fun toString() = "TemporaryRedirect307"
        }

        object PermanentRedirect308 : Redirection3XX() {
            override fun toString() = "PermanentRedirect308"
        }
    }

    sealed class ClientError4XX : HttpError() {
        abstract val errorBody: InputStream?

        class BadRequest400(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "BadRequest400"
        }

        class Unauthorized401(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "Unauthorized401"
        }

        class PaymentRequired402(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "PaymentRequired402"
        }

        class Forbidden403(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "Forbidden403"
        }

        class NotFound404(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "NotFound404"
        }

        class MethodNotAllowed405(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "MethodNotAllowed405"
        }

        class NotAcceptable406(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "NotAcceptable406"
        }

        class ProxyAuthenticationRequired407(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "ProxyAuthenticationRequired407"
        }

        class RequestTimeout408(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "RequestTimeout408"
        }

        class Conflict409(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "Conflict409"
        }

        class Gone410(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "Gone410"
        }

        class LengthRequired411(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "LengthRequired411"
        }

        class PreconditionFailed412(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "PreconditionFailed412"
        }

        class PayloadTooLarge413(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "PayloadTooLarge413"
        }

        class URITooLong414(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "URITooLong414"
        }

        class UnsupportedMediaType415(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "UnsupportedMediaType415"
        }

        class RangeNotSatisfiable416(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "RangeNotSatisfiable416"
        }

        class ExpectationFailed417(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "ExpectationFailed417"
        }

        class MisdirectedRequest421(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "MisdirectedRequest421"
        }

        class UnprocessableEntry422(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "UnprocessableEntry422"
        }

        class Locked423(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "Locked423"
        }

        class FailedDependency424(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "FailedDependency424"
        }

        class UpgradeRequired426(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "UpgradeRequired426"
        }

        class PreconditionRequired428(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "PreconditionRequired428"
        }

        class TooManyRequests429(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "TooManyRequests429"
        }

        class RequestHeaderFieldsTooLarge431(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "RequestHeaderFieldsTooLarge431"
        }

        class UnavailableForLegalReasons451(override val errorBody: InputStream? = null) : ClientError4XX() {
            override fun toString() = "UnavailableForLegalReasons451"
        }
    }

    sealed class ServerError5XX : HttpError() {
        object InternalServerError500 : ServerError5XX() {
            override fun toString() = "InternalServerError500"
        }

        object NotImplementedError501 : ServerError5XX() {
            override fun toString() = "NotImplementedError501"
        }

        object BadGateway502 : ServerError5XX() {
            override fun toString() = "BadGateway502"
        }

        object ServiceUnavailable503 : ServerError5XX() {
            override fun toString() = "ServiceUnavailable503"
        }

        object GatewayTimeout504 : ServerError5XX() {
            override fun toString() = "GatewayTimeout504"
        }

        object HTTPVersionNotSupported505 : ServerError5XX() {
            override fun toString() = "HTTPVersionNotSupported505"
        }

        object VariantAlsoNegotiates506 : ServerError5XX() {
            override fun toString() = "VariantAlsoNegotiates506"
        }

        object InsufficientStorage507 : ServerError5XX() {
            override fun toString() = "InsufficientStorage507"
        }

        object LoopDetected508 : ServerError5XX() {
            override fun toString() = "LoopDetected508"
        }

        object NotExtended510 : ServerError5XX() {
            override fun toString() = "NotExtended510"
        }

        object NetworkAuthenticationRequired511 : ServerError5XX() {
            override fun toString() = "NetworkAuthenticationRequired511"
        }
    }

    data class UnknownStatusCode(val code: Int) : HttpError()

    data class NetworkError(val e: IOException) : HttpError()

    fun toThrowable(): Throwable = when (this) {
        is NetworkError -> HttpException(e)
        else -> HttpException(this)
    }

    val statusCode
        get() = when (this) {
            is NoBody2XX.NoContent204 -> 204
            is NoBody2XX.ResetContent205 -> 205
            is Redirection3XX.MultipleChoices300 -> 300
            is Redirection3XX.MovedPermanently301 -> 301
            is Redirection3XX.Found302 -> 302
            is Redirection3XX.SeeOther303 -> 303
            is Redirection3XX.NotModified304 -> 304
            is Redirection3XX.UseProxy305 -> 305
            is Redirection3XX.TemporaryRedirect307 -> 307
            is Redirection3XX.PermanentRedirect308 -> 308
            is ClientError4XX.BadRequest400 -> 400
            is ClientError4XX.Unauthorized401 -> 401
            is ClientError4XX.PaymentRequired402 -> 402
            is ClientError4XX.Forbidden403 -> 403
            is ClientError4XX.NotFound404 -> 404
            is ClientError4XX.MethodNotAllowed405 -> 405
            is ClientError4XX.NotAcceptable406 -> 406
            is ClientError4XX.ProxyAuthenticationRequired407 -> 407
            is ClientError4XX.RequestTimeout408 -> 408
            is ClientError4XX.Conflict409 -> 409
            is ClientError4XX.Gone410 -> 410
            is ClientError4XX.LengthRequired411 -> 411
            is ClientError4XX.PreconditionFailed412 -> 412
            is ClientError4XX.PayloadTooLarge413 -> 413
            is ClientError4XX.URITooLong414 -> 414
            is ClientError4XX.UnsupportedMediaType415 -> 415
            is ClientError4XX.RangeNotSatisfiable416 -> 416
            is ClientError4XX.ExpectationFailed417 -> 417
            is ClientError4XX.MisdirectedRequest421 -> 421
            is ClientError4XX.UnprocessableEntry422 -> 422
            is ClientError4XX.Locked423 -> 423
            is ClientError4XX.FailedDependency424 -> 424
            is ClientError4XX.UpgradeRequired426 -> 426
            is ClientError4XX.PreconditionRequired428 -> 428
            is ClientError4XX.TooManyRequests429 -> 429
            is ClientError4XX.RequestHeaderFieldsTooLarge431 -> 431
            is ClientError4XX.UnavailableForLegalReasons451 -> 451
            is ServerError5XX.InternalServerError500 -> 500
            is ServerError5XX.NotImplementedError501 -> 501
            is ServerError5XX.BadGateway502 -> 502
            is ServerError5XX.ServiceUnavailable503 -> 503
            is ServerError5XX.GatewayTimeout504 -> 504
            is ServerError5XX.HTTPVersionNotSupported505 -> 505
            is ServerError5XX.VariantAlsoNegotiates506 -> 506
            is ServerError5XX.InsufficientStorage507 -> 507
            is ServerError5XX.LoopDetected508 -> 508
            is ServerError5XX.NotExtended510 -> 510
            is ServerError5XX.NetworkAuthenticationRequired511 -> 511
            is UnknownStatusCode -> code
            is NetworkError -> null
        }
}
