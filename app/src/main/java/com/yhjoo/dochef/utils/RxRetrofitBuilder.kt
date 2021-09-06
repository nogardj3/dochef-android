package com.yhjoo.dochef.utils

import android.content.Context
import com.yhjoo.dochef.R
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RxRetrofitBuilder {
    // TODO
    // 1. Coroutine용으로 변경
    // 2. create 옵션 살피기
    // 3. header, response 전역 메소드 정하기

    fun <T> create(context: Context, service: Class<T>, loginNeeds: Boolean, token: String?): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.server_url))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
        return retrofit.create(service)
    }

    fun <T> create(context: Context, service: Class<T>): T {
        return create(context, service, false, "")
    }

    fun defaultConsumer(): Consumer<Throwable> {
        return Consumer { throwable: Throwable ->
            throwable.printStackTrace()
            if (throwable is HttpException) {
                val code = throwable.code()
                var codeText = ""

                when(code){
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
}