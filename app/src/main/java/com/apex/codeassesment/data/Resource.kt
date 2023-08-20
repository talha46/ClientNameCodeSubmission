package com.apex.codeassesment.data

import okhttp3.ResponseBody

sealed class Resource<out R> {
    data class Success<out R>(val response: R?): Resource<R>()
    data class Failure<out R>(val error: ResponseBody?): Resource<R>()
    object Loading: Resource<Nothing>()
}
