package com.ahmed_badr.to_do_manager.utils.response

sealed class ResultStatus<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : ResultStatus<T>(data)
    class Error<T>(message: String) : ResultStatus<T>(message = message)
}
