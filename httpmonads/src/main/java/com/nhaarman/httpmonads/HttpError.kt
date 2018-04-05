package com.nhaarman.httpmonads

import java.io.*

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
        abstract val errorBody: InputStream?

        class BadRequest400(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "BadRequest400" }
        class Unauthorized401(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "Unauthorized401" }
        class PaymentRequired402(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "PaymentRequired402" }
        class Forbidden403(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "Forbidden403" }
        class NotFound404(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "NotFound404" }
        class MethodNotAllowed405(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "MethodNotAllowed405" }
        class NotAcceptable406(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "NotAcceptable406" }
        class ProxyAuthenticationRequired407(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "ProxyAuthenticationRequired407" }
        class RequestTimeout408(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "RequestTimeout408" }
        class Conflict409(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "Conflict409" }
        class Gone410(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "Gone410" }
        class LengthRequired411(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "LengthRequired411" }
        class PreconditionFailed412(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "PreconditionFailed412" }
        class PayloadTooLarge413(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "PayloadTooLarge413" }
        class URITooLong414(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "URITooLong414" }
        class UnsupportedMediaType415(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "UnsupportedMediaType415" }
        class RangeNotSatisfiable416(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "RangeNotSatisfiable416" }
        class ExpectationFailed417(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "ExpectationFailed417" }
        class MisdirectedRequest421(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "MisdirectedRequest421" }
        class UnprocessableEntry422(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "UnprocessableEntry422" }
        class Locked423(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "Locked423" }
        class FailedDependency424(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "FailedDependency424" }
        class UpgradeRequired426(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "UpgradeRequired426" }
        class PreconditionRequired428(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "PreconditionRequired428" }
        class TooManyRequests429(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "TooManyRequests429" }
        class RequestHeaderFieldsTooLarge431(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "RequestHeaderFieldsTooLarge431" }
        class UnavailableForLegalReasons451(override val errorBody: InputStream?) : ClientError4XX() { override fun toString() = "UnavailableForLegalReasons451" }
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
