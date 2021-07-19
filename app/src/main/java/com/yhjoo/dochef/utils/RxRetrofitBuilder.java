package com.yhjoo.dochef.utils;

import android.content.Context;

import com.yhjoo.dochef.R;

import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RxRetrofitBuilder {
    public static <T> T create(Context context, final Class<T> service, final boolean loginNeeds, String token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.server_url))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();

        return retrofit.create(service);
    }

    public static <T> T create(Context context, final Class<T> service) {
        return create(context, service, false, "");
    }


    /*
       TODO
       server http error code 정리하고 이거 정리
    */
    public static Consumer<Throwable> defaultConsumer(){
        return throwable -> {
            throwable.printStackTrace();

            if(throwable instanceof HttpException){
                int code = ((HttpException) throwable).code();

                if (code == 403)
                    Utils.log("403 에러 뭐지");
                if (code == 404)
                    Utils.log("404 에러 뭐지");
            }
            else{
                Utils.log("알 수 없는 에러");
            }
        };
    }
}
