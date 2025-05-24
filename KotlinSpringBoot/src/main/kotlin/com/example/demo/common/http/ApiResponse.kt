package com.example.demo.common.http

import com.fasterxml.jackson.annotation.JsonIgnore
import org.slf4j.Logger
import org.slf4j.LoggerFactory

sealed class ApiResponse<out T> {
	@get:JsonIgnore
	val log: Logger
		get() = LoggerFactory.getLogger(this::class.java)

	data class Success<T>(val data: T) : ApiResponse<T>()
	data class SuccessNoData(val message: String) : ApiResponse<Nothing>()

	// Base Error class
	sealed class Error(open val message: String, open val code: Int) : ApiResponse<Nothing>() {
		data class BadRequestError(override val message: String, override val code: Int = 400) : Error(message, code)
		data class AuthError(override val message: String, override val code: Int = 401) : Error(message, code)
		data class PermissionsError(override val message: String, override val code: Int = 403) : Error(message, code)
		data class NotFound(override val message: String, override val code: Int = 404) : Error(message, code)
		data class ServerError(override val message: String, override val code: Int = 500) : Error(message, code)
		data class UnknownError(override val message: String, override val code: Int = 0) : Error(message, code)
	}

	companion object {
		fun <T> success(data: T): ApiResponse<T> = Success(data)
		fun successNoData(message: String): ApiResponse<Nothing> = SuccessNoData(message)

		fun error(message: String, code: Int = 400): ApiResponse<Nothing> = when (code) {
			401 -> Error.AuthError(message, code)
			403 -> Error.PermissionsError(message, code)
			404 -> Error.NotFound(message, code)
			500 -> Error.ServerError(message, code)
			0 -> Error.UnknownError(message, code)
			else -> Error.BadRequestError(message, code)
		}
	}

	fun <R> map(transform: (T) -> R): ApiResponse<R> = when (this) {
		is Success -> Success(transform(data))
		is SuccessNoData -> SuccessNoData(message)
		is Error -> this.onError { txt, code ->
			log.error(txt, code)
		}
	}

	fun onSuccess(action: (T) -> Unit): ApiResponse<T> {
		if (this is Success) action(data)
		return this
	}

	fun onError(action: (String, Int) -> Unit): ApiResponse<T> {
		if (this is Error) action(message, code)
		return this
	}
}