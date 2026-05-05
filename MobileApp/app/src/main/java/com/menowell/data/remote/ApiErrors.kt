package com.menowell.data.remote

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import retrofit2.HttpException

private data class ApiErrorResponse(
    @SerializedName("detail") val detail: Any? = null,
)

fun Throwable.toReadableMessage(
    defaultMessage: String,
    gson: Gson = Gson(),
): String {
    val exception = this as? HttpException ?: return message ?: defaultMessage
    val body = exception.response()?.errorBody()?.string().orEmpty()
    if (body.isBlank()) return message ?: defaultMessage

    return runCatching {
        val parsed = gson.fromJson(body, ApiErrorResponse::class.java)
        when (val detail = parsed.detail) {
            is String -> detail
            is List<*> -> detail.joinToString("\n") { item ->
                (item as? Map<*, *>)?.get("msg")?.toString() ?: item.toString()
            }
            else -> message ?: defaultMessage
        }
    }.getOrDefault(message ?: defaultMessage)
}
