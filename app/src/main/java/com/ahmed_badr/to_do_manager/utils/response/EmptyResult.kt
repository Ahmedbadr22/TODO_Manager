package com.ahmed_badr.to_do_manager.utils.response

sealed class EmptyResult(
    val message: String? = null
) {
    class Success() : EmptyResult()
    class Error(message: String) : EmptyResult(message)
}
