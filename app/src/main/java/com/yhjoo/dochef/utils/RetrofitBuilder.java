package com.yhjoo.dochef.utils;

import android.content.Context;

import com.yhjoo.dochef.R;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitBuilder {
    public static <T> T create(Context context, final Class<T> service, final boolean loginNeeds, String token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.server_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(service);
    }

    public static <T> T create(Context context, final Class<T> service) {
        return create(context, service, false, "");
    }
}
