package com.android.model.utils

sealed class Results<T>() {

   /**
    * Convert Result<T> into Result<R>.
    */
   fun <R> map(mapper: ((T) -> R)? = null): Results<R> {
      return when (this) {
         is Success<T> -> {
            if (mapper == null) {
               throw IllegalStateException("Can't map Success<T> result without mapper.")
            } else {
               Success(mapper(this.value))
            }
         }
         is Error<T> -> Error(this.error)
         is Empty<T> -> Empty()
         is Pending<T> -> Pending()
      }
   }


   fun getValueOrNull(): T? {
      if (this is Success<T>) return this.value
      return null
   }

   fun isFinished() = this is Success<T> || this is Error<T>

   class Success<T>(val value: T) : Results<T>()
   class Error<T>(val error: Throwable) : Results<T>()
   class Empty<T> : Results<T>()
   class Pending<T> : Results<T>()
}

