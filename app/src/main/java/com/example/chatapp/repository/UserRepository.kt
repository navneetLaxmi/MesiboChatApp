package com.example.chatapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chatapp.data.UserToken
import com.example.chatapp.data.UserTokenResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

import java.io.IOException

class UserRepository {
    private val client = OkHttpClient()

    fun createUserToken(userTokenRequest: UserToken): LiveData<Result<String>> {
        val result = MutableLiveData<Result<String>>()

        val json = Gson().toJson(userTokenRequest)
        val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), json)

        val request = Request.Builder()
            .url("https://api.mesibo.com/backend/")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                result.postValue(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let {responseBody ->
                        //result.postValue(Result.success(it))
                        try {
                            val userTokenResponse = Gson().fromJson(responseBody, UserTokenResponse::class.java)
                            if (userTokenResponse.result) {
                                result.postValue(Result.success(userTokenResponse.user.token))
                            } else {
                                result.postValue(Result.failure(IOException("User token creation failed")))
                            }
                        } catch (e: JsonSyntaxException) {
                            result.postValue(Result.failure(IOException("Malformed response")))
                        }?: run {
                            result.postValue(Result.failure(IOException("Empty response body")))
                        }
                    }
                } else {
                    result.postValue(Result.failure(IOException("Unexpected code $response")))
                }
            }
        })
        return result
    }
}