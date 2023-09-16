package com.adtarassov.ginder.data

sealed interface ResponseState<out T> {

  data class Error(val throwable: Throwable, val message: String? = null) : ResponseState<Nothing>

  data class Success<T>(val item: T) : ResponseState<T>

}