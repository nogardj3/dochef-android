package com.yhjoo.dochef.data.network

import android.content.Context
import com.yhjoo.dochef.R
import com.yhjoo.dochef.utils.OtherUtil
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {
//    타임아웃, 헤더 등 설정
//    private val retrofitClient: OkHttpClient by lazy {
//        OkHttpClient.Builder()
//            .connectTimeout(10, TimeUnit.SECONDS)
//            .writeTimeout(10, TimeUnit.SECONDS)
//            .readTimeout(10, TimeUnit.SECONDS)
//            .build()
//    }

    fun <T> create(context: Context, service: Class<T>): T {
        return Retrofit.Builder()
//            .client(retrofitClient)
            .baseUrl(context.getString(R.string.server_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(service)
    }

    // TODO
    fun defaultErrorHandler(throwable: Throwable) {
        throwable.printStackTrace()
        if (throwable is HttpException) {
            val code = throwable.code()
            var codeText = ""

            when (code) {
                404 -> codeText = "404 Not found"
                409 -> codeText = "409 Already Exists"
                500 -> codeText = "500 Internal Error"
            }
            OtherUtil.log(codeText)

        } else {
            OtherUtil.log("알 수 없는 에러")
            OtherUtil.log(throwable.message!!, throwable.toString(), throwable.localizedMessage!!)
        }
    }
}