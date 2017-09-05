package com.nhaarman.httpmonads

import java.io.IOException

sealed class HttpError {

    sealed class NoBody2XX : HttpError() {
        object NoContent204 : NoBody2XX() { override fun toString() = "NoContent204" }
        object ResetContent205 : NoBody2XX() { override fun toString() = "ResetContent205" }
    }

    sealed class Redirection3XX : HttpError() {
        object MultipleChoices300 : Redirection3XX() { override fun toString() = "MultipleChoices300" }
        object MovedPermanently301 : Redirection3XX() { override fun toString() = "MovedPermanently301" }
        object Found302 : Redirection3XX() { override fun toString() = "Found302" }
        object SeeOther303 : Redirection3XX() { override fun toString() = "SeeOther303" }
        object NotModified304 : Redirection3XX() { override fun toString() = "NotModified304" }
        object UseProxy305 : Redirection3XX() { override fun toString() = "UseProxy305" }
        object TemporaryRedirect307 : Redirection3XX() { override fun toString() = "TemporaryRedirect307" }
        object PermanentRedirect308 : Redirection3XX() { override fun toString() = "PermanentRedirect308" }
    }

    sealed class ClientError4XX : HttpError() {
        object BadRequest400 : ClientError4XX() { override fun toString() = "BadRequest400" }
        object Unauthorized401 : ClientError4XX() { override fun toString() = "Unauthorized401" }
        object PaymentRequired402 : ClientError4XX() { override fun toString() = "PaymentRequired402" }
        object Forbidden403 : ClientError4XX() { override fun toString() = "Forbidden403" }
        object NotFound404 : ClientError4XX() { override fun toString() = "NotFound404" }
        object MethodNotAllowed405 : ClientError4XX() { override fun toString() = "MethodNotAllowed405" }
        object NotAcceptable406 : ClientError4XX() { override fun toString() = "NotAcceptable406" }
        object ProxyAuthenticationRequired407 : ClientError4XX() { override fun toString() = "ProxyAuthenticationRequired407" }
        object RequestTimeout408 : ClientError4XX() { override fun toString() = "RequestTimeout408" }
        object Conflict409 : ClientError4XX() { override fun toString() = "Conflict409" }
        object Gone410 : ClientError4XX() { override fun toString() = "Gone410" }
        object LengthRequired411 : ClientError4XX() { override fun toString() = "LengthRequired411" }
        object PreconditionFailed412 : ClientError4XX() { override fun toString() = "PreconditionFailed412" }
        object PayloadTooLarge413 : ClientError4XX() { override fun toString() = "PayloadTooLarge413" }
        object URITooLong414 : ClientError4XX() { override fun toString() = "URITooLong414" }
        object UnsupportedMediaType415 : ClientError4XX() { override fun toString() = "UnsupportedMediaType415" }
        object RangeNotSatisfiable416 : ClientError4XX() { override fun toString() = "RangeNotSatisfiable416" }
        object ExpectationFailed417 : ClientError4XX() { override fun toString() = "ExpectationFailed417" }
        object MisdirectedRequest421 : ClientError4XX() { override fun toString() = "MisdirectedRequest421" }
        object UnprocessableEntry422 : ClientError4XX() { override fun toString() = "UnprocessableEntry422" }
        object Locked423 : ClientError4XX() { override fun toString() = "Locked423" }
        object FailedDependency424 : ClientError4XX() { override fun toString() = "FailedDependency424" }
        object UpgradeRequired426 : ClientError4XX() { override fun toString() = "UpgradeRequired426" }
        object PreconditionRequired428 : ClientError4XX() { override fun toString() = "PreconditionRequired428" }
        object TooManyRequests429 : ClientError4XX() { override fun toString() = "TooManyRequests429" }
        object RequestHeaderFieldsTooLarge431 : ClientError4XX() { override fun toString() = "RequestHeaderFieldsTooLarge431" }
        object UnavailableForLegalReasons451 : ClientError4XX() { override fun toString() = "UnavailableForLegalReasons451" }
    }

    sealed class ServerError5XX : HttpError() {
      object InternalServerError500 : ServerError5XX() { override fun toString() = "InternalServerError500" }
      object NotImplementedError501 : ServerError5XX() { override fun toString() = "NotImplementedError501" }
      object BadGateway502 : ServerError5XX() { override fun toString() = "BadGateway502" }
      object ServiceUnavailable503 : ServerError5XX() { override fun toString() = "ServiceUnavailable503" }
      object GatewayTimeout504 : ServerError5XX() { override fun toString() = "GatewayTimeout504" }
      object HTTPVersionNotSupported505 : ServerError5XX() { override fun toString() = "HTTPVersionNotSupported505" }
      object VariantAlsoNegotiates506 : ServerError5XX() { override fun toString() = "VariantAlsoNegotiates506" }
      object InsufficientStorage507 : ServerError5XX() { override fun toString() = "InsufficientStorage507" }
      object LoopDetected508 : ServerError5XX() { override fun toString() = "LoopDetected508" }
      object NotExtended510 : ServerError5XX() { override fun toString() = "NotExtended510" }
      object NetworkAuthenticationRequired511 : ServerError5XX() { override fun toString() = "NetworkAuthenticationRequired511" }
    }

    data class UnknownStatusCode(val code: Int) : HttpError()

    data class NetworkError(val e: IOException) : HttpError()

    fun toThrowable() : Throwable = when (this) {
        is NetworkError -> HttpException(e)
        else -> HttpException(this)
    }
}
