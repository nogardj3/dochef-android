package com.yhjoo.dochef.data.network

import android.content.Context
import com.yhjoo.dochef.R
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {
    private val retrofitClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    fun <T> create(context: Context, service: Class<T>): T {
        return Retrofit.Builder()
            .client(retrofitClient)
            .baseUrl(context.getString(R.string.server_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(service)
    }
}