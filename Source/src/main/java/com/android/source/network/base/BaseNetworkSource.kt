package com.android.source.network.base

import com.android.model.models.AppException
import com.android.model.models.BackendException
import com.android.model.models.ConnectionException
import com.android.model.models.ParseBackendResponseException
import com.google.gson.JsonParseException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException


open class BaseNetworkSource {

   /**
    * Map network and parse exceptions into in-app exceptions.
    * @throws BackendException
    * @throws ParseBackendResponseException
    * @throws ConnectionException
    * @throws JsonParseException
    */
   suspend fun <T> wrapNetworkException(block: suspend () -> T): T {
      return try {
         block()
      } catch (e: AppException) {
         throw e
      } catch (e: JsonParseException) {
         throw e
      } catch (e: HttpException) {
         throw createBackendException(e)
         // mostly retrofit
      } catch (e: IOException) {
         throw ConnectionException(e)
      }
   }

   private fun createBackendException(e: HttpException): Exception {
      return try {
         val jsonObject = JSONObject(e.response()!!.errorBody()!!.string())
         val message = jsonObject.getString("message")
         val code = e.code()
         BackendException(code, message)
      } catch (e: Exception) {
         throw ParseBackendResponseException(e)
      }
   }
}