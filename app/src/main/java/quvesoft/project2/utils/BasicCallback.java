package quvesoft.project2.utils;

import android.content.Context;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BasicCallback<T> implements Callback<T> {
    private Context context;

    protected BasicCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        Headers headers = response.headers();
        if (headers.get("err") != null) {
            switch (Integer.valueOf(headers.get("err"))) {
                case 110:
                    //Project2.getAppInstance().showToast();
                    //SproutAuth.SignOut(context);
                    onFailure();
                    break;
                case 111:
                    //Project2.getAppInstance().showToast(context.getString(R.string.failed_loginfirst));
                    onFailure();
                    break;
                case 801:
                    onFailure();
                    break;
                case 828:
                    //Project2.getAppInstance().showToast(context.getString(R.string.auth_err1));
                    //SproutAuth.SignOut(context);
                    onFailure();
                    break;
                case 999:
                    //Project2.getAppInstance().showToast(context.getString(R.string.failed_login_unknown));
                    onFailure();
                    break;
                default:
                    onResponse(response, Integer.valueOf(headers.get("err")));
                    break;
            }
        } else {
            onResponse(response, 0);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        t.printStackTrace();
        //Project2.getAppInstance().showToast(context.getString(R.string.failed_connection));
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