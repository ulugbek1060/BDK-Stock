package com.android.source.network.base

import com.android.model.utils.AppException
import com.android.model.utils.BackendException
import com.android.model.utils.ConnectionException
import com.android.model.utils.ParseBackendResponseException
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

   // TODO: need to ask for change error message
   private fun createBackendException(e: HttpException): Exception {
      return try {
         val jsonObject = JSONObject(e.response()!!.errorBody()!!.string())
         val message = if (jsonObject.has("message")) {
            jsonObject.getString("message")
         } else if (jsonObject.has("error")) {
            jsonObject.getString("error")
         } else {
            "parse problem"
         }
         val code = e.code()
         BackendException(code, message)
      } catch (e: Exception) {
         throw ParseBackendResponseException(e)
      }
   }
}