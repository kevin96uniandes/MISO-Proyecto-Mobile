package com.uniandes.project.abcall.config

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int, val message: String?) : ApiResult<Nothing>()
    data object NetworkError : ApiResult<Nothing>()
}