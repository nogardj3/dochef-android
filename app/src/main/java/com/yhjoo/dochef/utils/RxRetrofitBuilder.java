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

    public static Consumer<Throwable> defaultConsumer() {
        return throwable -> {
            throwable.printStackTrace();

            if (throwable instanceof HttpException) {
                int code = ((HttpException) throwable).code();

                if (code == 404)
                    Utils.log("404 Not found");
                else if (code == 409)
                    Utils.log("409 Already Exists");
                else if (code == 500)
                    Utils.log("500 Internal Error");
            } else {
                Utils.log("알 수 없는 에러");
            }
        };
    }
}
