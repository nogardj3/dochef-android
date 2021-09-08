package com.yhjoo.dochef.utils

import android.content.Context
import com.yhjoo.dochef.R
import io.reactivex.rxjava3.functions.Consumer
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {
    // TODO
    // 1. Coroutine용으로 변경

    // 타임아웃, 헤더 등 설정
    private val retrofitClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();
    }

    fun <T> create(context: Context, service: Class<T>): T {
        return Retrofit.Builder()
            .client(retrofitClient)
            .baseUrl(context.getString(R.string.server_url))
            .addConverterFactory(GsonConverterFactory.create())
            // use RxJava, return Call -> Observable, Single
            // .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(service)
    }

    fun defaultErrorHandler (throwable: Throwable) {
        throwable.printStackTrace()
        if (throwable is HttpException) {
            val code = throwable.code()
            var codeText = ""

            when (code) {
                404 -> codeText = "404 Not found"
                409 -> codeText = "409 Already Exists"
                500 -> codeText = "500 Internal Error"
            }
            Utils.log(codeText)

        } else {
            Utils.log("알 수 없는 에러")
        }
    }
}