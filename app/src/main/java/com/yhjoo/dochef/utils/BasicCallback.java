package com.yhjoo.dochef.utils;

import android.content.Context;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BasicCallback<T> implements Callback<T> {

    /*
       TODO
       뭐야이거
    */
    protected BasicCallback(Context context) {
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        Headers headers = response.headers();
        if (headers.get("err") != null) {
            switch (Integer.parseInt(headers.get("err"))) {
                case 110:
                case 111:
                case 801:
                case 828:
                case 999:
                    onFailure();
                    break;
                default:
                    break;
            }
        } else {
            onResponse(response, 0);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        Utils.log(t.toString());
        t.printStackTrace();
        onFailure();
    }

    public void onResponse(Response<T> response) {
    }

    public void onResponse(Response<T> response, int err) {
        onResponse(response);
    }

    public void onFailure() {
    }
}