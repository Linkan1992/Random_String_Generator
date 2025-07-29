package com.linkan.randomstringgenerator.util

sealed class ResultEvent<out T> {

    data class Success<out T>(val data: T) : ResultEvent<T>()

    data class Error(val exception: Throwable) : ResultEvent<Nothing>()

    data object Loading : ResultEvent<Nothing>()
}