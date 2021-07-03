package com.yhjoo.dochef.utils;

import android.content.Context;

import com.yhjoo.dochef.R;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitBuilder {
    public static <T> T create(Context context, final Class<T> service, final boolean loginNeeds, String token) {
        if (loginNeeds) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            httpClient.addInterceptor(chain -> {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Auth-Token", token)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            });

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(context.getString(R.string.server_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

            return retrofit.create(service);
        } else {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(context.getString(R.string.server_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            return retrofit.create(service);
        }
    }

    public static <T> T create(Context context, final Class<T> service, boolean loginNeeds) {
        return create(context, service, loginNeeds, "");
    }
}
