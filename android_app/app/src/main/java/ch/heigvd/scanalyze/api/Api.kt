package ch.heigvd.scanalyze.api

import android.content.Context
import ch.heigvd.scanalyze.Utils.Utils.isNetworkAvailable
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException


object Api {

    fun getStats(fromDate: String, toDate: String, callback: ApiCallback, context: Context) {

        val endpoint =
            "http://scanalyze-backend.eba-mjtcbsxb.eu-central-1.elasticbeanstalk.com/statistics?from=$fromDate&to=$toDate"

        call(endpoint, callback, context, HttpMethod.GET)
    }

    fun getReceiptsOverview(callback: ApiCallback, context: Context) {
        val endpoint =
            "http://scanalyze-backend.eba-mjtcbsxb.eu-central-1.elasticbeanstalk.com/receipts"

        call(endpoint, callback, context, HttpMethod.GET)
    }

    fun getReceiptDetail(id: String, callback: ApiCallback, context: Context) {
        val endpoint =
            "http://scanalyze-backend.eba-mjtcbsxb.eu-central-1.elasticbeanstalk.com/receipts/$id"

        call(endpoint, callback, context, HttpMethod.GET)
    }

    fun postReceipt(body: String, callback: ApiCallback, context: Context) {

        val endpoint =
            "http://scanalyze-backend.eba-mjtcbsxb.eu-central-1.elasticbeanstalk.com/receipts"

        call(endpoint, callback, context, HttpMethod.POST, body)
    }
}

private fun call(
    endPoint: String,
    callback: ApiCallback,
    context: Context,
    method: HttpMethod,
    body: String = ""
) {

    val request = when (method) {
        HttpMethod.GET -> {
            Request.Builder().url(endPoint).get().build()
        }

        HttpMethod.POST -> {
            val b = body.toRequestBody("application/json".toMediaTypeOrNull())
            Request.Builder().url(endPoint).addHeader("Accept", "application/json").post(b).build()
        }
    }

    if (!isNetworkAvailable(context)) {
        callback.onFailure("Network not available")
    } else {
        sendRequest(request, callback)
    }
}

private fun sendRequest(request: Request, callback: ApiCallback) {

    // Get the client
    val client = OkHttpClient()

    // Asynchronous call
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            // Notify the callback of the failure
            callback.onFailure(e.message ?: "Unknown error")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                // Notify the callback of the success
                val responseBody = response.body?.string() ?: ""
                callback.onSuccess(responseBody)

            } else {
                // Notify the callback of the failure
                callback.onFailure("API error ${response.code}: ${response.message}")
            }
        }
    })
}

