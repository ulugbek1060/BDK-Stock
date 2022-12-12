package com.android.model.repository.base

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.android.model.utils.*
import com.google.gson.JsonParseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

open class BaseRepository : Repository {

   /**
    * @throws EmptyFieldException
    * @throws InvalidCredentialsException
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    * @throws JsonParseException
    */
   open suspend fun <T> suspendRunCatching(block: suspend () -> T): T {
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
            query().map { Success(it) }
         }
      } else {
         query().map { Success(it) }
      }
      emitAll(flow)
   }.flowOn(Dispatchers.IO)


   inline fun <T : Any> getPagerData(
      crossinline pagingFactory: () -> BasePageSource<T>
   ): Flow<PagingData<T>> {
      return Pager(
         config = PagingConfig(
            pageSize = DEFAULT_PAGE_SIZE,
            enablePlaceholders = false
         ),
         pagingSourceFactory = { pagingFactory() }
      ).flow
   }

   fun <T> Flow<T>.asResult(): Flow<Results<T>> {
      return this
         .map<T, Results<T>> {
            Success(it)
         }
         .onStart { emit(Pending()) }
         .catch { emit(Error(it)) }
   }

   companion object {
      const val DEFAULT_PAGE_SIZE = 10
   }
}

//--------------------------------------------------------------------------
//
//abstract class NetworkBoundResource<ResultType, RequestType> {
//
//    fun invoke(): Flow<Resource<ResultType>> = flow {
//        val rawData = loadFromDb()
//
//        if (shouldFetch(rawData)) {
//            fetchDataFromServer()
//                .onStart { emit(Resource.loading(rawData)) } // emit() causing issue
//                .catch { emit(Resource.error(it, null)) } // emit() causing issue
//                .collectLatest { }
//        }
//    }
//
//    // Save API response result into the database
//    protected abstract suspend fun cacheInDb(items: RequestType)
//
//    // Need to fetch data from server or not.
//    protected abstract fun shouldFetch(data: ResultType?): Boolean
//
//    // Show cached data from the database.
//    protected abstract suspend fun loadFromDb(): ResultType
//
//    // Fetch the data from server.
//    protected abstract suspend fun fetchDataFromServer(): Flow<ApiResponse<List<Category>>>
//
//    // when the fetch fails.
//    protected open fun onFetchFailed() {}
//}

// in repository
//    fun getCategories(): Flow<Resource<List<Category>>> {
//        return object : NetworkBoundResource<List<Category>, List<Category>>() {
//
//            override suspend fun cacheInDb(items: List<Category>) {
//                withContext(Dispatchers.IO) { database.getCategories().insert(items) }
//            }
//
//            override fun shouldFetch(data: List<Category>?): Boolean {
//                return true
//            }
//
//            override suspend fun loadFromDb(): List<Category> {
//                return withContext(Dispatchers.IO) { database.getCategories().read() }
//            }
//
//            override suspend fun fetchDataFromServer(): Flow<ApiResponse<List<Category>>> {
//                return flow { emit(RetrofitModule.getCategories()) }
//            }
//
//        }.invoke()
//    }