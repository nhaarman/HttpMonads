package com.nhaarman.httpmonads

import com.nhaarman.httpmonads.HttpError.*
import retrofit2.Response

fun <R : Any?> Response<R>.toHttpError(): HttpError = when (code()) {

    204 -> NoBody2XX.NoContent204
    205 -> NoBody2XX.ResetContent205

    300 -> Redirection3XX.MultipleChoices300
    301 -> Redirection3XX.MovedPermanently301
    302 -> Redirection3XX.Found302
    303 -> Redirection3XX.SeeOther303
    304 -> Redirection3XX.NotModified304
    305 -> Redirection3XX.UseProxy305
    307 -> Redirection3XX.TemporaryRedirect307
    308 -> Redirection3XX.PermanentRedirect308

    400 -> ClientError4XX.BadRequest400(errorBody()?.byteStream())
    401 -> ClientError4XX.Unauthorized401(errorBody()?.byteStream())
    402 -> ClientError4XX.PaymentRequired402(errorBody()?.byteStream())
    403 -> ClientError4XX.Forbidden403(errorBody()?.byteStream())
    404 -> ClientError4XX.NotFound404(errorBody()?.byteStream())
    405 -> ClientError4XX.MethodNotAllowed405(errorBody()?.byteStream())
    406 -> ClientError4XX.NotAcceptable406(errorBody()?.byteStream())
    407 -> ClientError4XX.ProxyAuthenticationRequired407(errorBody()?.byteStream())
    408 -> ClientError4XX.RequestTimeout408(errorBody()?.byteStream())
    409 -> ClientError4XX.Conflict409(errorBody()?.byteStream())
    410 -> ClientError4XX.Gone410(errorBody()?.byteStream())
    411 -> ClientError4XX.LengthRequired411(errorBody()?.byteStream())
    412 -> ClientError4XX.PreconditionFailed412(errorBody()?.byteStream())
    413 -> ClientError4XX.PayloadTooLarge413(errorBody()?.byteStream())
    414 -> ClientError4XX.URITooLong414(errorBody()?.byteStream())
    415 -> ClientError4XX.UnsupportedMediaType415(errorBody()?.byteStream())
    416 -> ClientError4XX.RangeNotSatisfiable416(errorBody()?.byteStream())
    417 -> ClientError4XX.ExpectationFailed417(errorBody()?.byteStream())
    421 -> ClientError4XX.MisdirectedRequest421(errorBody()?.byteStream())
    422 -> ClientError4XX.UnprocessableEntry422(errorBody()?.byteStream())
    423 -> ClientError4XX.Locked423(errorBody()?.byteStream())
    424 -> ClientError4XX.FailedDependency424(errorBody()?.byteStream())
    426 -> ClientError4XX.UpgradeRequired426(errorBody()?.byteStream())
    428 -> ClientError4XX.PreconditionRequired428(errorBody()?.byteStream())
    429 -> ClientError4XX.TooManyRequests429(errorBody()?.byteStream())
    431 -> ClientError4XX.RequestHeaderFieldsTooLarge431(errorBody()?.byteStream())
    451 -> ClientError4XX.UnavailableForLegalReasons451(errorBody()?.byteStream())

    500 -> ServerError5XX.InternalServerError500
    501 -> ServerError5XX.NotImplementedError501
    502 -> ServerError5XX.BadGateway502
    503 -> ServerError5XX.ServiceUnavailable503
    504 -> ServerError5XX.GatewayTimeout504
    505 -> ServerError5XX.HTTPVersionNotSupported505
    506 -> ServerError5XX.VariantAlsoNegotiates506
    507 -> ServerError5XX.InsufficientStorage507
    508 -> ServerError5XX.LoopDetected508
    510 -> ServerError5XX.NotExtended510
    511 -> ServerError5XX.NetworkAuthenticationRequired511

    else -> UnknownStatusCode(code())
}