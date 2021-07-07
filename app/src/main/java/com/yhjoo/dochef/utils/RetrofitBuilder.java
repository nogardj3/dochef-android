package com.yhjoo.dochef.utils;

import android.content.Context;

import com.yhjoo.dochef.R;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public final class RetrofitBuilder {
    public static <T> T create(Context context, final Class<T> service, final boolean loginNeeds, String token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.server_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(service);
    }

    public static <T> T create(Context context, final Class<T> service, boolean loginNeeds) {
        return create(context, service, loginNeeds, "");
    }

    public static <T> T create(Context context, final Class<T> service) {
        return create(context, service, false, "");
    }

    public static <T> T createScalar(Context context, final Class<T> service) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.server_url))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        return retrofit.create(service);
    }
}
