package com.android.model.repository.base

import com.android.model.utils.*
import com.google.gson.JsonParseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

open class BaseRepository {

   /**
    * @throws EmptyFieldException
    * @throws InvalidCredentialsException
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    * @throws JsonParseException
    */
   open suspend fun <T> launchWithException(block: suspend () -> T): T {
      return try {
         block.invoke()
      } catch (e: Exception) {
         throw if (e is BackendException && e.code == 401) {
            AuthException(e)
         } else {
            throw e
         }
      }
   }

   inline fun <ResultData, Response> networkBoundResult(
      // get resultData from database return type flow<RoomResult>
      crossinline query: () -> Flow<ResultData>,
      //fetch data from rest api
      crossinline fetch: suspend () -> Response,
      //save result to db
      crossinline saveFetchedResult: suspend (Response) -> Unit,
      //update db
      crossinline shouldFetch: (ResultData) -> Boolean = { true }
   ) = flow {
      val data = query().first()
      val flow = if (shouldFetch(data)) {
         emit(Pending())
         try {
            saveFetchedResult(fetch())
            query().map { Success(it) }
         } catch (e: Exception) {
            if (e is BackendException && e.code == 401) {
               throw AuthException(e)
            } else {
               query().map { Success(it) }
            }
         }
      } else {
         query().map { Success(it) }
      }
      emitAll(flow)
   }.flowOn(Dispatchers.IO)

}